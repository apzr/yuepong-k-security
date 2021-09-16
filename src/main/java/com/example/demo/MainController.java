package com.example.demo;

import com.example.demo.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    MyUserDetailsService myUserDetailsService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/userinfo1")
    public String userinfo() {

        return "userinfo1";
    }

}
