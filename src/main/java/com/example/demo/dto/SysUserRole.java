package com.example.demo.dto;

import lombok.Data;

import java.util.List;

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
    private SysUser user;
    private List<SysRole> roles;

    public SysUserRole(){}


}
