package com.huba.app.controller;

import com.huba.util.redis.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/huba/app")
@Api(tags = "基础Controller")
public class BaseController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "基础接口设置", notes = "基础接口测试")
    @GetMapping("/test")
    public String insertInsureCompany() {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(stringRedisTemplate);
        redisUtil.set("xxx", "{\"status\": \"success xxx\"}");
        return redisUtil.get("xxx");
    }
}
