package com.example.common.vo;

import com.example.common.enums.FileType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class FileVO extends BaseVO {
    private String fileName;
    private String originalName;
    private String url;
    private Long fileSize;
    private FileType fileType;
    private String mimeType;
    private String extension;
    private LocalDateTime createTime;
}
