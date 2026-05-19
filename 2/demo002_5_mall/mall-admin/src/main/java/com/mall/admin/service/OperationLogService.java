package com.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.entity.OperationLog;
import com.mall.admin.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationLogService extends ServiceImpl<OperationLogMapper, OperationLog> {

    public void logOperation(OperationLog log) {
        this.save(log);
    }
}
