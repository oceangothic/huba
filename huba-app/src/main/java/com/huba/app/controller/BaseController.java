package com.huba.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/huba/app")
@Api(tags = "基础Controller")
public class BaseController {

    @ApiOperation(value = "基础接口设置", notes = "基础接口测试")
    @GetMapping("/test")
    public String insertInsureCompany() {
        return "Hello wxx";
    }
}
