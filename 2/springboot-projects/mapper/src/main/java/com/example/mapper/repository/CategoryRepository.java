package com.example.mapper.repository;

import com.example.common.entity.Category;
import com.example.mapper.CategoryMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepository implements Repository<Category> {

    private final CategoryMapper categoryMapper;

    public CategoryRepository(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public BaseMapper<Category> getMapper() {
        return categoryMapper;
    }
}
