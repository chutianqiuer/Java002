package com.example.web.controller;

import com.example.common.vo.FileVO;
import com.example.common.vo.Result;
import com.example.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public Result<FileVO> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "dir", required = false) String dir) {
        return Result.success(fileService.upload(file, dir));
    }

    @GetMapping("/{id}")
    public Result<FileVO> getById(@PathVariable Long id) {
        return Result.success(fileService.getById(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return Result.success();
    }
}
