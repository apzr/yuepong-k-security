package com.example.demo.services;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.Map;

/**
 * KeyCloakAdminService
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/17 16:46:02
 **/
public interface KeyCloakAdminService {

    Map<String, String> getUserAttribute(String id);

    void createUser(String account, String password, String userName, String medicalInstitution, String telephone, String email, String code, String userLevel);

    JSONArray getUserList();

    JSONObject getUserInfo(String id);

    void updateUserInfo(String userId, String password, String userName, String medicalInstitution, String telephone, String email, String code, String userLevel);

    String getUserNameByUserId(String userId);

    void removeUser(String userId);
}
