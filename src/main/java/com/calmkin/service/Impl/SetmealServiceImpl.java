package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.dto.SetmealDto;
import com.calmkin.mapper.SetmealMapper;
import com.calmkin.pojo.Setmeal;
import com.calmkin.pojo.SetmealDish;
import com.calmkin.service.SetmealDishService;
import com.calmkin.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDishes(SetmealDto setmealDto) {

        //先保存套餐的信息
        this.save(setmealDto);
        Long setMealId = setmealDto.getId();

        List<SetmealDish> dishes = setmealDto.getSetmealDishes();

        dishes=dishes.stream().map((item)->{
            item.setSetmealId(setMealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);
    }
}
