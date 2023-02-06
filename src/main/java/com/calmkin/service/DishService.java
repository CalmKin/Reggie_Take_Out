package com.calmkin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.calmkin.dto.DishDto;
import com.calmkin.pojo.Dish;
import com.calmkin.pojo.Setmeal;
import com.calmkin.pojo.SetmealDish;

import java.util.List;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);

    public DishDto get_with_id(Long id);

    public void update_with_flavor(DishDto dishDto);

    public boolean changeBatchStatusByIds(int targetStatus, List<Long> ids);

    public boolean deldeteBatchs(List<Long> ids);

    public  boolean check(Dish dish);
}
