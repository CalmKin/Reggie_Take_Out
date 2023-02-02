package com.calmkin.controller;

import com.calmkin.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.filePath}")    //从配置文件中读取转存路径，方便项目上线时更改
    private String filePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file)
    {

        String originalFilename = file.getOriginalFilename();
        //获取文件后缀名，取最后一个点的后面
        String suffix = originalFilename.substring( originalFilename.lastIndexOf('.') );

        //用UUID+文件格式后缀来代替原始的文件名，防止重名
        String fileName = UUID.randomUUID().toString()+suffix;
        try {
            //file只是一个临时文件，需要转存,文件存放路径和文件名都是动态生成的
            file.transferTo(new File(filePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //将文件名返回给前端，方便在数据库中存储文件名
        return R.success(fileName);
    }
}
