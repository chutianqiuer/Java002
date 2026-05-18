package com.example.mapper.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.BaseEntity;

import java.util.List;

public interface Repository<T extends BaseEntity> {

    BaseMapper<T> getMapper();

    default T getById(Long id) {
        return getMapper().selectById(id);
    }

    default List<T> getAll() {
        return getMapper().selectList(null);
    }

    default Long insert(T entity) {
        getMapper().insert(entity);
        return entity.getId();
    }

    default void update(T entity) {
        getMapper().updateById(entity);
    }

    default void deleteById(Long id) {
        getMapper().deleteById(id);
    }

    default void deleteByIds(List<Long> ids) {
        getMapper().deleteBatchIds(ids);
    }
}
