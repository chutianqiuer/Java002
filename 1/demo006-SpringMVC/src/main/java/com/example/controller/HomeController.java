package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 *
 * 处理应用根路径的请求
 */
@Controller
public class HomeController {

    /**
     * 转向到首页
     *
     * @return 视图名
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
