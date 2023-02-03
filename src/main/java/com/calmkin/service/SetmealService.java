package com.calmkin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.calmkin.dto.SetmealDto;
import com.calmkin.pojo.Setmeal;
import org.springframework.transaction.annotation.Transactional;

public interface SetmealService extends IService<Setmeal> {

    @Transactional
    void saveWithDishes(SetmealDto setmealDto);
}
