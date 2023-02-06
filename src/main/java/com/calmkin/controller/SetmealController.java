package com.calmkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.calmkin.common.R;
import com.calmkin.dto.SetmealDto;
import com.calmkin.pojo.Category;
import com.calmkin.pojo.Setmeal;
import com.calmkin.service.CategoryService;
import com.calmkin.service.SetmealDishService;
import com.calmkin.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> uploadWithDishes(@RequestBody SetmealDto setmealDto)
    {
        setmealService.saveWithDishes(setmealDto);
        return R.success("添加套餐成功");
    }


    @GetMapping("/page")
    public R<Page> getPage(int page,int pageSize,String name)
    {
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealService.page(setmealPage,lambdaQueryWrapper);

        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> lists = records.stream().map((item)->{
            //进行属性拷贝
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            //查询这个套餐对应的分类名称
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            setmealDto.setCategoryName(category.getName());

            return setmealDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        setmealDtoPage.setRecords(lists);

        return R.success(setmealDtoPage);
    }

}
