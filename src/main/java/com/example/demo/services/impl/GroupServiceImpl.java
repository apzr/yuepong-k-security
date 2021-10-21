package com.example.demo.services.impl;

import com.example.demo.dto.*;
import com.example.demo.services.GroupService;
import com.example.demo.util.Utils;
import com.yuepong.jdev.exception.BizException;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * GroupServiceImpl
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 10:31:32
 **/
@Service
public class GroupServiceImpl extends AdminServiceImpl implements GroupService {

	@Override
	public String create(GroupDTO groupDTO) {
		//删除现有
		try{
			GroupDTO conditions = new GroupDTO();
			conditions.setName(groupDTO.getName());
			List<GroupDTO> g = this.search(conditions);
			if(Objects.nonNull(g) && !g.isEmpty())
				groupsResource.group(g.get(0).getId()).remove();
		}catch(Exception e){
			throw new BizException("删除原有的组失败: "+e.getMessage());
		}

		//新增
		GroupRepresentation group = Utils.copyProperty(groupDTO, new GroupRepresentation());
		Response result = groupsResource.add(group);
		int statusId = result.getStatus();
		if (statusId >= 200 && statusId < 300) {
			return result.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
		}else {
			throw new BizException("创建失败: "+statusId);
		}
	}

	@Override
	public List<GroupDTO> getGroupsByUser(String uid) {
		List<GroupRepresentation> group = usersResource.get(uid).groups();
		List<GroupDTO> groupDetails = group.stream().map(g -> getGroupById(g.getId())).collect(Collectors.toList());
		return groupDetails;
	}

	@Override
	public GroupDTO getGroupById(String gid) {
		GroupRepresentation g = groupsResource.group(gid).toRepresentation();
		return Utils.toGroup(g);
	}

	@Override
	public List<GroupDTO> search(GroupDTO conditions){
		GroupRepresentation groupDTO = null;

		List<GroupRepresentation> resultList = groupsResource.groups().stream()
				.filter(group -> Utils.filterByName(conditions, group))
				.filter(result -> {
					GroupDTO g = getGroupById(result.getId());
					return Utils.filterByType(conditions, g);
				})
				.collect(Collectors.toList());

		//roll
		if(Objects.nonNull(resultList) ){
			return resultList.stream().map(rl -> {
				GroupRepresentation groupTmp = groupsResource.group(rl.getId()).toRepresentation();
				List<String> roleIds = groupTmp.getRealmRoles().stream().map(roleName -> getRoleIdByName(roleName)).collect(Collectors.toList());
				groupTmp.setRealmRoles(roleIds);
				return groupTmp;
			}).map(Utils::toGroup).collect(Collectors.toList());
		}

		return null;
	}

	@Override
	public List<RoleDTO> getGroupRoles(String gid) {
		RoleMappingResource rolesResource = groupsResource.group(gid).roles();
		List<RoleRepresentation> roles = rolesResource.realmLevel().listAll();
		return roles.stream().map(Utils::toRole).collect(Collectors.toList());
	}

	@Override
	public List<UserDTO> getGroupMembers(String gid) {
		List<UserRepresentation> users = groupsResource.group(gid).members();
		return users.stream().map(Utils::toUser).collect(Collectors.toList());
	}

	@Override
	public List<GroupDTO> listGroups() {
		List<GroupDTO> gs = new ArrayList();
		groupsResource.groups().stream().forEach(group -> {
			GroupDTO gr = getGroupById(group.getId());
			gs.add(gr);
		});
		//return groupsResource.groups(); //lazy
		return gs;
	}

	@Override
	public void joinGroup(GroupMappingsDTO groupMappingsDTO) {
		groupMappingsDTO.getUserIds().stream().forEach(uid -> {
			UserResource userResource = usersResource.get(uid);
			userResource.joinGroup(groupMappingsDTO.getGroupId());
		});
	}

	@Override
	public void leaveGroup(GroupMappingDTO groupMappingDTO) {
		UserResource userResource = usersResource.get(groupMappingDTO.getUserId());
		userResource.leaveGroup(groupMappingDTO.getGroupId());
	}

	@Override
	public void joinGroup(GroupDTO groupDTO) {
		List<RoleRepresentation> rolesToAdd = getRolesByIds(groupDTO.getRole_ids());
		GroupResource group = groupsResource.group(groupDTO.getId());
		RoleMappingResource rolesMappingResource = group.roles();
		rolesMappingResource.realmLevel().add(rolesToAdd);
	}

	@Override
	public void leaveGroup(GroupDTO groupDTO) {
		List<RoleRepresentation> rolesToRemove = getRolesByIds(groupDTO.getRole_ids());
		GroupResource group = groupsResource.group(groupDTO.getId());
		RoleMappingResource rolesMappingResource = group.roles();
		rolesMappingResource.realmLevel().remove(rolesToRemove);
	}

    private  RoleRepresentation getRoleById(String id){
		RoleRepresentation roleRepresentation = null;
		try {
			roleRepresentation = rolesByIdResource.getRole(id);
		} catch (Exception e) {
			roleRepresentation = null;
		}

		return roleRepresentation;
	}

	private  String getRoleIdByName(String name){
		String roleId;
		try {
			RoleResource roleResource = rolesResource.get(name);
			roleId = roleResource.toRepresentation().getId();
		} catch (Exception e) {
			roleId = "";
		}

		return roleId;
	}

    private  List<RoleRepresentation> getRolesByIds(List<String> ids){
		List<RoleRepresentation> roles = ids.stream()
				.map(id -> getRoleById(id))
				.filter(role -> Objects.nonNull(role))
				.collect(Collectors.toList());

		return roles;
	}
}
