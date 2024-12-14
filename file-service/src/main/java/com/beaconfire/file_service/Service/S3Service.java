package com.beaconfire.file_service.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

   private final S3Client s3Client;

   @Value("${aws.s3.bucket-name}")
   private String bucketName;

   @Autowired
   public S3Service(S3Client s3Client) {
      this.s3Client = s3Client;
   }

   /**
    * Upload a new image to AWS S3 bucket.
    * Example of file path included in the request body: "file=@path/to/your/file.jpg"
    * @param file the path of the file
    */
   public String uploadFile(MultipartFile file) {
      try{
         String uniqueKey = generateUniqueKey(file.getOriginalFilename());
         s3Client.putObject(PutObjectRequest.builder()
                     .bucket(bucketName)
                     .key(uniqueKey)
                     .contentType(file.getContentType()!=null?file.getContentType():"application/octet-stream")
                     .build(),
               software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

         return s3Client.utilities().getUrl(GetUrlRequest.builder()
                     .bucket(bucketName)
                     .key(uniqueKey)
                     .build()).toString();
      }
      catch(IOException e){
         throw new RuntimeException("Error uploading file",e);
      }

   }

//   /**
//    *
//    * @param objectUrl the url specific for files you want to retrieve
//    * @return file in S3 bucket in byte array
//    */
//   public byte[] downloadFile(String objectUrl) {
//      String key = objectUrl.substring(objectUrl.lastIndexOf("/")+1);
//
//   }

   private String generateUniqueKey(String fileName) {
      String uuid = UUID.randomUUID().toString();

      //Remain the file extension for retrieval recognition
      String extension = ".bin";

      if(fileName!=null && !fileName.isEmpty()) {
         int dotIndex = fileName.lastIndexOf(".");
         if(dotIndex >= 0) {extension = fileName.substring(dotIndex);}
      }

      return uuid + extension;
   }
}
