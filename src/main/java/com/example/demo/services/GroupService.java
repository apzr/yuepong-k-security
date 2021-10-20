package com.example.demo.services;

import com.example.demo.dto.GroupDTO;
import com.example.demo.dto.GroupMappingDTO;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

/**
 * GroupService
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 10:31:32
 **/
public interface GroupService {
  /**
     * 创建用户的组
     *
     * @param group
     * @return java.lang.String
     * @author apr
     * @date 2021/10/20 8:45
     */
    String create(GroupRepresentation group);

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
     * getGroupByName
     *
     * @param conditions
     * @return org.keycloak.representations.idm.GroupRepresentation
     * @author apr
     * @date 2021/10/20 15:00
     */
    GroupRepresentation search(GroupRepresentation conditions);
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


}
