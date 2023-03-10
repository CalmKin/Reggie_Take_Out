package com.calmkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.calmkin.common.R;
import com.calmkin.pojo.Category;
import com.calmkin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> add(@RequestBody Category category)
    {
        categoryService.save(category);
        return R.success("添加成功");
    }


    //展示分类的分页查询
    //请求URL：http://localhost:8080/category/page?page=1&pageSize=10
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize)
    {
        Page<Category> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        //根据菜品排序字段来顺序展示
        lqw.orderByAsc(Category::getSort);

        Page<Category> pageRet = categoryService.page(pageInfo, lqw);

        return R.success(pageRet);
    }

    //删除请求路径：http://localhost:8080/category?ids=1397844263642378242
    @DeleteMapping
    public R<String> remove(Long ids)
    {
        categoryService.deleteById(ids);
        return R.success("删除分类成功");
    }

    /**
     * 根据id修改分类的信息
     * 请求地址：http://localhost:8080/category
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category)
    {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getId,category.getId());
        categoryService.update(category,lqw);
        return R.success("修改成功");
    }

    //虽然是get请求，参数在路径上，但是如果形参里面这个对象的存在路径上的请求参数名的属性，那么就能构造一个有指定属性值的对象
    @GetMapping("/list")
    public R<List<Category>> getCategoryList(Category category)
    {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(category.getType()!=null,Category::getType,category.getType());
        lqw.orderByAsc(Category::getSort);
        lqw.orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lqw);
        return R.success(list);
    }

}
