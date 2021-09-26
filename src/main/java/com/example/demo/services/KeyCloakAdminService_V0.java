package com.example.demo.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * KeyCloakAdminService
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/17 16:46:02
 **/
public interface KeyCloakAdminService_V0 {

    /**
     * 获取用户的属性值
     *
     * @param userId
     * @return
     */
    Map<String, String> getUserAttribute(String userId);

    /**
     * 创建用户
     *
     * @param account
     * @param password
     * @param userName
     * @param medicalInstitution
     * @param telephone
     * @param email
     * @param code
     * @param userLevel
     */
    void createUser(String account, String password, String userName, String medicalInstitution, String telephone, String email, String code, String userLevel) throws Exception;

     /**
     * 获取用户列表
     *
     * @return
     */
    JSONArray getUserList();

    /**
     * 获取用户信息
     *
     * @param userId
     * @return net.minidev.json.JSONObject
     * @author apr
     * @date 2021/9/26 9:18
     */
    JSONObject getUserInfo(String userId) throws Exception;

    /**
     * 更新用户信息
     *
     * @param userId
     * @param password
     * @param userName
     * @param medicalInstitution
     * @param telephone
     * @param email
     * @param code
     * @param userLevel
     * @return void
     * @author apr
     * @date 2021/9/26 9:20
     */
    void updateUserInfo(String userId, String password, String userName, String medicalInstitution, String telephone, String email, String code, String userLevel);

    /**
     * 根据用户id获取用户名
     *
     * @param userId
     * @return java.lang.String
     * @author apr
     * @date 2021/9/26 9:20
     */
    String getUserNameByUserId(String userId);

    /**
     * 删除用户
     *
     * @param userId
     * @return void
     * @author apr
     * @date 2021/9/26 9:20
     */
    void removeUser(String userId);
}
