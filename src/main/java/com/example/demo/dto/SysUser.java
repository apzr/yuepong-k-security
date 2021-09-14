package com.example.demo.dto;

import lombok.Data;

import java.util.Map;
import java.util.Optional;

/**
 * SysUser
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/14 14:51:45
 **/
@Data
public class SysUser {
    private Integer id;
    private String name;
    private String password;

    public SysUser(){
    }

    public SysUser(Integer id, String name, String pass){
        this.id = id;
        this.name = name;
        this.password = pass;
    }


    public SysUser(Map attr){
        super();
        this.name = Optional.ofNullable((String)attr.get("given_name")).orElse("")
                    +""
                    +Optional.ofNullable((String)attr.get("family_name")).orElse("");
    }

    public static SysUser toUser(Map attr){
        return new SysUser(attr);
    }
}
