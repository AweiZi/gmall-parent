package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.service.ActivityInfoService;
import com.atguigu.gmall.activity.service.CouponRangeService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.ActivityInfo;
import com.atguigu.gmall.model.activity.CouponRange;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/activity")
public class AdminController {
    @Autowired
    ActivityInfoService activityInfoService;

    @Autowired
    CouponRangeService couponRangeService;
    //http://192.168.6.1/admin/activity/activityInfo/1/10
    @GetMapping("/activityInfo/{page}/{limit}")
    public Result getActivityInfo( @PathVariable Long page,
                                   @PathVariable Long limit) {
        Page<ActivityInfo> pageParam = new Page<>(page, limit);
        IPage<ActivityInfo> infoPage = activityInfoService.page(pageParam);
        return Result.ok(infoPage);
    }

    //    http://192.168.6.1/admin/activity/couponInfo/1/10
    @GetMapping("/couponInfo/{pn}/{pz}")
    public Result getCouponInfo(@PathVariable Long pn,
                                @PathVariable Long pz) {
        Page<CouponRange> page = new Page<CouponRange>();
        IPage<CouponRange> couponRangePage = couponRangeService.page(page);
        return Result.ok(couponRangePage);
    }

}
