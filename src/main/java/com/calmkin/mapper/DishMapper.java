package com.calmkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.calmkin.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
