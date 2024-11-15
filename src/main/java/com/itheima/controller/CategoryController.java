package com.itheima.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Category;
import com.itheima.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> addCategory(@RequestBody Category category) {
        log.info("新增分类{}", category);
        Category cate = categoryService.getByName(category);
        if (cate != null)
            return R.error("分类已存在");
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> categoryPage(Integer page, Integer pageSize) {
        log.info("page:{},pageSize:{}", page, pageSize);
        //分页查询
        Page<Category> p = categoryService.categoryPage(page, pageSize);
        return R.success(p);
    }

    @DeleteMapping
    public R<String> deleteCategory(Long ids) {
        log.info("删除分类ids:{}", ids);
        categoryService.removeCategory(ids);
//        categoryService.removeById(ids);
        return R.success("删除分类成功");
    }

    @PutMapping
    public R<String> updateCategory(@RequestBody Category category) {
        log.info("更新分类信息{}", category);
        categoryService.updateById(category);
        return R.success("分类信息修改成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        List<Category> list = categoryService.categoryList(category);
        return R.success(list);
    }
}
