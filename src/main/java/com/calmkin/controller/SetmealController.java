package com.calmkin.controller;

import com.calmkin.common.R;
import com.calmkin.dto.SetmealDto;
import com.calmkin.service.SetmealDishService;
import com.calmkin.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @PostMapping
    public R<String> uploadWithDishes(@RequestBody SetmealDto setmealDto)
    {
        setmealService.saveWithDishes(setmealDto);
        return R.success("添加套餐成功");
    }

}
