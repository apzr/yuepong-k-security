package com.example.demo.services.impl;

import com.example.demo.dto.UserCredentials;
import com.example.demo.dto.UserDTO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.services.KeyCloakAdminService_V0;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;

/**
 * KeyCloakAdminServiceImpl
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/17 16:51:02
 **/
@Service
public class KeyCloakAdminServiceImpl_V0 implements KeyCloakAdminService_V0 {
    @Value("${keycloak.auth-server-url}")
    private String url;

    @Value("${kc.master.realm.user.name}")
    private String adminUserName;

    @Value("${kc.master.realm.user.password}")
    private String adminPassword;

    @Value("${kc.master.realm.client.id}")
    private String clientId;

	@Value("${kc.target.realm}")
    private String TARGET_REALM;

    private static final String MASTER_REALM = "master";

    @Override
    public Map<String, String> getUserAttribute(String userId) {
        Keycloak kcMaster = Keycloak.getInstance(url, MASTER_REALM, adminUserName, adminPassword, clientId);
        System.out.println(kcMaster.isClosed());
        RealmResource realmResource = kcMaster.realm(TARGET_REALM);
        UsersResource userResource = realmResource.users();
        Map<String, String> attributeMap = new HashMap<>();
        List<UserRepresentation> userList = userResource.list();
        for (UserRepresentation user : userList) {
            Map<String, List<String>> userAttributesList = user.getAttributes();
            if (userId != null && userId.equals(user.getId())) {
                for (String key : userAttributesList.keySet()) {
                    String attribute = null;
                    if (userAttributesList != null && userAttributesList.get(key) != null && userAttributesList.get(key).size() > 0) {
                        attribute = userAttributesList.get(key).get(0);
                    }
                    attributeMap.put(key, attribute);
                }
            }
        }
        return attributeMap;
    }

    @Override
    public void createUser(String account, String password, String userName, String medicalInstitution, String telephone, String email, String code, String userLevel) throws Exception {
        Keycloak kcMaster = Keycloak.getInstance(url, MASTER_REALM, adminUserName, adminPassword, clientId);
        RealmResource realmResource = kcMaster.realm(TARGET_REALM);
        UsersResource userResource = realmResource.users();
        //编辑用户信息
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(account);
        user.setFirstName(userName);
        // user.setLastName(lastName);
        user.setEmail(email);
        Map<String, List<String>> attributeMap = new HashMap<>();

        attributeMap.put("medicalInsitution", Arrays.asList(medicalInstitution));
        attributeMap.put("telephone", Arrays.asList(telephone));
        attributeMap.put("code", Arrays.asList(code));
        attributeMap.put("userLevel", Arrays.asList(userLevel));
        //user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));
        user.setAttributes(attributeMap);
        //将创建的用户添加到系统中，创建新用户
        Response response = userResource.create(user);

        //判断创建用户状态；如果时创建成功
        Response.StatusType createUserStatus = response.getStatusInfo();
        URI location = response.getLocation();
        System.out.println(createUserStatus);
        if ("Created".equals(createUserStatus.toString())) {
            System.out.println("创建用户成功！");
            System.out.println("创建用户的URI：" + location);
        } else {
            throw new Exception("账号已经存在！");
        }
        //获取创建用户的userId
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        //获取该域下角色为user的描述
        String userRoleName = userLevel;
//        if ("admin".equals(userLevel)) {
//            userRoleName = "admin";
//        }
        //创建角色
         RoleRepresentation testerRealmRole = realmResource.roles()
                 .get(userRoleName).toRepresentation();
         userResource.get(userId).roles().realmLevel().add(Arrays.asList(testerRealmRole));

//        ClientRepresentation app1Client = realmResource.clients().findByClientId(TARGET_CLIENT_ID).get(0);

//        RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()) //
//                .roles().get(userRoleName).toRepresentation();

        // Assign client level role to user
//        userResource.get(userId).roles().clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));

        //重置用户密码
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        // 重置用户密码
        userResource.get(userId).resetPassword(passwordCred);
    }

    @Override
    public JSONArray getUserList() {
        Keycloak kcMaster = Keycloak.getInstance(url, MASTER_REALM, adminUserName, adminPassword, clientId);
        RealmResource realmResource = kcMaster.realm(TARGET_REALM);
        UsersResource userResource = realmResource.users();
        List<UserRepresentation> userList = userResource.list();
        JSONArray array = new JSONArray();
//        String userId = UserUtils.getUserId();
        for (UserRepresentation user : userList) {
            JSONObject object = new JSONObject();
//            if (userId != null && userId.equals(user.getId())) {
//                continue;
//            }
            object.put("id", user.getId());
            object.put("account", user.getUsername());
            object.put("email", user.getEmail());
            object.put("userName", user.getFirstName());
            Map<String, List<String>> userAttributesList = user.getAttributes();
            if (userAttributesList != null) {
                for (String key : userAttributesList.keySet()) {
                    object.put(key, userAttributesList.get(key).get(0));
                }
                array.add(object);
            }

        }
        return array;
    }

    @Override
    public JSONObject getUserInfo(String userId) throws Exception {
        Keycloak kcMaster = Keycloak.getInstance(url, MASTER_REALM, adminUserName, adminPassword, clientId);
        RealmResource realmResource = kcMaster.realm(TARGET_REALM);
        UsersResource userResource = realmResource.users();
        if (StringUtils.isEmpty(userId)) {
            throw new Exception("userId为空");
        }
        List<UserRepresentation> userList = userResource.list();
        JSONObject object = new JSONObject();
        for (UserRepresentation user : userList) {
            if (userId.equals(user.getId())) {
                object.put("id", user.getId());
                object.put("account", user.getUsername());
                object.put("email", user.getEmail());
                object.put("userName", user.getFirstName());
                Map<String, List<String>> userAttributesList = user.getAttributes();
                for (String key : userAttributesList.keySet()) {
                    object.put(key, userAttributesList.get(key).get(0));
                }
            }
        }
        return object;
    }

    @Override
    public void updateUserInfo(String userId, String password, String userName, String medicalInstitution, String telephone, String email, String code, String userLevel) {

    }

    @Override
    public String getUserNameByUserId(String userId) {
        if(userId == null){return "";}

		JSONObject userInfo = null;
		String userName = null;
		try {
			userInfo = this.getUserInfo(userId);
			userName = userInfo.getString("account");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userName;
    }

    @Override
    public void removeUser(String userId) {
        Keycloak kcMaster = Keycloak.getInstance(url, MASTER_REALM, adminUserName, adminPassword, clientId);
        RealmResource realmResource = kcMaster.realm(TARGET_REALM);
        UsersResource userResource = realmResource.users();
        userResource.get(userId).remove();
    }

}
