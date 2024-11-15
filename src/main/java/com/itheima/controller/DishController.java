package com.itheima.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.entity.Dish;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto) {
        log.info("添加菜品{}", dishDto.toString());
        dishService.addDish(dishDto);
        //清理所有缓存菜品信息
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);
        //清理指定分类的菜品
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> dishPage(Integer page, Integer pageSize, String name) {
        Page<DishDto> p = dishService.dishPage(page, pageSize, name);
        return R.success(p);
    }

    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable Long id) {
        DishDto dto = dishService.getDishAndFlavor(id);
        return R.success(dto);
    }

    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto) {
        dishService.updateDish(dishDto);
        //清理所有缓存菜品信息
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);
        //清理指定分类的菜品
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("菜品更新成功");
    }

    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids) {
        log.info("删除菜品{}", ids.toString());
        dishService.removeByIds(ids);
        return R.success("菜品删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateDishStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("状态{},集合{}", status, ids.toString());
        dishService.updateStatus(status, ids);
        return R.success("更新成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        log.info("查询菜品分类{}", dish.getCategoryId());
        List<DishDto> list = dishService.dishList(dish);
        return R.success(list);
    }
}
