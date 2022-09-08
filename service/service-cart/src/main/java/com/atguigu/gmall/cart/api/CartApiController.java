package com.atguigu.gmall.cart.api;


import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/inner/rpc/cart")
@RestController
public class CartApiController {

    @Autowired
    CartService cartService;

    /**
     * 把商品添加到购物车
     *
     * @return 把那个商品添加到了购物车
     */
    @GetMapping("/addToCart")
    public Result<SkuInfo> addToCart(@RequestParam("skuId") Long skuId,
                                     @RequestParam("num") Integer num
    ) {
    //老请求RequestContextHolder 绑定在当前线程
        SkuInfo skuInfo = cartService.addToCart(skuId, num);


        return Result.ok();
    }

    /**
     * 删除购物车中选中的商品
     * @return
     */
    @GetMapping("/deleteChecked")
    public Result deleteChecked(){
        String cartKey = cartService.determinCartKey();
        cartService.deleteChecked(cartKey);
        return Result.ok();
    }
}
