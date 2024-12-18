package com.beaconfire.file_service;

import com.beaconfire.file_service.DTO.FileRequestResponse;
import com.beaconfire.file_service.Service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@TestPropertySource(properties = {
//      "aws.s3.bucket-name=test-bucket"
//})

public class S3ServiceTest {

   @Mock
   private S3Client s3Client;

   @InjectMocks
   private S3Service s3Service;

   @BeforeEach
   public void setUp() throws Exception {
      MockitoAnnotations.openMocks(this);

      Field bucketNameField = S3Service.class.getDeclaredField("bucketName");
      bucketNameField.setAccessible(true);
      bucketNameField.set(s3Service, "test-bucket");
   }

   @Test
   void testUpload() throws Exception {
      MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "Test Content".getBytes(StandardCharsets.UTF_8)
      );

      String uniqueKey = "unique-key.txt";
      String bucketName = "test-bucket";
      String expectedUrl = "http://s3-bucket-url/" + uniqueKey;

      S3Utilities mockS3Utilities = mock(S3Utilities.class);
      when(s3Client.utilities()).thenReturn(mockS3Utilities);
      when(mockS3Utilities.getUrl(any(GetUrlRequest.class))).thenReturn(new URL(expectedUrl));

      // Mock S3Client behavior
      when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(null);

      // Act
      String result = s3Service.uploadFile(mockFile);

      // Assert
      assertEquals(expectedUrl, result);
      verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
      verify(s3Client.utilities(), times(1)).getUrl(any(GetUrlRequest.class));
   }

   @Test
   void testDownloadFile_Success() {
      // Arrange
      String objectUrl = "http://s3-bucket-url/unique-key.txt";
      String bucketName = "test-bucket";
      String uniqueKey = "unique-key.txt";
      byte[] fileContent = "Test Content".getBytes(StandardCharsets.UTF_8);
      String contentType = "text/plain";

      // Mock S3Client behavior
      ResponseBytes<GetObjectResponse> mockResponse = mock(ResponseBytes.class);
      when(mockResponse.asByteArray()).thenReturn(fileContent);

      GetObjectResponse mockMetadata = GetObjectResponse.builder()
            .contentType(contentType)
            .build();
      when(mockResponse.response()).thenReturn(mockMetadata);

      when(s3Client.getObject(any(GetObjectRequest.class), any(ResponseTransformer.class)))
            .thenReturn(mockResponse);

      // Act
      FileRequestResponse result = s3Service.downloadFile(objectUrl);

      // Assert
      assertNotNull(result);
      assertArrayEquals(fileContent, result.getContent());
      assertEquals(contentType, result.getContentType());
      verify(s3Client, times(1)).getObject(any(GetObjectRequest.class), any(ResponseTransformer.class));
   }
}
