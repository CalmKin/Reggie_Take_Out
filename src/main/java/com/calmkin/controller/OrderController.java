package com.calmkin.controller;


import com.calmkin.common.R;
import com.calmkin.pojo.Orders;
import com.calmkin.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

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
}
