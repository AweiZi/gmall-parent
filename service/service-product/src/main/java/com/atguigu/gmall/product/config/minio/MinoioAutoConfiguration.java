package com.atguigu.gmall.product.config.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinoioAutoConfiguration {
    @Autowired
    MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() throws Exception {
        //1、创建Minio客户端
        MinioClient minioClient = new MinioClient(
                minioProperties.getEndpointUrl(),
                minioProperties.getAccessKey(),
                minioProperties.getSecreKey()
        );

        String bucketName = minioProperties.getBucketName();
        if(!minioClient.bucketExists(bucketName)){
            minioClient.makeBucket(bucketName);
        }

        return minioClient;
    }
}
