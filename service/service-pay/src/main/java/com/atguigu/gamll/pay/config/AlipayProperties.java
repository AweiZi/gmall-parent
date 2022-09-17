package com.atguigu.gamll.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.alipay")
@Component
@Data
public class AlipayProperties {


    private String gatewayUrl;//支付宝网关

    private String appId;//用户id

    private String merchantPrivateKey;//商户私钥

    private String charset; //字符集

    private String alipayPublicKey;//阿里公钥

    private String signType; //签名方式

    private String returnUrl; //页面返回地址

    private String notifyUrl; //异步通知地址
}
