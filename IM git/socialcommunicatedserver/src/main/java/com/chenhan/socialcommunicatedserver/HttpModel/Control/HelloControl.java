package com.chenhan.socialcommunicatedserver.HttpModel.Control;

import com.chenhan.socialcommunicatedserver.HttpModel.Service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloControl {
    @Autowired
    HttpService service;
    @GetMapping("/hello")
    public String helloController(){
        return service.helloController();
    }

}
