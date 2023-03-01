package com.calmkin.controller;

import com.aliyun.credentials.http.HttpRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.calmkin.common.BaseContext;
import com.calmkin.common.R;
import com.calmkin.pojo.ShoppingCart;
import com.calmkin.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService service;

    /**
     * 添加一条购物车（一类餐品在数据库对应一个购物车记录）
     * 因为前端还需要购物车的数据来显示，所以不是返回字符串
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> addOne(@RequestBody ShoppingCart shoppingCart)
    {
        //设置这个购物车的用户ID
        shoppingCart.setUserId(BaseContext.getID());
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        //如果添加的是菜品
        lqw.eq(ShoppingCart::getUserId,shoppingCart.getUserId());

        //如果添加的是套餐
        if(shoppingCart.getDishId()!=null)
        {
            lqw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }
        else
        {
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart one = service.getOne(lqw);

        //否则就创建一个购物车对象，然后添加到数据库
        if(one==null)
        {
            shoppingCart.setNumber(1); //初始情况第一次添加需要设置数量为1
            shoppingCart.setCreateTime(LocalDateTime.now());
            service.save(shoppingCart);
            one = shoppingCart;
        }
        //如果已经存在这个购物车，那就在原来的数量上增加
        else
        {
            Integer number = one.getNumber();
            one.setNumber(number+1);
            service.updateById(one);
        }
        return R.success(one);
    }

    /**
     * 根据用户的id，显示用户购物车信息，每次对购物车进行操作的时候，都会重新执行一次这个查询，以更新信息
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getList()
    {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        //根据用户id来查询购物车
        lqw.eq(ShoppingCart::getUserId,BaseContext.getID());
        lqw.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = service.list(lqw);

        return R.success(list);
    }


    /**
     * 根据用户id，清空该用户购物车
     * 请求网址: http://localhost:8080/shoppingCart/clean
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clear()
    {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getID());
        service.remove(lqw);
        return R.success("清空购物车成功");
    }

    /**
     * 减少该物品在购物车中的数量
     * 如果数量大于1，那么直接修改数量
     * 否则直接将这个物品从购物车中删除
     * 因为请求发送的是菜品的id或者套餐的id
     * 所以直接用购物车实体类来接收这两个参数
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> subOne(@RequestBody ShoppingCart shoppingCart)
    {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getID());

        if(shoppingCart.getDishId()!=null)
        {
            lqw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }
        else
        {
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart one = service.getOne(lqw);

        if(one.getNumber()==1)
        {
            service.remove(lqw);
        }
        else
        {
            Integer number = one.getNumber();
            one.setNumber(number-1);
            service.updateById(one);
        }
        return R.success("减少数量成功");
    }
}
