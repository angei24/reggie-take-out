package com.itheima.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.common.BaseContext;
import com.itheima.common.CustomException;
import com.itheima.entity.*;
import com.itheima.mapper.OrderMapper;
import com.itheima.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Override
    @Transactional
    public void submitOrder(Orders orders) {
        //获取用户id
        Long userId = BaseContext.getCurrentThread();
        //查询用户的购物车
        LambdaQueryWrapper<ShoppingCart> wrapper = Wrappers.lambdaQuery(ShoppingCart.class).eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> list = Db.list(wrapper);
        if (list == null || list.size() == 0)
            throw new CustomException("购物车为空，不能下单");
        //查询用户数据
        User user = Db.getById(userId, User.class);
        //查询地址信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = Db.getById(addressBookId, AddressBook.class);
        if (addressBook == null)
            throw new CustomException("默认地址不能为空");

        long id = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            BeanUtil.copyProperties(item, orderDetail, "id","userId","createTime");
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).toList();

        orders.setId(id);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(id));
//        orders.setUserName(user.getName());
        orders.setUserName("默认名称");
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //订单表中插入一条数据
        save(orders);
        //订单详细表中插入多条数据
        Db.saveBatch(orderDetails);
        //清空用户购物车
        Db.remove(wrapper);
    }

    @Override
    public Page<Orders> orderPage(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        Page<Orders> p = Page.of(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = Wrappers.lambdaQuery();
        wrapper.like(number != null, Orders::getNumber, number);
        wrapper.ge(beginTime != null, Orders::getOrderTime, beginTime);
        wrapper.le(endTime != null, Orders::getOrderTime, endTime);
        page(p, wrapper);
        return p;
    }

    @Override
    public Page<Orders> userPage(Integer page, Integer pageSize) {
        Page<Orders> p = Page.of(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Orders::getUserId, BaseContext.getCurrentThread());
        wrapper.orderByDesc(Orders::getOrderTime);
        page(p, wrapper);
        return p;
    }
}
