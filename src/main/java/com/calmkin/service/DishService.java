package com.calmkin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.calmkin.dto.DishDto;
import com.calmkin.pojo.Dish;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);
}
