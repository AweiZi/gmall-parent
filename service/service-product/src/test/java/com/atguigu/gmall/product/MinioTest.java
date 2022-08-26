package com.atguigu.gmall.product;

import io.minio.*;
import io.minio.errors.MinioException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

//@SpringBootTest//可以测试springboot的所有组件功能
public class MinioTest {
    public static void main(String[] args)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
//      使用minio服务的URL，端口
            MinioClient minioClient = new
                    MinioClient(
                    "http://192.168.6.99:9000",
                    "admin", "admin123456"
            );

            // Make 'asiatrip' bucket if not exist.
            boolean found = minioClient.bucketExists("gmall");

            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket("gmall");
            } else {
                System.out.println("Bucket 'asiatrip' already exists.");
            }

            // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
            // 'asiatrip'.
            /*
             * String butName
             * */
            //文件流
            FileInputStream inputStream = new FileInputStream("D:\\0310尚硅谷\\08-尚品汇\\资料\\03 商品图片\\品牌\\\\pingguo.png");
            //文件上传参数：long objectSize, long partSize
            PutObjectOptions options = new PutObjectOptions(inputStream.available(), -1L);
            options.setContentType("image/png");
            //告诉Minio上传的这个文件的内容类型
            minioClient.putObject("gmall", "pingguo.png",
                    inputStream,
                    options
            );
            System.out.println("上传成功");
        } catch (MinioException e) {
            System.out.println("发生错误" + e);
        }
    }
}