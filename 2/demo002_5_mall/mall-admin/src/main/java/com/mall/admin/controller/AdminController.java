package com.mall.admin.controller;

import com.mall.admin.service.OperationLogService;
import com.mall.common.entity.OperationLog;
import com.mall.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OperationLogService operationLogService;

    @PostMapping("/operation-log")
    public ApiResponse<Void> logOperation(@RequestBody OperationLog log) {
        operationLogService.logOperation(log);
        return ApiResponse.success();
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("OK");
    }
}
