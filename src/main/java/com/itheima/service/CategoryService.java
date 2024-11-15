package com.itheima.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    Category getByName(Category category);

    Page<Category> categoryPage(Integer page, Integer pageSize);

    void removeCategory(Long ids);

    List<Category> categoryList(Category category);
}
