package com.calmkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.calmkin.common.R;
import com.calmkin.pojo.User;
import com.calmkin.service.UserService;
import com.calmkin.utils.SMSUtils;
import com.calmkin.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    /**
     * 前端将用户手机号发送过来，后台随机生成验证码，然后通过阿里云短信服务进行发送,同时在浏览器的session里面存一份，用于
     * 请求网址: http://localhost:8080/user/sendMsg
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session)
    {
        Integer code = ValidateCodeUtils.generateValidateCode(6);

        //第四个参数携带要发送的验证码
//        SMSUtils.sendMessage("calmkin","SMS_269860027",user.getPhone(),code.toString());
        log.info("验证码是${}",code);

        session.setAttribute("code",code);

        return R.success("短信发送成功");
    }

    /**
     * 将用户填写的验证码和存在缓存里面的验证码进行比较，如果相同，则登录成功，用户不存在的话，
     * 自动进行存表操作，然后将用户的id存进session，防止未登录拦截
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody LinkedHashMap<String,String> map, HttpSession session)   //这里map携带的是用户填进去的验证码
    {
        //首先检查用户填写的验证码是否正确
        String phone = map.get("phone");
        String code = map.get("code");

        System.out.println(phone+"  "+code);

        if( session.getAttribute("code").toString().equals(code) )
        {
            //然后检查用户是否已经注册过了
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);
            User one = service.getOne(lqw);
            //没有注册的话，就将其存进用户表里面
            Long id ;
            if(one==null)
            {
                User user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                service.save(user);
                id=user.getId();
            }
            else
                id= one.getId();

            session.setAttribute("user",id);
            //然后将生成的用户id存进session
            return R.success("登录成功");
        }
        return R.error("登录失败");
    }


}
