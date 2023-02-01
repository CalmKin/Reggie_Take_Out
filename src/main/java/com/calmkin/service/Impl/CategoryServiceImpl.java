package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.common.CustomException;
import com.calmkin.mapper.CategoryMapper;
import com.calmkin.pojo.Category;
import com.calmkin.pojo.Dish;
import com.calmkin.pojo.Setmeal;
import com.calmkin.service.CategoryService;
import com.calmkin.service.DishService;
import com.calmkin.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>

                                 implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除对应分类，删除之前需要判断以下是否存在菜品或者套餐关联了这个分类
     * @param id
     */
    @Override
    public void deleteById(Long id) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //查询是否存在菜品关联了这个分类
            dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
            int count1 = dishService.count(dishLambdaQueryWrapper);
            //存在关联,抛出业务异常
            if(count1!=0)
            {
                throw new CustomException("删除失败,该分类已关联菜品");
            }
        //查询是否存在套餐关联了这个分类
            setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
            int count2 = setmealService.count(setmealLambdaQueryWrapper);
        //存在关联,抛出业务异常
            if(count2!=0)
            {
                throw new CustomException("删除失败,该分类已关联套餐");
            }
        //都不存在，执行删除操作,这里super调用的还是IServiec的方法
        super.removeById(id);
    }
}
