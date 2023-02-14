package com.calmkin.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calmkin.mapper.UserMapper;
import com.calmkin.pojo.User;
import com.calmkin.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}

