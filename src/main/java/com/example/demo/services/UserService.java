package com.example.demo.services;

import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.UserDTO;

import java.util.List;

/**
 * UserService
 * 用户
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 10:31:09
 **/
public interface UserService {

    /**
     * 创建用户
     *
     * @param userDTO
     * @return id
     * @author apr
     * @date 2021/9/26 11:19
     */
    String createUser(UserDTO userDTO) ;

    /**
     * 修改用户
     *
     * @param userDTO
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    void updateUser(UserDTO userDTO) ;

    /**
     * 删除用户
     *
     * @param id
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    String deleteUser(String id) ;

    /**
     * 重置密码
     *
     * @param id
     * @return
     * @author apr
     * @date 2021/10/18 14:50
     */
    void resetUser(String id);

    /**
     * 用户列表
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
     UserDTO getUser(String uid) ;

    /**
     * 用户列表
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
     List<UserDTO> listUsers() ;

    /**
     * 用户列表(分页)
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
     List<UserDTO> pageUsers(String start, String size) ;

    /**
     * 用户列表(条件查询)
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
     List<UserDTO> search(UserDTO conditions) ;

    /**
     * 查询用户的关联的角色, 组
     *
     * @param uid
     * @return java.util.List<com.example.demo.dto.UserDTO>
     * @author apr
     * @date 2021/10/21 9:50
     */
     List listRoles(String uid);
}
