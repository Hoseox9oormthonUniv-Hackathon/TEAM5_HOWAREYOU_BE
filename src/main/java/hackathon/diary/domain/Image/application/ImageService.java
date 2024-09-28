package hackathon.diary.domain.Image.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private static final String S3_BASE_URL_FORMAT = "https://%s.s3.ap-northeast-2.amazonaws.com/";

    // 단일 이미지 업로드
    public String uploadImage(MultipartFile file) throws IOException {
        log.info("Validating file: {}", file.getOriginalFilename());

        if (!validateFile(file)) {
            log.info("Image validation failed for file: {}", file.getOriginalFilename());
            return null;
        }

        String fileName = generateFileName(file);
        log.info("Generated file name: {}", fileName);

        String imageUrl = uploadToS3(file, fileName);
        log.info("Image uploaded: {}", imageUrl);
        return imageUrl;
    }

    private String uploadToS3(MultipartFile file, String fileName) throws IOException {
        String imageUrl = String.format(S3_BASE_URL_FORMAT, bucket) + fileName;
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), getObjectMetadata(file)));
        return imageUrl;
    }

    public void deleteImage(String imageUrl) {
        String fileName = extractFileNameFromUrl(imageUrl);
        amazonS3.deleteObject(bucket, fileName);
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        return metadata;
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID() + "-" + file.getOriginalFilename();
    }

    private String extractFileNameFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

    private boolean validateFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }
}
