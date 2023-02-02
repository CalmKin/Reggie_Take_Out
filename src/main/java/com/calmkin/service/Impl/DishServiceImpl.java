package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.common.R;
import com.calmkin.dto.DishDto;
import com.calmkin.mapper.DishMapper;
import com.calmkin.pojo.Dish;
import com.calmkin.pojo.DishFlavor;
import com.calmkin.service.DishFlavorService;
import com.calmkin.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional      //涉及多表操作，需要开启事务
    public void saveWithFlavor(DishDto dishDto) {
        //因为dishdto继承了dish（dish的字段全都有），所以可以直接用dishdto插入到表中
        this.save(dishDto);

        Long id = dishDto.getId();  //这里可以直接获取插入之后生成的id（具体为什么我也不清楚）

        List<DishFlavor> flavors = dishDto.getFlavors();
        //但是插入到dish_flavor之前，还需要设置一下口味对应的dish_id
        for(int i=0;i<flavors.size();i++)
        {
            flavors.get(i).setDishId(id);
        }


        dishFlavorService.saveBatch(flavors);
    }
}
