package com.calmkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.calmkin.common.R;
import com.calmkin.mapper.EmployeeMapper;
import com.calmkin.pojo.Employee;
import com.calmkin.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")  //返回统一的响应信息
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee)  //我们需要存储session或者读取session，这就需要通过request对象进行操作
    {

//      1、将页面提交的密码password进行md5加密处理
        String pwd = employee.getPassword();
        pwd = DigestUtils.md5DigestAsHex(pwd.getBytes());

//      2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,employee.getUsername());

        Employee emp = employeeService.getOne(lqw);//之所以返回一个，是因为employee表里面，用户姓名这个字段设置成了互不相等，所以不会有重复

//      3、如果没有查询到则返回登录失败结果
        if(emp==null)  return R.error("登录失败");

//      4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(pwd)) return R.error("登录失败");

//      5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus()==0) return R.error("账号已被锁定");

//      6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpServletRequest)
    {
        httpServletRequest.getSession().removeAttribute("employee");
        return R.success("logout success");
    }

    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request)   //这里需要request参数是因为，设置创建人的时候，需要用到当前登录的用户id，这个要从session中获取
    {
        //System.out.println("员工信息"+ employee.toString());

        //存库的时候，id是默认自动加上去的，status默认为1，密码可以设置一个默认密码，假设为123456，后面让员工自己去修改就行
        //密码要经过加密之后才能存进数据库
        employee.setPassword( DigestUtils.md5DigestAsHex("123456".getBytes()) );
        //设置创建时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //设置创建人（当前登录用户）
        Long userId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(userId);
        employee.setUpdateUser(userId);

        employeeService.save(employee);
        return R.success("添加员工成功");
    }

}
