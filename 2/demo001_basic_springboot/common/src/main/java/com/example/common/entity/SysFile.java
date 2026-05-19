package com.example.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.enums.FileType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseEntity {
    private String fileName;
    private String originalName;
    private String url;
    private String path;
    private Long fileSize;
    private FileType fileType;
    private String mimeType;
    private String extension;
}
