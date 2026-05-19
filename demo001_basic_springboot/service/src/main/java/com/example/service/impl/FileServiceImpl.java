package com.example.service.impl;

import com.example.common.entity.SysFile;
import com.example.common.enums.FileType;
import com.example.common.exception.BusinessException;
import com.example.common.utils.BeanCopyUtils;
import com.example.common.vo.FileVO;
import com.example.mapper.SysFileMapper;
import com.example.service.FileService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final SysFileMapper sysFileMapper;

    @Value("${file.upload-path:/tmp/uploads}")
    private String uploadPath;

    public FileServiceImpl(SysFileMapper sysFileMapper) {
        this.sysFileMapper = sysFileMapper;
    }

    @Override
    public FileVO upload(MultipartFile file, String dir) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fullDir = uploadPath + File.separator + (dir != null ? dir + File.separator : "") + datePath;

        File dirFile = new File(fullDir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        try {
            File targetFile = new File(fullDir, fileName);
            file.transferTo(targetFile);

            SysFile sysFile = new SysFile();
            sysFile.setFileName(fileName);
            sysFile.setOriginalName(originalFilename);
            sysFile.setPath(targetFile.getAbsolutePath());
            sysFile.setUrl("/files/" + datePath + "/" + fileName);
            sysFile.setFileSize(file.getSize());
            sysFile.setExtension(extension);
            sysFile.setMimeType(file.getContentType());
            sysFile.setFileType(determineFileType(extension));

            sysFileMapper.insert(sysFile);

            return BeanCopyUtils.copyBean(sysFile, FileVO.class);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        SysFile sysFile = sysFileMapper.selectById(id);
        if (sysFile == null) {
            throw new BusinessException("文件不存在");
        }

        File physicalFile = new File(sysFile.getPath());
        if (physicalFile.exists()) {
            physicalFile.delete();
        }

        sysFileMapper.deleteById(id);
    }

    @Override
    public FileVO getById(Long id) {
        SysFile sysFile = sysFileMapper.selectById(id);
        if (sysFile == null) {
            throw new BusinessException("文件不存在");
        }
        return BeanCopyUtils.copyBean(sysFile, FileVO.class);
    }

    private FileType determineFileType(String extension) {
        if (extension == null) {
            return FileType.OTHER;
        }
        extension = extension.toLowerCase();

        if ("jpg,jpeg,png,gif,bmp,webp,svg".contains(extension)) {
            return FileType.IMAGE;
        } else if ("mp4,avi,mov,wmv,flv,mkv".contains(extension)) {
            return FileType.VIDEO;
        } else if ("mp3,wav,flac,aac,ogg".contains(extension)) {
            return FileType.AUDIO;
        } else if ("pdf,doc,docx,xls,xlsx,ppt,pptx,txt".contains(extension)) {
            return FileType.DOCUMENT;
        }
        return FileType.OTHER;
    }
}
