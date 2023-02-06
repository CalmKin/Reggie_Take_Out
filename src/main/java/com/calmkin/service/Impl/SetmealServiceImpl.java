package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.common.CustomException;
import com.calmkin.dto.SetmealDto;
import com.calmkin.mapper.SetmealMapper;
import com.calmkin.pojo.Setmeal;
import com.calmkin.pojo.SetmealDish;
import com.calmkin.service.SetmealDishService;
import com.calmkin.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

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

    /**
     * 根据id进行套餐的删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {

        //先判断套餐是否起售，用in操作(如果这些id里面有套餐是正在起售的，抛出业务异常)
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        lambdaQueryWrapper.in(Setmeal::getId,ids);

        int count = this.count(lambdaQueryWrapper);
        //如果套餐正在起售，则抛出业务异常
        if(count>0)
        {
            throw new CustomException("当前选中套餐无法删除");
        }
        //否则先删除套餐表里面对应的信息
        this.removeByIds(ids);

        //然后再删除菜品映射关系里面的信息,不能直接调用deleteByIds
        LambdaQueryWrapper<SetmealDish> lqw=  new LambdaQueryWrapper<>();
        lqw.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lqw);
    }


}
