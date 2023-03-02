package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.mapper.OrdersMapper;
import com.calmkin.pojo.Orders;
import com.calmkin.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}
