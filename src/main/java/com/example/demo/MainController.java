package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/userinfo1")
    public String userinfo1(Principal principal) {
   	    Map<String, Object> map = new HashMap<String, Object>();
    	map.put("username", principal.getName());
    	System.out.println("Debug: the username is " + principal.getName());
        return "userinfo1";
    }

    @GetMapping("/userinfo")
    public String userinfo(Principal principal) {
   	    Map<String, Object> map = new HashMap<String, Object>();
    	map.put("username", principal.getName());
    	System.out.println("Debug: the username is " + principal.getName());
        return "userinfo";
    }
}
