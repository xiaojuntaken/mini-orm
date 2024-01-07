package org.hxj.controller;

import com.alibaba.fastjson.JSON;
import org.hxj.entity.po.Activity;
import org.hxj.entity.po.Banner;
import org.hxj.mapper.ActivityMapper;
import org.hxj.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @authorxiaojun
 * @date2023/12/31 14:39
 */
@RestController
public class TestController {
    @Autowired
    ActivityMapper activityMapper;

    @Autowired
    BannerMapper bannerMapper;

    @RequestMapping("/test")
    public String test() {
//        Activity activity = activityMapper.selectById(1);
//        Banner banner = bannerMapper.selectById(2);
        Activity activity = new Activity();
        activity.setMiniProgramId(1);
        List<Activity> list = activityMapper.selectByParam(activity);
        return JSON.toJSONString(list);
    }
}
