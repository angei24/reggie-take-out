package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    ShoppingCart addShoppingCart(ShoppingCart shoppingCart);

    void cleanShoppingCart(Long userId);

    ShoppingCart subShoppingCart(ShoppingCart shoppingCart);
}
