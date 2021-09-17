package com.example.demo.services.impl;

import com.example.demo.services.KeyCloakAdminService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * KetCloakAdminServiceImpl
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/17 16:51:02
 **/
public class KetCloakAdminServiceImpl implements KeyCloakAdminService {
    @Value("${keycloak.auth-server-url}")
    private String url;

    @Value("${kc.master.realm.user.name}")
    private String adminUserName;

    @Value("${kc.master.realm.user.password}")
    private String adminPassword;

    @Value("${kc.master.realm.client.id}")
    private String clientId;

	@Value("${target.realm}")
    private String TARGET_REALM;

    private static final String MASTER_REALM = "master";

    @Override
    public Map<String, String> getUserAttribute(String id) {
        return null;
    }

    @Override
    public void createUser(String account, String password, String userName, String medicalInstitution, String telephone, String email, String code, String userLevel) {

    }

    @Override
    public JSONArray getUserList() {
        return null;
    }

    @Override
    public JSONObject getUserInfo(String id) {
        return null;
    }

    @Override
    public void updateUserInfo(String userId, String password, String userName, String medicalInstitution, String telephone, String email, String code, String userLevel) {

    }

    @Override
    public String getUserNameByUserId(String userId) {
        return null;
    }

    @Override
    public void removeUser(String userId) {

    }
}
