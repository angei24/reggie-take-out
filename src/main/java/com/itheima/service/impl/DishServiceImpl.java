package com.itheima.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.dto.DishDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.mapper.DishMapper;
import com.itheima.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void addDish(DishDto dishDto) {
        //保存菜品
        save(dishDto);
        Long id = dishDto.getId();
        //保存菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor f : flavors)
            f.setDishId(id);
        //静态工具保存菜品口味
        Db.saveBatch(flavors);
    }

    @Override
    public Page<DishDto> dishPage(Integer page, Integer pageSize, String name) {
        //创建分页对象
        Page<Dish> p = Page.of(page, pageSize);
        Page<DishDto> dishDto = new Page<>();
        //构建条件并分页查询
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        page(p, queryWrapper);
        //对象拷贝
        BeanUtil.copyProperties(p, dishDto, "records");
        //取出records使用分类id查询分类名称
        List<Dish> records = p.getRecords();
        List<DishDto> lists = records.stream().map((item)->{
            DishDto dto = new DishDto();
            BeanUtil.copyProperties(item, dto);
            Long categoryId = item.getCategoryId();
            Category category = Db.getById(categoryId, Category.class);
            String categoryName = category.getName();
            if (categoryName != null) {
                dto.setCategoryName(categoryName);
            }
            return dto;
        }).collect(Collectors.toList());
        dishDto.setRecords(lists);
        return dishDto;
    }

    @Override
    public DishDto getDishAndFlavor(Long id) {
        Dish dish = getById(id);
        DishDto dto = new DishDto();
        BeanUtil.copyProperties(dish, dto);
        List<DishFlavor> flavors = Db.lambdaQuery(DishFlavor.class).eq(DishFlavor::getDishId, id).list();
        dto.setFlavors(flavors);
        return dto;
    }

    @Override
    @Transactional
    public void updateDish(DishDto dishDto) {
        //更新菜品表
        updateById(dishDto);
        //更新菜品口味表
        Long id = dishDto.getId();
        //清除口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = Wrappers.lambdaQuery(DishFlavor.class).eq(DishFlavor::getDishId, id);
        Db.remove(queryWrapper);
        //重新插入口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor f : flavors)
            f.setDishId(id);
        Db.saveBatch(flavors);
    }

    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        //构建语句
        LambdaUpdateWrapper<Dish> wrapper = Wrappers.lambdaUpdate(Dish.class).set(Dish::getStatus, status).in(Dish::getId, ids);
        //执行查询
        update(wrapper);
    }

//    @Override
//    public List<Dish> dishList(Dish dish) {
//        Long categoryId = dish.getCategoryId();
//        LambdaQueryWrapper<Dish> wrapper = Wrappers.lambdaQuery(Dish.class);
//        wrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
//        wrapper.eq(Dish::getStatus, 1);
//        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        return list(wrapper);
//    }

    @Override
    public List<DishDto> dishList(Dish dish) {
        List<DishDto> lists = null;
        String key = "dishId_"+dish.getCategoryId()+"_"+dish.getStatus();
        //先从Redis中获取数据
        lists = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果存在，直接返回
        if (lists != null)
            return lists;
        Long categoryId = dish.getCategoryId();
        LambdaQueryWrapper<Dish> wrapper = Wrappers.lambdaQuery(Dish.class);
        wrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
        wrapper.eq(Dish::getStatus, 1);
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = list(wrapper);
        lists = list.stream().map((item)->{
            DishDto dto = new DishDto();
            BeanUtil.copyProperties(item, dto);
            Category category = Db.getById(item.getCategoryId(), Category.class);
            String categoryName = category.getName();
            if (categoryName != null) {
                dto.setCategoryName(categoryName);
            }
            Long id = item.getId();
            List<DishFlavor> flavors = Db.lambdaQuery(DishFlavor.class).eq(DishFlavor::getDishId, id).list();
            dto.setFlavors(flavors);
            return dto;
        }).collect(Collectors.toList());
        //不存在则查询数据库并添加到Redis中
        redisTemplate.opsForValue().set(key, lists, 1, TimeUnit.HOURS);
        return lists;
    }
}
