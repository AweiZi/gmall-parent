package com.atguigu.gamll.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfiguration {

    @Bean
    public AlipayClient aliPayClient(AlipayProperties properties) {
        AlipayClient alipayClient = new DefaultAlipayClient(
                properties.getGatewayUrl(),//支付宝网关
                properties.getAppId(), //用户id
                properties.getMerchantPrivateKey(), //商户私钥
                "json",
                properties.getCharset(), //字符集
                properties.getAlipayPublicKey() , //阿里公钥
                properties.getSignType()//签名方式
        );

        return alipayClient;
    }
}
