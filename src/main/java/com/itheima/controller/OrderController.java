package com.itheima.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Orders;
import com.itheima.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info(orders.toString());
        orderService.submitOrder(orders);
        return R.success("订单提交成功");
    }

    @GetMapping("/page")
    public R<Page> orderPage(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        log.info("page:{},pageSize:{},number:{},beginTime:{},endTime:{}", page, pageSize, number, beginTime, endTime);
        Page<Orders> p = orderService.orderPage(page, pageSize, number, beginTime, endTime);
        return R.success(p);
    }

    @GetMapping("/userPage")
    public R<Page> userPage(Integer page, Integer pageSize) {
        log.info("page:{},pageSize:{}", page, pageSize);
        Page<Orders> p = orderService.userPage(page, pageSize);
        return R.success(p);
    }

    @PutMapping
    public R<String> orderStatus(@RequestBody Orders orders) {
        log.info(orders.toString());
        orderService.updateById(orders);
        return R.success("订单状态更新成功");
    }
}
