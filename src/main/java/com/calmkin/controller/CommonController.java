package com.calmkin.controller;

import com.calmkin.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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

        //判断指定目录是否存在，如果不存在的话，需要手动创建，否则会报错
        File dir = new File(filePath);
        if(!dir.exists())
        {
            //目录不存在，需要创建
            dir.mkdirs();
        }

        try {
            //file只是一个临时文件，需要转存,文件存放路径和文件名都是动态生成的
            file.transferTo(new File(filePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //将文件名返回给前端，方便在数据库中存储文件名
        return R.success(fileName);
    }

    //进行图片回显,请求路径:请求网址: http://localhost:8080/common/download?name=xxx.jpg
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response)
    {
        //获取文件输入流
        try {
            FileInputStream inputStream = new FileInputStream(filePath+name);

            ServletOutputStream outputStream = response.getOutputStream();

            //别忘记设置一下内容格式，否则浏览器不知道怎么去解析
            response.setContentType("image/jpeg");

            //将文件写入到请求的输出流当中
            byte[] arr = new byte[1024];
            int len=0;

            while ( (len = inputStream.read(arr) ) != -1 )  //一直从文件输入流里面读取arr数组那么多个字节，直到不能读为止
            {

                //就向输出流里面写入那么多数据
                outputStream.write(arr,0,len);
                //清空缓冲区
                outputStream.flush();
            }

            //别忘记关闭资源
            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
}
