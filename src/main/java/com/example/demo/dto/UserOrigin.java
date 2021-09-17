package com.example.demo.dto;

import lombok.Data;
import org.keycloak.representations.AccessToken;

import java.util.Set;

/**
 * UserOrigin
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/17 14:16:34
 **/
@Data
public class UserOrigin {
    private String name;
    private AccessToken token;
}
