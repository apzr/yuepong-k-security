package com.example.demo;

import com.example.demo.dto.SysUser;
import com.example.demo.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    MyUserDetailsService myUserDetailsService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/userinfo1")
    public String userinfo1() {

        return "userinfo1";
    }

    @GetMapping("/userinfo")
    public String userinfo() {

        return "userinfo";
    }
}
