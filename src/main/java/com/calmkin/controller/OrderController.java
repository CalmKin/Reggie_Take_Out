package com.calmkin.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.calmkin.common.BaseContext;
import com.calmkin.common.R;
import com.calmkin.dto.OrdersDto;
import com.calmkin.pojo.OrderDetail;
import com.calmkin.pojo.Orders;
import com.calmkin.service.OrderDetailService;
import com.calmkin.service.OrdersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService detailService;

    /**
     * 提交订单进行支付
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submitPay(@RequestBody Orders orders)
    {
        boolean result = ordersService.submitPay(orders);
        if(result)
        return R.success("支付成功");
        else
            return R.error("支付失败");
    }

    /**
     * 请求网址: http://localhost:8080/order/userPage?page=1&pageSize=1
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> allOrders(int page, int pageSize)
    {

        Page<OrdersDto> page1 = new Page<>(page,pageSize);
        Page<Orders> page2 = new Page<>(page,pageSize);

        Long userId = BaseContext.getID();

        //找到这个用户的所有订单,进行分页查询
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId,userId);
        ordersService.page(page2,ordersLambdaQueryWrapper);
        //分页查询属性值拷贝
        BeanUtils.copyProperties(page2,page1,"records");

        List<Orders> list = page2.getRecords();

        List<OrdersDto> records2 = list.stream().map((item) -> {
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(item, dto);

            Long orderId = item.getId();

            //获取该订单下的所有子订单
            LambdaQueryWrapper<OrderDetail> detail = new LambdaQueryWrapper<>();
            detail.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> detailList = detailService.list(detail);
            dto.setOrderDetails(detailList);
            return dto;
        }).collect(Collectors.toList());


        page1.setRecords(records2);

        return R.success(page1);
    }
}
