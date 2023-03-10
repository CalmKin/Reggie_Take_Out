package com.calmkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.calmkin.common.R;
import com.calmkin.dto.DishDto;
import com.calmkin.dto.SetmealDto;
import com.calmkin.pojo.Category;
import com.calmkin.pojo.Setmeal;
import com.calmkin.pojo.SetmealDish;
import com.calmkin.service.CategoryService;
import com.calmkin.service.SetmealDishService;
import com.calmkin.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 添加套餐功能
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> uploadWithDishes(@RequestBody SetmealDto setmealDto)
    {
        setmealService.saveWithDishes(setmealDto);
        return R.success("添加套餐成功");
    }

    /**
     * 套餐信息分页查询，套餐分类名称需要单独查询
     * 请求网址: http://localhost:8080/setmeal/page?page=1&pageSize=10
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")
    public R<Page> getPage(int page,int pageSize,String name)
    {
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Setmeal::getName,name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getStatus).orderByDesc(Setmeal::getUpdateTime);

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

    /**
     * 批量删除套餐，但是只有停售的套餐才能被删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByIds(@RequestParam List<Long> ids)
    {

        setmealService.deleteByIds(ids);
        return R.success("删除操作成功");
    }

    @PostMapping("/status/{targetStatus}")
    public R<String> changeStatus(@PathVariable int targetStatus,@RequestParam List<Long> ids)
    {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);

        List<Setmeal> list = setmealService.list(queryWrapper);

        list=list.stream().map((item)->{
            item.setStatus(targetStatus);
            return item;
        }).collect(Collectors.toList());

        setmealService.updateBatchById(list);

        return R.success("修改套餐状态成功");
    }


    /**
     * 根据id查询指定套餐的所有信息，用于修改套餐信息的数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getInfoById(@PathVariable Long id)
    {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = setmealService.getById(id);

        BeanUtils.copyProperties(setmeal,setmealDto);
        //查询这个套餐关联的所有菜品
        LambdaQueryWrapper<SetmealDish> lqw =new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> lists = setmealDishService.list(lqw);
        setmealDto.setSetmealDishes(lists);

        //单独查询这个套餐对应分类的名称
        Category category = categoryService.getById(setmeal.getCategoryId());
        setmealDto.setCategoryName(category.getName());

        return R.success(setmealDto);
    }


    @Transactional
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto)
    {

        //先把套餐的信息存进setmeal表里面（直接用dto来存）
        setmealService.updateById(setmealDto);

        //然后在setmealdish里面将所有关联信息都删掉
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();

        lqw.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lqw);

        //再重新插入关联信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        List<SetmealDish> collect = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(collect);
        return R.success("信息修改成功");
    }

    /**
     * http://localhost:8080/setmeal/list?categoryId=1622447465650585601&status=1
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> getList(Setmeal setmeal)
    {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();

        lqw.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lqw.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lqw.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(lqw);

        return R.success(list);

    }

}
