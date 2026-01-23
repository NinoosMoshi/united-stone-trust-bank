package com.ninos.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.io.IOException;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;


    @Override
    public String uploadFile(MultipartFile file, String folderName) throws IOException, S3Exception {

        String originalFileName = file.getOriginalFilename();  // "example.png"
        String fileExtension = ""; // Will later store ".png"

        // Extract the file extension from the original filename.
        // originalFileName = "example.png"
        // fileExtension = ".png";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")); // ".png"
        }

        // Purpose: Create a new unique filename to avoid collisions
        // String newFileName = "a1b2c3d4-e5f6-7890-1234-56789abcdef0.png";
        String newFileName = UUID.randomUUID() + fileExtension;

        // Purpose: Define the path in S3 where the file will be stored.
        // String s3Key = "images/a1b2c3d4-e5f6-7890-1234-56789abcdef0.png";
        String s3Key = folderName + "/" + newFileName;

        // Build the S3 upload request.

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName) // "my-s3-bucket"
                .key(s3Key) // "images/a1b2c3d4-e5f6-7890-1234-56789abcdef0.png"
                .contentType(file.getContentType()) // "image/png"
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // Purpose: Return the URL of the uploaded file.
        // "https://my-s3-bucket.s3.amazonaws.com/images/a1b2c3d4-e5f6-7890-1234-56789abcdef0.png"
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(s3Key)).toString();
    }

    @Override
    public boolean deleteFile(String fileUrl) {  // fileUrl: https://mybucket.s3.amazonaws.com/profile-pictures/a1b2c3d4-e5f6-7890-1234-56789abcdef0.png
        try {
            // Purpose: Get the filename part from the URL.
            // String key = "a1b2c3d4-e5f6-7890-1234-56789abcdef0.png";
            String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key("profile-pictures/" + key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            return true;

        } catch (S3Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


}



