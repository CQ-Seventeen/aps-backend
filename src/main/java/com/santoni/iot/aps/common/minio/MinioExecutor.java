package com.santoni.iot.aps.common.minio;

import com.santoni.iot.aps.common.config.MinioProperties;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.Random;

@Slf4j
@Component
public class MinioExecutor {

    @Autowired
    private MinioProperties minioProperties;

    // client
    private MinioClient minioClient;

    @PostConstruct
    private void clientInit() {
        minioClient = MinioClient.builder()
                .endpoint(minioProperties.getAddress())
                .credentials(minioProperties.getUser(), minioProperties.getPassword())
                .build();
    }

    public String uploadFile(MultipartFile file, String bucketName, boolean isPrivate) {
        try {
            File temp = File.createTempFile("upload", "tmp");
            String originName = file.getOriginalFilename();
            file.transferTo(temp);
            String filename = createPrefix(originName, isPrivate);
            upload(temp, bucketName, filename);
            return minioProperties.getDownload() + "/" + bucketName + "/" + filename;
        } catch (Exception e) {
            log.error("Upload file error, bucket:{}", bucketName, e);
            return null;
        }
    }

    public String uploadFile(MultipartFile file, boolean isPrivate) {
        return uploadFile(file, minioProperties.getOpenBucket(), isPrivate);
    }

    public String uploadFile(File file, String originName, boolean isPrivate) {
        String filename = createPrefix(originName, isPrivate);
        upload(file, minioProperties.getOpenBucket(), filename);
        return minioProperties.getDownload() + "/" + minioProperties.getOpenBucket() + "/" + filename;
    }

    public File downloadToLocal(String url) {
        try {
            String[] parts = url.split("/");
            String bucket = parts[3];
            String object = parts[4];
            File dir = new File("/home/tempFiles");
            if(!dir.exists() && !dir.mkdir()) {
                log.error("Create tempFiles directory failed");
                return null;
            }
            File temp = new File("/home/tempFiles/" + createPrefix(object, false));
            minioClient.downloadObject(DownloadObjectArgs.builder()
                    .bucket(bucket)
                    .object(object)
                    .filename(temp.getPath())
                    .build());
            return temp;
        } catch (Exception e) {
            log.error("Error happened when download file: {}", url);
        }
        return null;
    }

    public void deleteFile(String url) {
        try {
            URI uri = new URI(url);
            String bucket = uri.getPath().split("/")[1];
            String object = uri.getPath().split("/")[2];
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("Delete file error", e);

        }
    }

    private static String createPrefix(String originName, boolean isPrivate) {
        long stamp = new Date().getTime() * 100 + new Random().nextInt(99);
        String prefix = isPrivate ? "S" : "O";
        return prefix + Long.toString(stamp, 36).toUpperCase() + "-" + originName;
    }

    private void upload(File file, String bucketName, String filename) {
        try {
            minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .filename(file.getPath())
                    .build());
        } catch (Exception e) {
            log.error("Error happened when upload file: {} to bucket: {}", filename, bucketName, e);
        }
    }

    public String createDownloadUrl(String fileUrl, int expire) {
        try {
            URI uri = new URI(fileUrl);
            String bucket = uri.getPath().split("/")[1];
            String object = uri.getPath().split("/")[2];
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(minioProperties.getDownload().replace("https", "http"))
                    .credentials(minioProperties.getUser(), minioProperties.getPassword())
                    .build();
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(object)
                    .expiry(expire)
                    .build());
            return url.replace("%2F", "/");
        } catch (Exception e) {
            log.error("Create file downloadUrl error, file:{}", fileUrl, e);
            return null;
        }
    }
}
