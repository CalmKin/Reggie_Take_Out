package com.calmkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.calmkin.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
