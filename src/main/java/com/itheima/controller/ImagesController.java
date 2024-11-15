package com.itheima.controller;

import com.itheima.common.R;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class ImagesController {

    @Value("${reggie.basepath}")
    private String basepath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        //临时文件，请求之后自动删除
        log.info(file.toString());
        //使用UUID和原始后缀作为文件名
        String name = file.getOriginalFilename();
        String ex = name.substring(name.lastIndexOf("."));
        String fileName = UUID.randomUUID() + ex;
        //创建目录对象
        File dir = new File(basepath);
        if (!dir.exists()) {
            //目录不存在则创建
            dir.mkdirs();
        }
        //保存文件到指定目录
        file.transferTo(new File(basepath+fileName));
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //输入流，通过输入读取文件
        File img = new File(basepath+name);
        FileInputStream input = new FileInputStream(img);
        //输出流，向前端写入数据
        response.setContentType("image/jpeg");
        ServletOutputStream output = response.getOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = input.read(buffer)) != -1) {
            output.write(buffer, 0, len);
            output.flush();
        }
        input.close();
        output.close();
    }
}
