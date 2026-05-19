package com.example.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

@Data
@EqualsAndHashCode(callSuper = true)
public class FileDTO extends BaseDTO {
    private MultipartFile file;
    private String bucket;
    private String dir;
}
