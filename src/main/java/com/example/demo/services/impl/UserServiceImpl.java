package com.example.demo.services.impl;

import com.example.demo.dto.*;
import com.example.demo.services.*;
import com.example.demo.util.Utils;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * UserServiceImpl
 * 用户
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 10:31:09
 **/
@Service
public class UserServiceImpl extends AdminServiceImpl implements UserService {
	@Autowired
	RoleService roleService;

	@Autowired
	MappingService mappingService;

	@Autowired
	GroupService groupService;

    @Override
	public String createUser(UserDTO userDTO) {
		String uid = "-1";
		try {
			UserRepresentation user = Utils.copyProperty(userDTO, new UserRepresentation() );

			// Create user
			Response result = usersResource.create(user);
			System.out.println("Keycloak create user response code>>>>" + result.getStatus());

			int statusId = result.getStatus();
			if (statusId >= 200 && statusId < 300) {

				System.out.println("Keycloak create user getLocation>>>>" + result.getLocation().getPath());

				uid = result.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

				// Define password credential
				CredentialRepresentation passwordCred = new CredentialRepresentation();
				passwordCred.setTemporary(false);
				passwordCred.setType(CredentialRepresentation.PASSWORD);
				passwordCred.setValue(userDTO.getPassword());

				// Set password credential
				usersResource.get(uid).resetPassword(passwordCred);

				//默认角色
				try{
					RoleRepresentation savedRoleRepresentation = realmResource.roles().get("user").toRepresentation();
					realmResource.users().get(uid).roles().realmLevel().add(Arrays.asList(savedRoleRepresentation));
				}catch(NotFoundException e){
					System.err.println("javax.ws.rs.NotFoundException: HTTP 404 Not Found");
				}

				System.out.println("Username==" + userDTO.getUsername() + " created in keycloak successfully");
			}

			else if (statusId == Response.Status.CONFLICT.getStatusCode()) {
				System.out.println("Username==" + userDTO.getUsername() + " already present in keycloak");
			} else {
				System.out.println("Username==" + userDTO.getUsername() + " could not be created in keycloak");
			}

		} catch (Exception e) {
			uid = "-1";
			e.printStackTrace();
		}

		return uid;
	}

	@Override
	public void updateUser(UserDTO userDTO) {
		try {
			UserResource userToEdit = usersResource.get(userDTO.getId());

			UserRepresentation user = Utils.copyProperty(userDTO, userToEdit.toRepresentation());

			// 修改信息
			userToEdit.update(user);

			//修改密码
			if(Objects.nonNull(userDTO.getPassword())){
				CredentialRepresentation passwordCred = new CredentialRepresentation();
				passwordCred.setTemporary(false);
				passwordCred.setType(CredentialRepresentation.PASSWORD);
				passwordCred.setValue(userDTO.getPassword());
				userToEdit.resetPassword(passwordCred);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String deleteUser(String id) {
		Response result = usersResource.delete(id);
		return result.getStatus()+"";
	}

	@Override
	public void resetUser(String id) {
    	resetPassword("jjy123456", id);
	}

	@Override
	public UserDTO getUser(String uid) {
		UserResource userResource = usersResource.get(uid);
		UserRepresentation userRepresentation = userResource.toRepresentation();
		UserDTO user = Utils.toUser(userRepresentation);

		List<MappingDTO> mappings = mappingService.getMappingsByUser(userRepresentation.getId());
		List<RoleDTO> roles = getRolesByMappings(mappings);
		user.setRole(roles);

		return user;
	}

	@Override
	public List<UserDTO> listUsers() {
		List<UserRepresentation> userRepresentations = usersResource.list();
		List<UserDTO> result = userRepresentations.stream().map(userRepresentation -> {
			UserDTO userDTO = Utils.toUser(userRepresentation);
			List<MappingDTO> mappings = mappingService.getMappingsByUser(userRepresentation.getId());
			List<RoleDTO> roles = getRolesByMappings(mappings);
			userDTO.setRole(roles);
			return userDTO;
		}).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDTO> pageUsers(String start, String size) {
    	Integer firstResult = Integer.parseInt(start);
		Integer maxResults = Integer.parseInt(size);
		List<UserRepresentation> userRepresentations = usersResource.list(firstResult, maxResults);

		List<UserDTO> result = userRepresentations.stream().map(userRepresentation -> {
			UserDTO userDTO = Utils.toUser(userRepresentation);
			List<MappingDTO> mappings = mappingService.getMappingsByUser(userRepresentation.getId());
			List<RoleDTO> roles = getRolesByMappings(mappings);
			userDTO.setRole(roles);
			return userDTO;
		}).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDTO> search(UserDTO conditions) {
		List<UserRepresentation> aa = usersResource.search("id:" + conditions.getId(), 0, 2);
		return null;
    }

	@Override
	public List listRoles(String uid) {
    	//mapping
		List<MappingDTO> mappings = mappingService.getMappingsByUser(uid);
		List<RoleDTO> roles = mappings.stream().map(mapping -> {
			//根据角色id获取角色对象
			return roleService.getRole(mapping.getRoleId());
		}).collect(Collectors.toList());

		//group
		List<GroupDTO> groups = groupService.getGroupsByUser(uid);
		groups.stream().forEach(group -> {
			group.getRealmRoles().forEach(realmRoleName -> {
				RoleDTO conditions = new RoleDTO();
				conditions.setName(realmRoleName);
				List<RoleDTO> groupRole = roleService.getRole(conditions);
				roles.addAll(groupRole);
			});
		});

		return roles;
	}

	private List<RoleDTO> getRolesByMappings(List<MappingDTO> mappings){
		List<RoleDTO> userRoles = new ArrayList();
		mappings.stream().forEach(mapping -> {
			RoleDTO role = roleService.getRole(mapping.getRoleId());
			userRoles.add(role);
		});

		return userRoles;
	}
}
