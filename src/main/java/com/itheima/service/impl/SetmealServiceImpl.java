package com.itheima.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.common.CustomException;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Category;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDish;
import com.itheima.mapper.SetmealMapper;
import com.itheima.service.SetmealService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Override
    @Transactional
    @CacheEvict(value = "setmealCacha", allEntries = true)
    public void addSetmeal(SetmealDto setmealDto) {
        //保存套餐基本信息setmeal
        save(setmealDto);
        //保存套餐菜品关系表setmeal_dish
        Long id = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //设置套餐菜品的菜品id
        for (SetmealDish dish : setmealDishes)
            dish.setSetmealId(id);
        Db.saveBatch(setmealDishes);
    }

    @Override
    public Page<SetmealDto> setmealPage(Integer page, Integer pageSize, String name) {
        //创建分页对象
        Page<Setmeal> setmeal = Page.of(page, pageSize);
        Page<SetmealDto> dto = new Page<>();
        //分页查询
        LambdaQueryWrapper<Setmeal> wrapper = Wrappers.lambdaQuery(Setmeal.class);
        wrapper.like(name != null, Setmeal::getName, name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        page(setmeal, wrapper);
        //对象拷贝
        BeanUtil.copyProperties(setmeal, dto, "records");
        //取出records使用setmeal的id查询分类
        List<Setmeal> records = setmeal.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtil.copyProperties(item, setmealDto);
            Long id = item.getCategoryId();
            Category category = Db.getById(id, Category.class);
            String categoryName = category.getName();
            if (categoryName != null)
                setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).toList();
        dto.setRecords(list);
        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = "setmealCache", allEntries = true)
    public void deleteSetmael(List<Long> ids) {
        //查询套餐状态，判断是否可删除
        LambdaQueryWrapper<Setmeal> query = Wrappers.lambdaQuery(Setmeal.class);
        query.in(Setmeal::getId, ids);
        query.eq(Setmeal::getStatus, 1);
        //售卖中不能删除
        int i = (int) count(query);
        if (i > 0)
            throw new CustomException("有正在售卖中的套餐，不能删除");
        //删除套餐表的信息
        removeByIds(ids);
        //删除套餐菜品表的信息
        LambdaQueryWrapper<SetmealDish> wrapper = Wrappers.lambdaQuery(SetmealDish.class);
        wrapper.in(SetmealDish::getSetmealId, ids);
        Db.remove(wrapper);
    }

    @Override
    @CacheEvict(value = "setmealCache", allEntries = true)
    public void updateStatus(Integer status, List<Long> ids) {
        LambdaUpdateWrapper<Setmeal> wrapper = Wrappers.lambdaUpdate(Setmeal.class);
        wrapper.set(Setmeal::getStatus, status);
        wrapper.in(Setmeal::getId, ids);
        update(wrapper);
    }

    @Override
    public SetmealDto getSetmealAndDish(Long id) {
        Setmeal setmeal = getById(id);
        SetmealDto dto = new SetmealDto();
        BeanUtil.copyProperties(setmeal, dto);
        List<SetmealDish> list = Db.lambdaQuery(SetmealDish.class).eq(SetmealDish::getSetmealId, id).list();
        dto.setSetmealDishes(list);
        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = "setmealCache", allEntries = true)
    public void updateSetmeal(SetmealDto setmealDto) {
        //更新套餐表
        updateById(setmealDto);
        //更新套餐菜品表
        //清除套餐中的菜品信息
        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> wrapper = Wrappers.lambdaQuery(SetmealDish.class).eq(SetmealDish::getSetmealId, id);
        Db.remove(wrapper);
        //重新插入套餐菜品信息
        List<SetmealDish> lists = setmealDto.getSetmealDishes();
        for (SetmealDish dish : lists)
            dish.setSetmealId(id);
        Db.saveBatch(lists);
    }

    @Override
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    public List<Setmeal> setmealList(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> wrapper = Wrappers.lambdaQuery(Setmeal.class)
                .eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(Setmeal::getStatus, 1)
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = list(wrapper);
        return list;
    }
}
