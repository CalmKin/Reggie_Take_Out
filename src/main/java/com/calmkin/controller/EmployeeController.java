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
        httpServletRequest.removeAttribute("employee");
        return R.success("logout success");
    }

}
