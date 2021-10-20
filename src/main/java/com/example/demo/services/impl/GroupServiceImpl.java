package com.example.demo.services.impl;

import com.example.demo.dto.GroupDTO;
import com.example.demo.dto.GroupMappingDTO;
import com.example.demo.services.GroupService;
import com.yuepong.jdev.exception.BizException;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
public class GroupServiceImpl implements GroupService {

	@Autowired
	GroupsResource groupsResource;

	@Autowired
	UsersResource usersResource;

	@Autowired
	RoleByIdResource rolesByIdResource;

	@Override
	public String create(GroupRepresentation group) {
		Response result = groupsResource.add(group);
		int statusId = result.getStatus();
		if (statusId >= 200 && statusId < 300) {
			return result.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
		}else {
			throw new BizException("创建失败: "+statusId);
		}
	}

	@Override
	public List<GroupRepresentation> getGroupsByUser(String uid) {
		return usersResource.get(uid).groups();
	}

	@Override
	public GroupRepresentation getGroupById(String gid) {
		return groupsResource.group(gid).toRepresentation();
	}

	@Override
	public GroupRepresentation getGroupByName(String group_name) {
		List<GroupRepresentation> result = groupsResource.groups(group_name, 1, 1);
		return Optional.ofNullable(result).orElse(new ArrayList<GroupRepresentation>()).get(0);
	}

	@Override
	public List<RoleRepresentation> getGroupRoles(String gid) {
		RoleMappingResource rolesResource = groupsResource.group(gid).roles();

		return rolesResource.realmLevel().listAll();
	}

	@Override
	public List<UserRepresentation> getGroupMembers(String gid) {
		return groupsResource.group(gid).members();
	}

	@Override
	public List<GroupRepresentation> listGroups() {
		List<GroupRepresentation> gs = new ArrayList<GroupRepresentation>();
		groupsResource.groups().stream().forEach(group -> {
			GroupRepresentation gr = getGroupById(group.getId());
			gs.add(gr);
		});
		//return groupsResource.groups(); //lazy
		return gs;
	}

	@Override
	public void joinGroup(GroupMappingDTO groupMappingDTO) {
		UserResource userResource = usersResource.get(groupMappingDTO.getUserId());
		userResource.joinGroup(groupMappingDTO.getGroupId());
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

    private  List<RoleRepresentation> getRolesByIds(List<String> ids){
		List<RoleRepresentation> roles = ids.stream()
				.map(id -> getRoleById(id))
				.filter(role -> Objects.nonNull(role))
				.collect(Collectors.toList());

		return roles;
	}
}
