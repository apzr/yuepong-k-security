package com.example.demo;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;

@Controller
public class MainController {

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @RolesAllowed("ROLE_VIEWER")
    @GetMapping("/userinfo")
    public String userinfo(Model model, KeycloakAuthenticationToken authentication) {//KeycloakAuthenticationToken
        model.addAttribute("userName", authentication.getName());
        model.addAttribute("credentials", authentication.getAccount().getKeycloakSecurityContext().getTokenString());

        return "userinfo";
    }

}
