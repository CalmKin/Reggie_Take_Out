package com.calmkin.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 这里的形参就是我们进行修改或者更新操作时提交的对象
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始执行插入操作");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getID());
        metaObject.setValue("updateUser",BaseContext.getID());

    }

    @Override
    public void updateFill(MetaObject metaObject) {

//        long id = Thread.currentThread().getId();
//        log.info("当前线程ID为{}",id);
        metaObject.setValue("updateUser",BaseContext.getID());
        metaObject.setValue("updateTime",LocalDateTime.now());
    }
}
