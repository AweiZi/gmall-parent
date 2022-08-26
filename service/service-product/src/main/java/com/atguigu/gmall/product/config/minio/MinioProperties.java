package com.atguigu.gmall.product.config.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.minio")
public class MinioProperties {
    //  获取文件上传对应的地址
    public String endpointUrl;

    public String accessKey;

    public String secreKey;

    public String bucketName;

}
