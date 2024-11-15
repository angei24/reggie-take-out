package com.itheima.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void addSetmeal(SetmealDto setmealDto);

    Page<SetmealDto> setmealPage(Integer page, Integer pageSize, String name);

    void deleteSetmael(List<Long> ids);

    void updateStatus(Integer status, List<Long> ids);

    SetmealDto getSetmealAndDish(Long id);

    void updateSetmeal(SetmealDto setmealDto);

    List<Setmeal> setmealList(Setmeal setmeal);
}
