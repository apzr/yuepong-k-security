package com.example.demo.services.impl;

import com.example.demo.dto.UserDTO;
import com.example.demo.services.AdminService;
import com.example.demo.services.MappingService;
import com.example.demo.services.UserService;
import org.apache.commons.lang.StringUtils;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.*;
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
public class UserServiceImpl implements UserService {

	@Autowired
	UsersResource usersResource;

	@Autowired
	RealmResource realmResource;

	@Autowired
	MappingService mappingService;

	@Autowired
	AdminService adminService;

    @Override
	public String createUser(UserDTO userDTO) {
		String uid = "-1";
		try {
			UserRepresentation user = copyProperty(userDTO, new UserRepresentation() );

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

			UserRepresentation user = copyProperty(userDTO, userToEdit.toRepresentation());

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
    	adminService.resetPassword("jjy123456", id);
	}

	@Override
	public UserDTO getUser(String uid) {
		UserResource userResource = usersResource.get(uid);
		UserDTO user = convertUser(userResource.toRepresentation());

		return user;
	}

	@Override
	public List<UserDTO> listUsers() {
		List<UserRepresentation> userRepresentations = usersResource.list();
		return userRepresentations.stream().map(userRepresentation -> convertUser(userRepresentation)).collect(Collectors.toList());
	}

	private UserRepresentation copyProperty(UserDTO userDTO, UserRepresentation userToEdit) {
		Map<String, List<String>> attributes = Optional.ofNullable(userToEdit.getAttributes()).orElse(new HashMap<>());

		//基本信息
		if(Objects.nonNull(userDTO.getUsername())){
			userToEdit.setUsername(userDTO.getUsername());
		}
		if(Objects.nonNull(userDTO.getEmail())){
			userToEdit.setEmail(userDTO.getEmail());
		}
		if(Objects.nonNull(userDTO.getFirstName())){
			userToEdit.setFirstName(userDTO.getFirstName());
		}
		if(Objects.nonNull(userDTO.getLastName())){
			userToEdit.setLastName(userDTO.getLastName());
		}
		if(Objects.nonNull(userDTO.getGender())){
			attributes.put("gender", Stream.of(userDTO.getGender()).collect(Collectors.toList()));
		}
		if(Objects.nonNull(userDTO.getEnabled())){
			userToEdit.setEnabled(userDTO.getEnabled());
		}
		if(Objects.nonNull(userDTO.getJobs())){
			attributes.put("jobs", Stream.of(userDTO.getJobs()).collect(Collectors.toList()));
		}
		if(Objects.nonNull(userDTO.getMobile())){
			attributes.put("mobile", Stream.of(userDTO.getMobile()).collect(Collectors.toList()));
		}
		if(Objects.nonNull(userDTO.getUpdateBy())){
			attributes.put("updateBy", Stream.of(userDTO.getUpdateBy()).collect(Collectors.toList()));
		}
		if(Objects.nonNull(userDTO.getUpdateAt())){
			Date d = userDTO.getUpdateAt();
			attributes.put("updateAt", Stream.of(d.getTime()+"").collect(Collectors.toList()));
		}

		userToEdit.setAttributes(attributes);

		return userToEdit;
	}

	private UserDTO convertUser(UserRepresentation userRepresentation) {
		UserDTO u = new UserDTO();
		u.setId(userRepresentation.getId());
		u.setUsername( userRepresentation.getUsername() );
		u.setEmail(userRepresentation.getEmail() );
		u.setFirstName(userRepresentation.getFirstName());
		u.setLastName(userRepresentation.getLastName());
		u.setEnabled(userRepresentation.isEnabled());
		u.setRole( mappingService.getMappingsByUser(userRepresentation.getId()) );

		Map<String, List<String>> attr = userRepresentation.getAttributes();
		if(Objects.nonNull(attr)){
			if(Objects.nonNull(attr.get("gender")))
				u.setGender( getAttr("gender", attr) );
			if(Objects.nonNull(attr.get("jobs")))
				u.setJobs( getAttr("jobs", attr) );
			if(Objects.nonNull(attr.get("mobile")))
				u.setMobile( getAttr("mobile", attr) );
			if(Objects.nonNull(attr.get("updateBy")))
				u.setUpdateBy( getAttr("updateBy", attr) );
			if(Objects.nonNull(attr.get("updateAt"))){
				Long date = new Long( attr.get("updateAt").get(0) );
				u.setUpdateAt( new Date(date) );
			}

		}

		return u;
	}

	private String getAttr(String attrName, Map<String, List<String>> attributes){
		String attr = "";
		if(Objects.nonNull(attrName) && Objects.nonNull(attributes))
			attr = StringUtils.join(attributes.get(attrName),", ");

		return Optional.ofNullable(attr).orElse("");
	}

}
