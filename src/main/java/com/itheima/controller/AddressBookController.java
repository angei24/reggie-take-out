package com.itheima.controller;

import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.entity.AddressBook;
import com.itheima.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    //新增
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentThread());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    //设置默认地址
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        Long userId = BaseContext.getCurrentThread();
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.lambdaUpdate().eq(AddressBook::getUserId, userId).set(AddressBook::getIsDefault, 0).update();
        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    //根据id查询地址
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null)
            return R.success(addressBook);
        else
            return R.error("没有找到该对象");
    }

    //修改地址信息
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success("地址更新成功");
    }

    //查询默认地址
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        Long userId = BaseContext.getCurrentThread();
        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.lambdaQuery()
                .eq(AddressBook::getUserId, userId)
                .eq(AddressBook::getIsDefault, 1)
                .one();
        if (null == addressBook)
            return R.error("没有找到该对象");
        else
            return R.success(addressBook);
    }

    //查询指定用户的全部地址
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentThread());
        log.info("addressBook:{}", addressBook);
        //条件构造器
        List<AddressBook> list = addressBookService.lambdaQuery()
                .eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId())
                .orderByDesc(AddressBook::getUpdateTime)
                .list();
        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(list);
    }

    //删除地址信息
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids) {
        log.info("ids:{}", ids);
        addressBookService.removeById(ids);
        return R.success("删除地址成功");
    }
}
