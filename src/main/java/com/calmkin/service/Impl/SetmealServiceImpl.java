package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.mapper.SetmealMapper;
import com.calmkin.pojo.Setmeal;
import com.calmkin.service.SetmealService;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {
}
