package com.project.ongojisik.global.storage;

import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3ImageStorageService implements ImageStorageService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final S3Client s3Client;

    @Value("${app.s3.bucket-name:}")
    private String bucketName;

    @Override
    public List<String> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new APIException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return files.stream()
                .map(this::uploadImage)
                .toList();
    }

    private String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new APIException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!StringUtils.hasText(bucketName)) {
            throw new APIException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + (extension == null ? "" : "." + extension);
        String key = "board-images/" + LocalDate.now().format(DATE_FORMATTER) + "/" + fileName;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            URL url = s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return url.toExternalForm();
        } catch (IOException exception) {
            throw new APIException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
