package com.beaconfire.file_service.Controller;

import com.beaconfire.file_service.DTO.FileUploadResponse;
import com.beaconfire.file_service.Service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

@RestController
@RequestMapping("/files")
public class S3Controller {
   private final String SUCCESS_MESSAGE = "File successfully uploaded";
   private final String FAILURE_MESSAGE = "File could not be uploaded";

   private final S3Service s3Service;

   @Autowired
   public S3Controller(S3Service s3Service) {
      this.s3Service = s3Service;
   }

   @PostMapping("/upload")
   public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
      try{
         String url = s3Service.uploadFile(file);
         FileUploadResponse response = FileUploadResponse.builder()
               .message(SUCCESS_MESSAGE)
               .objectUrl(url)
               .build();
         return ResponseEntity.ok(response);
      }
      catch(Exception e){
         FileUploadResponse errorResponse = FileUploadResponse.builder()
               .message(FAILURE_MESSAGE)
               .objectUrl(null)
               .build();
         return ResponseEntity.status(500).body(errorResponse);
      }
   }
}
