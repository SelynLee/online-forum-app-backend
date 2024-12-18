package com.beaconfire.file_service;

import com.beaconfire.file_service.Controller.S3Controller;
import com.beaconfire.file_service.DTO.FileRequestResponse;
import com.beaconfire.file_service.Service.S3Service;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(S3Controller.class)
public class S3ControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockBean
   private S3Service s3Service;

   private final String SUCCESS_MESSAGE = "File successfully uploaded";
   private final String FAILURE_MESSAGE = "File could not be uploaded";

   @Test
   void testUpload() throws Exception {
      MockMultipartFile mockFile = new MockMultipartFile(
            "file", "test.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Test Content".getBytes());

      String fileURL = "http://s3-bucket-url/test.txt";

      Mockito.when(s3Service.uploadFile(Mockito.any())).thenReturn(fileURL);

      mockMvc.perform(multipart("/files/upload")
                  .file(mockFile))
            .andExpect(jsonPath("$.message").value(SUCCESS_MESSAGE))
            .andExpect(jsonPath("$.objectUrl").value(fileURL))
            .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()));
   }

   @Test
   void testUploadFail() throws Exception {
      MockMultipartFile mockFile = new MockMultipartFile(
            "file", "test.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Test Content".getBytes());

      Mockito.when(s3Service.uploadFile(Mockito.any())).thenThrow(new RuntimeException("S3 Error"));

      mockMvc.perform(multipart("/files/upload")
                  .file(mockFile))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value(FAILURE_MESSAGE))
            .andExpect(jsonPath("$.objectUrl").isEmpty());
   }

   @Test
   void testDownload() throws Exception {
      String fileContent = "Test Content";
      String contentType = "text/plain";
      String objectUrl = "http://s3-bucket-url/test.txt";

      FileRequestResponse mockResponse = FileRequestResponse.builder()
            .content(fileContent.getBytes())
            .contentType(contentType)
            .build();

      Mockito.when(s3Service.downloadFile(Mockito.any())).thenReturn(mockResponse);

      mockMvc.perform(get("/files/download")
                  .param("objectUrl", objectUrl))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(MockMvcResultMatchers.content().bytes(fileContent.getBytes()));
   }
}
