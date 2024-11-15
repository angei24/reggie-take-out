package com.itheima.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submitOrder(Orders orders);

    Page<Orders> orderPage(Integer page, Integer pageSize, String number, String beginTime, String endTime);

    Page<Orders> userPage(Integer page, Integer pageSize);
}
