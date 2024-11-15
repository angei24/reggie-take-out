package com.itheima.controller;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.entity.ShoppingCart;
import com.itheima.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> addshoppingCart(@RequestBody ShoppingCart shoppingCart) {
        log.info(shoppingCart.toString());
        //获取当前用户的id
        Long userId = BaseContext.getCurrentThread();
        shoppingCart.setUserId(userId);
        ShoppingCart cart = shoppingCartService.addShoppingCart(shoppingCart);
        return R.success(cart);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> subshoppingCart(@RequestBody ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrentThread();
        shoppingCart.setUserId(userId);
        ShoppingCart cart = shoppingCartService.subShoppingCart(shoppingCart);
        return R.success(cart);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long userId = BaseContext.getCurrentThread();
        List<ShoppingCart> list = shoppingCartService.lambdaQuery()
                .eq(ShoppingCart::getUserId, userId)
                .orderByAsc(ShoppingCart::getCreateTime)
                .list();
        return R.success(list);
    }
    
    @DeleteMapping("/clean")
    public R<String> clean() {
        Long userId = BaseContext.getCurrentThread();
        shoppingCartService.cleanShoppingCart(userId);
        return R.success("购物车清空成功");
    }
}
