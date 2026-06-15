package com.project.ongojisik.global.storage;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    List<String> uploadImages(List<MultipartFile> files);
}
