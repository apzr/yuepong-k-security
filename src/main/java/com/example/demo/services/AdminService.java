package com.example.demo.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.dto.*;
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
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * KeyCloakAdminService
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/17 16:46:02
 **/
public interface AdminService {

    /**
     * getToken
     *
     * @param userCredentials
     * @return java.lang.String
     * @author apr
     * @date 2021/9/26 11:19
     */
    String getToken(UserCredentials userCredentials) ;

    /**
     * getByRefreshToken
     *
     * @param refreshToken
     * @return java.lang.String
     * @author apr
     * @date 2021/9/26 11:19
     */
    String getByRefreshToken(String refreshToken) ;

	/**
     * 登出
     *
     * @param userId
     * @return void
     * @author apr
     * @date 2021/9/26 11:19
     */
    void logoutUser(String userId);

	/**
     * 重设密码
     *
     * @param newPassword
     * @param userId
     * @return void
     * @author apr
     * @date 2021/9/26 11:19
     */
    void resetPassword(String newPassword, String userId);

}
