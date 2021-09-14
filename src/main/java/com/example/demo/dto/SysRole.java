package com.example.demo.dto;

import lombok.Data;

/**
 * SysRole
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/14 14:55:59
 **/
@Data
public class SysRole {
    private Integer id;
    private String name;

    public SysRole(){
    }

    public SysRole(Integer id, String name){
        this.id = id;
        this.name = name;
    }
}
