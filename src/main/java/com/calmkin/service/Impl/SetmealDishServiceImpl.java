package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.dto.DishDto;
import com.calmkin.dto.SetmealDto;
import com.calmkin.mapper.SetmealDishMapper;
import com.calmkin.pojo.SetmealDish;
import com.calmkin.service.SetmealDishService;
import com.calmkin.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish>
        implements SetmealDishService  {
}
