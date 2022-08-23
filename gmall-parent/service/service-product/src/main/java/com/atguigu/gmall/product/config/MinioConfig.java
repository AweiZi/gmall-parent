package com.atguigu.gmall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    //  获取文件上传对应的地址
    public String endpointUrl;

    public String accessKey;

    public String secreKey;

    public String bucketName;

}
