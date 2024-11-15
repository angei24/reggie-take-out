package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.common.CustomException;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.Setmeal;
import com.itheima.mapper.CategoryMapper;
import com.itheima.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Override
    public Category getByName(Category category) {
        return lambdaQuery().eq(Category::getName, category.getName()).one();
    }

    @Override
    public Page<Category> categoryPage(Integer page, Integer pageSize) {
        //创建分页对象
        Page<Category> p = Page.of(page, pageSize);
        //构建分页条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        //执行分页查询
        page(p, queryWrapper);
        return p;
    }

    @Override
    public void removeCategory(Long ids) {
        //查询当前分类是否关联菜品
        Long dish = Db.lambdaQuery(Dish.class).eq(Dish::getCategoryId, ids).count();
        if (dish > 0) {
            throw new CustomException("当前分类关联了菜品，不能删除");
        }
        //查询当前分类是否关联套餐
        Long setmeal = Db.lambdaQuery(Setmeal.class).eq(Setmeal::getCategoryId, ids).count();
        if (setmeal > 0) {
            throw new CustomException("当前菜品关联了套餐，不能删除");
        }
        //没有关联，则删除分类
        removeById(ids);
    }

    @Override
    public List<Category> categoryList(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        return list(queryWrapper);
    }
}
