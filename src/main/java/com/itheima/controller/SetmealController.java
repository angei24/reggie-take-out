package com.itheima.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Setmeal;
import com.itheima.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    public R<String> addSetmeal(@RequestBody SetmealDto setmealDto) {
        log.info(setmealDto.toString());
        setmealService.addSetmeal(setmealDto);
        return R.success("添加套餐成功");
    }

    @GetMapping("/page")
    public R<Page> setmealPage(Integer page, Integer pageSize, String name) {
        log.info("page:{},pageSize:{},name:{}", page, pageSize, name);
        Page<SetmealDto> p = setmealService.setmealPage(page, pageSize, name);
        return R.success(p);
    }

    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam List<Long> ids) {
        log.info("删除ids:{}", ids);
        setmealService.deleteSetmael(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("状态{},ids{}", status, ids);
        setmealService.updateStatus(status, ids);
        return R.success("套餐状态更新成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealById(@PathVariable Long id) {
        SetmealDto dto = setmealService.getSetmealAndDish(id);
        return R.success(dto);
    }

    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto) {
        setmealService.updateSetmeal(setmealDto);
        return R.success("套餐更新成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        List<Setmeal> list = setmealService.setmealList(setmeal);
        return R.success(list);
    }

//    @GetMapping("/dish/{id}")
//    public R<SetmealDto> getSetmealDto(@PathVariable Long id) {
//        SetmealDto dto = setmealService.getSetmealAndDish(id);
//        return R.success(dto);
//    }
}
