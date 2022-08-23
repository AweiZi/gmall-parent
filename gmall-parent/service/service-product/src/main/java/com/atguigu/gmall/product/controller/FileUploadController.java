package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("admin/product")
@EnableConfigurationProperties
public class FileUploadController {
    @Autowired
    MinioConfig minioConfig;

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
    @PostMapping("fileUpload")
    public Result fileUpload(@RequestPart("file") MultipartFile file) {
        try {
            //  准备获取到上传的文件路径！
         String url = "";
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(minioConfig.getEndpointUrl())
                    .credentials(minioConfig.getAccessKey(), minioConfig.getSecreKey())
                    .build();


            //        检查桶是否已经存在
            // Make 'asiatrip' bucket if not exist.
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucketName()).build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.创建一个名为asiatrip的桶

                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucketName()).build());
            } else {
                System.out.println("Bucket 'asiatrip' already exists.");
            }
            //  定义一个文件的名称 : 文件上传的时候，名称不能重复！
            String fileName = System.currentTimeMillis() + UUID.randomUUID().toString();
            // 使用putObject上传一个文件到存储桶中。
            //  minioClient.putObject("asiatrip","asiaphotos.zip", "/home/user/Photos/asiaphotos.zip");
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).stream(
                                    file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            //  System.out.println("/home/user/Photos/asiaphotos.zip is successfully uploaded as asiaphotos.zip to `asiatrip` bucket.");
            //  文件上传之后的路径： http://39.99.159.121:9000/gmall/xxxxxx
            url = minioConfig.getEndpointUrl() + "/" + minioConfig.bucketName + "/" + fileName;

            System.out.println("url:\t" + url);
            //  将文件上传之后的路径返回给页面！
            return Result.ok(url);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.ok();
    }
}
