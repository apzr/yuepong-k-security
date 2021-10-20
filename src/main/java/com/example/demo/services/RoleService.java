package com.example.demo.services;

import com.example.demo.dto.RoleDTO;

import java.util.List;

/**
 * RoleService
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 10:31:21
 **/
public interface RoleService {
    /**
     * 创建角色
     *
     * @param roleDTO
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    String createRole(RoleDTO roleDTO) ;

    /**
     * 编辑角色
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    void updateRole(RoleDTO roleDTO) ;

    /**
     * 删除角色
     *
     * @author apr
     * @date 2021/9/26 11:19
     */
    void deleteRole(String name) ;

    /**
     * 角色列表
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    RoleDTO getRole(String name) ;

    /**
     * 角色列表
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    List<RoleDTO> getRole(RoleDTO conditions) ;

    /**
     * 角色列表
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    List<RoleDTO> listRoles() ;
}
