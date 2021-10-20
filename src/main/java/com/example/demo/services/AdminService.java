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

    /**
     * 创建用户角色映射
     *
     * @param mappingDTO
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    int createMapping(MappingDTO mappingDTO) ;
    int createMapping(MappingsDTO mappingsDTO);

    /**
     * 删除角色用户映射
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    void deleteMapping(MappingDTO mappingDTO) ;

    /**
     * 获取映射列表
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    List<MappingDTO> getMappingsByUser(String uid) ;

    /**
     * 获取映射列表
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    List<MappingDTO> listMappings() ;

    /**
     * 创建用户的组
     *
     * @param groupMapping 
     * @return java.lang.String
     * @author apr
     * @date 2021/10/20 8:45
     */
    String create(GroupDTO groupMapping);

    /**
     * 获取用户的组
     *
     * @param uid 
     * @return java.util.List<org.keycloak.representations.idm.GroupRepresentation>
     * @author apr
     * @date 2021/10/13 10:13
     */
    List<GroupRepresentation> getGroupsByUser(String uid) ;

    /**
     * 获取组的详情
     *
     * @param gid
     * @return java.util.List<org.keycloak.representations.idm.GroupRepresentation>
     * @author apr
     * @date 2021/10/13 10:13
     */
    GroupRepresentation getGroupById(String gid) ;

    /**
     * 获取组角色
     *
     * @param gid 
     * @return java.util.List
     * @author apr
     * @date 2021/10/13 10:39
     */
    List<RoleRepresentation> getGroupRoles(String gid) ;

    /**
     * 获取组成员
     *
     * @param gid 
     * @return java.util.List
     * @author apr
     * @date 2021/10/13 10:39
     */
    List<UserRepresentation> getGroupMembers(String gid) ;
    
    /**
     * 列出所有组
     *
     * @return
     * @author apr
     * @date 2021/10/13 10:23
     */
    List<GroupRepresentation> listGroups();

    /**
     * 向组中添加用户
     *
     * @param groupMappingDTO 
     * @return void
     * @author apr
     * @date 2021/10/13 11:04
     */
    void joinGroup(GroupMappingDTO groupMappingDTO);

    /**
     * 移除组中的用户
     *
     * @param groupMappingDTO 
     * @return void
     * @author apr
     * @date 2021/10/13 11:24
     */
    void leaveGroup(GroupMappingDTO groupMappingDTO);

    /**
     * 向组中添加角色
     *
     * @param groupDTO
     * @return void
     * @author apr
     * @date 2021/10/13 11:04
     */
    void joinGroup(GroupDTO groupDTO);

    /**
     * 移除组中的角色
     *
     * @param groupDTO
     * @return void
     * @author apr
     * @date 2021/10/13 11:24
     */
    void leaveGroup(GroupDTO groupDTO);

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
