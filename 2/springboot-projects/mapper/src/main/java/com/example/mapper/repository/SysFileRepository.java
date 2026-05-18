package com.example.mapper.repository;

import com.example.common.entity.SysFile;
import com.example.mapper.SysFileMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SysFileRepository implements Repository<SysFile> {

    private final SysFileMapper sysFileMapper;

    public SysFileRepository(SysFileMapper sysFileMapper) {
        this.sysFileMapper = sysFileMapper;
    }

    @Override
    public BaseMapper<SysFile> getMapper() {
        return sysFileMapper;
    }
}
