package com.calmkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.calmkin.common.R;
import com.calmkin.dto.DishDto;
import com.calmkin.pojo.*;
import com.calmkin.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;    //用于设置dishDTO的分页数据查询用的

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品功能
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> postDish(@RequestBody DishDto dishDto)
    {
        //因为这里涉及到两张表的插入，所以不能直接调用现有的方法
        dishService.saveWithFlavor(dishDto);
        return R.success("菜品保存成功");
    }

    /**
     * 菜品信息分页查询
     * 请求网址: http://localhost:8080/dish/page?page=1&=10
     * @return
     */
    @GetMapping("/page")
    public R<Page> getPage(int page,int pageSize,String name)
    {
        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);

        //先用dishPage获取到除了菜品名的所有属性值
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        dishService.page(dishPage,lqw);

        //通过属性拷贝，将dishPage中除了records以外，其他属性值都拷贝过来（因为两个records的类型都不同，不能将所有的属性都拷贝过去）
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        //获取到所有的dish对象，dishDTO其实就是在dish对象的基础上多加一个categoryName属性
        List<Dish> records = dishService.page(dishPage, lqw).getRecords();

        //采用lambda表达式的方式进行属性修改
        List<DishDto> list = records.stream().map((item) -> {

            DishDto dishDto = new DishDto();

            //item就是一个dish对象，需要将dish对象所有属性都拷贝过来
            BeanUtils.copyProperties(item,dishDto);

            //item没有的属性，再单独设置(categoryName),用item里面的CategoryId查询到对应的Category对象，将这个对象的name设置到dishdto上面
            LambdaQueryWrapper<Category> categoryLQW = new LambdaQueryWrapper<>();
            categoryLQW.eq(Category::getId,item.getCategoryId());
            Category one = categoryService.getOne(categoryLQW);

            dishDto.setCategoryName(one.getName());

            return dishDto;  //将这个对象放进list里面
        }).collect(Collectors.toList());

        //再将改造好的records塞回dishDtoPage
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 请求网址: http://localhost:8080/dish/1397849739276890114
     * 编辑菜品信息数据回显——根据id查询菜品全部信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> data_reflect(@PathVariable Long id)       //因为回显的数据除了dish的信息，还有flavor的信息，所以回显dishdto对象
    {
        DishDto dishDto = dishService.get_with_id(id);
        return R.success(dishDto);
    }

    /**
     * 编辑菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto)
    {
        dishService.update_with_flavor(dishDto);
        return R.success("修改信息成功");
    }

    /**
     * 根据菜品分类查询所有菜品（第一次返回list<Dish>,后面返回list<DishDto>，否则前端无法展示选择规格）
     * 移动端二次修改
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> getDishes(Dish dish)
    {

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());

        //只有起售的菜品才进行显示
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        //排序展示
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();

            lqw.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> flavors = dishFlavorService.list(lqw);

            dishDto.setFlavors(flavors);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }


    /**
     * 批量进行禁用菜品,但是要先看是否存在菜品关联了某个套餐，而且是正在起售的，那么我们就不禁用这些菜品
     * @param targetStatus
     * @param ids
     * @return
     */
    @PostMapping("/status/{targetStatus}")
    public R<String> changeStatusByIds(@PathVariable int targetStatus,@RequestParam List<Long> ids)
    {
        boolean result = dishService.changeBatchStatusByIds(targetStatus, ids);
        if(result)
            return R.success("禁用菜品成功");
        return R.success("部分菜品禁用失败(已关联套餐)");
    }

    /**
     * 根据id批量删除菜品，逻辑和批量禁用菜品一样，需要判断是否关联套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByIds(@RequestParam List<Long> ids)
    {

        boolean results = dishService.deldeteBatchs(ids);

        if(results)
            return R.success("删除菜品成功");
        return R.success("部分菜品删除失败(已关联套餐)");
    }

}



