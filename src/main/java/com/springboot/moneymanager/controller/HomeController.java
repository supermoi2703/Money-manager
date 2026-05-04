package com.springboot.moneymanager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/status", "/health"})
public class HomeController {
    @RequestMapping
    public String HealthCheck() {
        return "Application is running";
    }

}
