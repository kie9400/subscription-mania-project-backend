package com.springboot.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
public class S3StorageService implements StorageService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public S3StorageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String store(MultipartFile file, String fileNameWithoutExt) {
        try {
            String extension = getExtension(file.getOriginalFilename());
            //S3 key값을 "/"를 제거하고 넣는다. ("/"폴더 생성 방지)
            String fileName = fileNameWithoutExt.replaceFirst("^/+", "") + "." + extension;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata);
            amazonS3.putObject(putObjectRequest);

            //S3에 저장할땐 fileName
            //db에 저장할땐 /fileName
            return "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }
    }

    @Override
    public void delete(String relativePath) {
        amazonS3.deleteObject(bucket, relativePath);
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > -1 ? fileName.substring(dotIndex + 1).toLowerCase() : "";
    }
}