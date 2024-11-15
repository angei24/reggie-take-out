package com.itheima.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    //元数据处理器，设置create和update字段
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("新增数据填充[insert]...");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentThread());
        metaObject.setValue("updateUser", BaseContext.getCurrentThread());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新数据填充[update]...");
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentThread());
    }
}
