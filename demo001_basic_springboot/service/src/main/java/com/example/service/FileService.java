package com.example.service;

import com.example.common.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileVO upload(MultipartFile file, String dir);

    void delete(Long id);

    FileVO getById(Long id);
}
