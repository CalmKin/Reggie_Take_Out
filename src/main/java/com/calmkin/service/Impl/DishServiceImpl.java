package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.dto.DishDto;
import com.calmkin.mapper.DishMapper;
import com.calmkin.pojo.Dish;
import com.calmkin.pojo.DishFlavor;
import com.calmkin.service.DishFlavorService;
import com.calmkin.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional      //涉及多表操作，需要开启事务
    public void saveWithFlavor(DishDto dishDto) {
        //因为dishdto继承了dish（dish的字段全都有），所以可以直接用dishdto插入到表中
        this.save(dishDto);

        Long id = dishDto.getId();  //这里可以直接获取插入之后生成的id（具体为什么我也不清楚）

        List<DishFlavor> flavors = dishDto.getFlavors();
        //但是插入到dish_flavor之前，还需要设置一下口味对应的dish_id
        for(int i=0;i<flavors.size();i++)
        {
            flavors.get(i).setDishId(id);
        }


        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto get_with_id(Long id) {
        Dish dish = this.getById(id);   //dish对象可以直接获取

        //拿着菜品的id去取口味的列表
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper();
        lqw.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(lqw);

        DishDto dishDto = new DishDto();
        //将dish 的属性都拷贝到dishdto上
        BeanUtils.copyProperties(dish,dishDto);

        //只有一个属性需要拷贝的话，可以直接set
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 修改菜品的所有信息（涉及多表操作，需要开启事务）
     * @param dishDto
     */
    @Transactional
    @Override
    public void update_with_flavor(DishDto dishDto) {

        this.updateById(dishDto); //先将dish相关信息保存到dish表中

        Long dish_id = dishDto.getId();

        //先把该菜品关联的所有口味都删除
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish_id);
        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> lists = dishDto.getFlavors();
        //需要设置上这些口味对应的菜品id
        lists = lists.stream().map((item)->{
            item.setDishId(dish_id);
            return item;
        }).collect(Collectors.toList());

        //再重新添加该菜品对应口味
        dishFlavorService.saveBatch(lists);

    }
}
