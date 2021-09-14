package com.example.demo.dto;

import lombok.Data;

/**
 * SysUserRole
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/14 14:51:56
 **/
@Data
public class SysUserRole {
    private Integer userId;
    private Integer roleId;

    public SysUserRole(){}

    public SysUserRole(Integer uid, Integer rid){
        this.userId = uid;
        this.roleId = rid;
    }
}
