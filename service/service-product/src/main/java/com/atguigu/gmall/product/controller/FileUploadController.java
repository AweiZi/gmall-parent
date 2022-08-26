package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@Api(value = "文件上传控制器")
@RestController
@RequestMapping("admin/product")
public class FileUploadController {
    @Autowired
    FileUploadService fileUploadService;

    /*
     * 文件上传
     * 1.前端将文件流放在哪？我们应该怎么拿到？
     * post请求数据放入请求体中（包含了文件流）
     *
     * 各种注解不同位置的请求数据
     * @RequestParam： 无论什么请求 接请求参数：用一个Pojo接受所有数据
     * @RequestPart： 接受请参数里面的文件项
     * @RequestBody： 接收请求体中的锁有数据（pojo转为pojo）
     * @PathVariable：接受路径上的动态变量
     * @RequestHeard：获取浏览器发送的请求的请求头中的某些值
     * @CookieValue: 获取浏览器发送的请求的Cookie值
     *
     * 如果多个就写数组，否则就写单个对象
     * */
//    http://192.168.6.1/admin/product/fileUpload
    @ApiOperation(value = "文件上传")
    @PostMapping("fileUpload")
    public Result fileUpload(@RequestPart("file") MultipartFile file) throws Exception {
        String url = fileUploadService.upload(file);
        return Result.ok(url);
    }
}
