package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.mapper.EmployeeMapper;
import com.calmkin.pojo.Employee;
import com.calmkin.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
                                 implements EmployeeService {

}
