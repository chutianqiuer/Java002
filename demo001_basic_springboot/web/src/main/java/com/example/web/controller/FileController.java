package com.example.web.controller;

import com.example.common.vo.FileVO;
import com.example.common.vo.Result;
import com.example.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
@Tag(name = "文件管理", description = "文件上传、下载、删除")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件到服务器，支持指定目录")
    public Result<FileVO> upload(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "目录") @RequestParam(value = "dir", required = false) String dir) {
        return Result.success(fileService.upload(file, dir));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文件信息", description = "根据ID获取文件信息")
    public Result<FileVO> getById(
            @Parameter(description = "文件ID") @PathVariable Long id) {
        return Result.success(fileService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件", description = "根据ID删除文件")
    public Result<Void> delete(
            @Parameter(description = "文件ID") @PathVariable Long id) {
        fileService.delete(id);
        return Result.success();
    }
}
