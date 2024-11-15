package com.itheima.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.DishDto;
import com.itheima.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    void addDish(DishDto dishDto);

    Page<DishDto> dishPage(Integer page, Integer pageSize, String name);

    DishDto getDishAndFlavor(Long id);

    void updateDish(DishDto dishDto);

    void updateStatus(Integer status, List<Long> ids);

    List<DishDto> dishList(Dish dish);
}
