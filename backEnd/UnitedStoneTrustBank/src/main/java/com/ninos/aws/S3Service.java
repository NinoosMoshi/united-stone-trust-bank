package com.ninos.aws;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

public interface S3Service {

    String uploadFile(MultipartFile file, String folderName) throws IOException, S3Exception;

    boolean deleteFile(String fileUrl);

}
