package com.atguigu.gamll.pay;

import com.alipay.api.AlipayApiException;
import com.atguigu.gamll.pay.service.AlipayService;
import com.atguigu.gmall.common.util.Jsons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/api/payment")
public class PayController {
    @Autowired
    AlipayService alipayService;

    /*买家账号cedtkv5665@sandbox.com
            登录密码111111
    支付密码111111
            用户UID2088622987769648
    用户名称cedtkv5665
            证件类型IDENTITY_CARD
    证件账号93381119451120370X*/
    @ResponseBody
    @GetMapping("/alipay/submit/{orderId}")
    public String alipayPage(@PathVariable("orderId") Long orderId) throws AlipayApiException {

        String content = alipayService.getAlipayPageHtml(orderId);

        return content;
    }

    /**
     *
     */
    @GetMapping("/paysuccess") //同步通知地址
    public String paysuccess(@RequestParam Map<String, String> paramMaps) throws AlipayApiException {
        System.out.println("支付成功同步通知页：收到的参数：" + paramMaps);
        //1、如果要在这里改订单状态，先验签。验证是否支付宝给我们发来的数据
        boolean b = alipayService.rsaCheckV1(paramMaps);
        if (b) {
            //验签通过
            System.out.println("正在修改订单状态....订单信息：" + paramMaps);
        }

        return "redirect:http://gmall.com/pay/success.html";
    }

    /**
     * 支付成功
     */
    @ResponseBody
    @RequestMapping("/success/notify")
    public String notifySuccess(@RequestParam Map<String, String> param) throws AlipayApiException {
        boolean b = alipayService.rsaCheckV1(param);
        if (b) {
            log.info("异步通知抵达。支付成功，验签通过。数据：{}", Jsons.toStr(param));
            //TODO 修改订单状态。用到支付最大努力通知


        } else {
            return "error";
        }
        return "success";

    }
}