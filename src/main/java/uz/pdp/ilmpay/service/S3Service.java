package uz.pdp.ilmpay.service;

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
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    
    private final S3Client s3Client;
    
    @Value("${aws.s3.bucket}")
    private String bucketName;
    
    @Value("${aws.s3.region}")
    private String region;
    
    /**
     * 📁 Upload file to S3
     * @param file The file to upload
     * @param folder The folder path in S3 (e.g., "support-logos")
     * @return The S3 URL of the uploaded file
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = generateFileName(file);
            String key = folder + "/" + fileName;
            
            log.debug("🎯 Attempting to upload file: {}", fileName);
            log.debug("📦 File details - Size: {} bytes, Type: {}", file.getSize(), file.getContentType());
            
            // 🚀 Upload to S3 without ACL (let bucket policy handle public access)
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    // Remove ACL setting to avoid permission issues
                    .build();
            
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            // 🔗 Generate S3 URL with regional endpoint
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
            log.info("✨ File uploaded successfully to S3: {}", fileUrl);
            return fileUrl;
            
        } catch (S3Exception e) {
            log.error("❌ S3 operation failed - Status: {}, Error: {}", e.statusCode(), e.getMessage());
            if (e.statusCode() == 403) {
                log.error("🔐 Access denied. IAM user lacks required permissions.");
                throw new RuntimeException("Unable to upload file. Please check S3 bucket permissions or contact administrator.");
            }
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage());
        } catch (IOException e) {
            log.error("📄 Failed to read file contents", e);
            throw new RuntimeException("Failed to read file contents: " + e.getMessage());
        } catch (Exception e) {
            log.error("💥 Unexpected error while uploading file", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }
    
    /**
     * 🗑️ Delete file from S3
     * @param fileUrl The S3 URL of the file to delete
     */
    public void deleteFile(String fileUrl) {
        try {
            String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);
            log.debug("🎯 Attempting to delete file with key: {}", key);
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            log.info("✨ File deleted successfully from S3: {}", fileUrl);
            
        } catch (S3Exception e) {
            log.error("❌ S3 delete operation failed - Status: {}, Error: {}", e.statusCode(), e.getMessage());
            throw new RuntimeException("Failed to delete file from S3: " + e.getMessage());
        } catch (Exception e) {
            log.error("💥 Unexpected error while deleting file", e);
            throw new RuntimeException("Failed to delete file from S3: " + e.getMessage());
        }
    }
    
    /**
     * 🎨 Generate unique filename with original extension
     * @param file The uploaded file
     * @return A unique filename with the original extension
     */
    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        return UUID.randomUUID().toString() + extension;
    }
}
