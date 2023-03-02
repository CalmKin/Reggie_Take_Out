package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.mapper.OrderDetailMapper;
import com.calmkin.pojo.OrderDetail;
import com.calmkin.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
