package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.common.BaseContext;
import com.calmkin.common.CustomException;
import com.calmkin.mapper.OrdersMapper;
import com.calmkin.pojo.*;
import com.calmkin.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addrService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public boolean submitPay(Orders orders) {

        //获取当前用户id
        long userId = BaseContext.getID();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartService.list(lqw);

        if(list==null || list.size()==0)
        {
            throw new CustomException("购物车为空，不能下单");
        }


        //查询用户的详细信息
        User userInfo = userService.getById(userId);

        //查询收货人的名字
        AddressBook addr = addrService.getById(orders.getAddressBookId());
        if(addr==null)
        {
            throw new CustomException("用户地址信息有误，不能下单");
        }

        long orderId = IdWorker.getId();


        //计算总金额
        AtomicInteger paySum = new AtomicInteger(0);
        List<OrderDetail> orderDetailList = list.stream().map((item) -> {            //因为订单明细和购物车的数据类型不同，所以这里不能用beanUitil进行属性拷贝
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            paySum.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(paySum.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(userInfo.getName());
        orders.setConsignee(addr.getConsignee());
        orders.setPhone(addr.getPhone());
        orders.setAddress((addr.getProvinceName() == null ? "" : addr.getProvinceName())
                + (addr.getCityName() == null ? "" : addr.getCityName())
                + (addr.getDistrictName() == null ? "" : addr.getDistrictName())
                + (addr.getDetail() == null ? "" : addr.getDetail()));
        //向订单表插入数据
        this.save(orders);

        //向订单明细表插入若干条数据
        orderDetailService.saveBatch(orderDetailList);

        //清空购物车数据
        shoppingCartService.remove(lqw);

        return true;
    }
}
