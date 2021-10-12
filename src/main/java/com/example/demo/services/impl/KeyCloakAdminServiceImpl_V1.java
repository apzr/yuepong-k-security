package com.example.demo.services.impl;

import com.example.demo.dto.MappingDTO;
import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.UserCredentials;
import com.example.demo.dto.UserDTO;
import com.example.demo.services.KeyCloakAdminService_V0;
import com.example.demo.services.KeyCloakAdminService_V1;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ImplBak
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/26 11:12:45
 **/
@Service
public class KeyCloakAdminServiceImpl_V1 implements KeyCloakAdminService_V1 {

	private String SECRETKEY = "keycloak";

	@Value("${keycloak.resource}")
	private String CLIENTID;

	@Value("${keycloak.auth-server-url}")
	private String AUTHURL;

	@Value("${keycloak.realm}")
	private String REALM;

	public String getToken(UserCredentials userCredentials) {
		String responseToken = null;
		try {
			String username = userCredentials.getUsername();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("grant_type", "password"));
			urlParameters.add(new BasicNameValuePair("client_id", CLIENTID));
			urlParameters.add(new BasicNameValuePair("username", username));
			urlParameters.add(new BasicNameValuePair("password", userCredentials.getPassword()));
			urlParameters.add(new BasicNameValuePair("client_secret", SECRETKEY));
			responseToken = sendPost(urlParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseToken;
	}

	public String getByRefreshToken(String refreshToken) {
		String responseToken = null;
		try {
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
			urlParameters.add(new BasicNameValuePair("client_id", CLIENTID));
			urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
			urlParameters.add(new BasicNameValuePair("client_secret", SECRETKEY));
			responseToken = sendPost(urlParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseToken;
	}

	@Override
	public List<UserDTO> getUsersInKeyCloak() {
		UsersResource usersResource = getKeycloakUserResource();
		List<UserRepresentation> userRepresentations = usersResource.list();
		return userRepresentations.stream().map(userRepresentation -> convertUser(userRepresentation)).collect(Collectors.toList());
	}

	@Override
	public UserDTO getUserInKeyCloak(String uid) {
		UsersResource usersResource = getKeycloakUserResource();
		UserResource userResource = usersResource.get(uid);
		UserDTO user = convertUser(userResource.toRepresentation());
		Map<String, List<String>> attrs = userResource.toRepresentation().getAttributes();
		user.setAttrs(attrs);

		return user;
	}

	public String createUserInKeyCloak(UserDTO userDTO) {
		String uid = "0";
		try {
			UsersResource userResource = getKeycloakUserResource();
			UserRepresentation user = new UserRepresentation();
			user.setUsername(userDTO.getUserName());
			user.setEmail(userDTO.getEmailAddress());
			user.setFirstName(userDTO.getFirstName());
			user.setLastName(userDTO.getLastName());
			user.setEnabled(true);

			// Create user
			Response result = userResource.create(user);
			System.out.println("Keycloak create user response code>>>>" + result.getStatus());

			int statusId = result.getStatus();
			if (statusId == Response.Status.OK.getStatusCode()) {

				System.out.println("Keycloak create user getLocation>>>>" + result.getLocation().getPath());

				uid = result.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

				// Define password credential
				CredentialRepresentation passwordCred = new CredentialRepresentation();
				passwordCred.setTemporary(false);
				passwordCred.setType(CredentialRepresentation.PASSWORD);
				passwordCred.setValue(userDTO.getPassword());

				// Set password credential
				userResource.get(uid).resetPassword(passwordCred);

				//默认角色
				try{
					RealmResource realmResource = getRealmResource();
					RoleRepresentation savedRoleRepresentation = realmResource.roles().get("user").toRepresentation();
					realmResource.users().get(uid).roles().realmLevel().add(Arrays.asList(savedRoleRepresentation));
				}catch(NotFoundException e){
					System.err.println("javax.ws.rs.NotFoundException: HTTP 404 Not Found");
				}

				System.out.println("Username==" + userDTO.getUserName() + " created in keycloak successfully");
			}

			else if (statusId == Response.Status.CONFLICT.getStatusCode()) {
				System.out.println("Username==" + userDTO.getUserName() + " already present in keycloak");
			} else {
				System.out.println("Username==" + userDTO.getUserName() + " could not be created in keycloak");
			}

		} catch (Exception e) {
			uid = "-1";
			e.printStackTrace();
		}

		return uid;
	}

	@Override
	public void updateUserInKeyCloak(UserDTO userDTO) {
		try {
			UsersResource userResource = getKeycloakUserResource();
			UserResource userToEdit = userResource.get(userDTO.getId());

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
	public String deleteUserInKeyCloak(String id) {
		UsersResource usersResource = getKeycloakUserResource();
		Response result = usersResource.delete(id);
		return result.getStatus()+"";
	}

	@Override
	public List<RoleDTO> getRolesInKeyCloak() {
		RolesResource rolesResource = getKeycloakRoleResource();
		List<RoleRepresentation> roleRepresentations = rolesResource.list();
		return roleRepresentations.stream().map(roleRepresentation -> convertRole(roleRepresentation)).collect(Collectors.toList());
	}

	public int createRoleInKeyCloak(RoleDTO roleDTO) {
		int statusId = 0;
		try {
			RolesResource rolesResource = getKeycloakRoleResource();
			RoleRepresentation role = new RoleRepresentation();
			role.setName(roleDTO.getName());
			role.setDescription(roleDTO.getDescription());
			role.setClientRole(true);

			// Create role
			rolesResource.create(role);

		} catch (Exception e) {
			statusId = -1;
			e.printStackTrace();
		}

		return statusId;

	}

	@Override
	public void deleteRolesInKeyCloak(RoleDTO roleDTO) {
		RolesResource rolesResource = getKeycloakRoleResource();
		rolesResource.deleteRole(roleDTO.getName());
	}

	@Override
	public void updateRolesInKeyCloak(RoleDTO roleDTO) {
		RolesResource rolesResource = getKeycloakRoleResource();
		//TODO: 更新角色
	}

	public int createMappingInKeyCloak(MappingDTO mappingDTO) {
		int statusId = 0;

		try {
			RealmResource realmResource = getRealmResource();
			String clientUUID = realmResource.clients().findByClientId("localhost8888").get(0).getClientId();

			RolesResource rolesResource = getKeycloakRoleResource();
			List<RoleRepresentation> rolesToAdd = rolesResource.list().stream()
					.filter(role -> mappingDTO.getRoleName().equals(role.getName())).collect(Collectors.toList());

			UsersResource usersResource = getKeycloakUserResource();
			List<UserRepresentation> users = usersResource.list().stream().filter(user -> mappingDTO.getUserId().equals(user.getId())).collect(Collectors.toList());

			RoleMappingResource roleMappingResource = getKeycloakRoleMappingResource(usersResource, mappingDTO.getUserId());
			roleMappingResource.realmLevel().add(rolesToAdd);
			//usersResource.get("").roles().clientLevel(clientUUID).add(rolesToAdd);

		} catch (Exception e) {
			statusId = -1;
			e.printStackTrace();
		}

		return statusId;
	}

	@Override
	public List<MappingDTO> getMappingsByUser(String uid) {

		UsersResource usersResource = getKeycloakUserResource();
		RoleMappingResource roleMappingResource = getKeycloakRoleMappingResource(usersResource, uid);
		List<RoleRepresentation> aa = roleMappingResource.realmLevel().listAll();

		return aa.stream().map(roleRepresentation -> toMappingDto(roleRepresentation, uid)).collect(Collectors.toList());
	}

	@Override
	public void deleteMappingsByUser(MappingDTO mapping) {
		UsersResource usersResource = getKeycloakUserResource();
		//TODO: 删除

		//List<RoleRepresentation> aa = roleMappingResource.realmLevel().remove();
	}

	// after logout user from the keycloak system. No new access token will be issued.
	public void logoutUser(String userId) {
		UsersResource userResource = getKeycloakUserResource();
		userResource.get(userId).logout();
	}

	// Reset passowrd
	public void resetPassword(String newPassword, String userId) {
		UsersResource userResource = getKeycloakUserResource();

		// Define password credential
		CredentialRepresentation passwordCred = new CredentialRepresentation();
		passwordCred.setTemporary(false);
		passwordCred.setType(CredentialRepresentation.PASSWORD);
		passwordCred.setValue(newPassword.toString().trim());

		// Set password credential
		userResource.get(userId).resetPassword(passwordCred);

	}

	private UserRepresentation copyProperty(UserDTO userDTO, UserRepresentation userToEdit) {
		//基本信息
		if(Objects.nonNull(userDTO.getUserName())){
			userToEdit.setUsername(userDTO.getUserName());
		}
		if(Objects.nonNull(userDTO.getEmailAddress())){
			userToEdit.setEmail(userDTO.getEmailAddress());
		}
		if(Objects.nonNull(userDTO.getFirstName())){
			userToEdit.setFirstName(userDTO.getFirstName());
		}
		if(Objects.nonNull(userDTO.getLastName())){
			userToEdit.setLastName(userDTO.getLastName());
		}

		//其他属性
		if(Objects.nonNull(userDTO.getAttrs())){
			//TODO:这里是覆盖, 要做成检测相同的覆盖 其余的保留, 即为合并
			userToEdit.setAttributes(userDTO.getAttrs());
		}

		return userToEdit;
	}

	private UserDTO convertUser(UserRepresentation userRepresentation) {
		UserDTO u = new UserDTO();
		u.setId(userRepresentation.getId());
		u.setUserName( userRepresentation.getUsername() );
		u.setEmailAddress(userRepresentation.getEmail() );
		u.setFirstName(userRepresentation.getFirstName());
		u.setLastName(userRepresentation.getLastName());
		return u;
	}

	private RoleDTO convertRole(RoleRepresentation roleRepresentation) {
		RoleDTO r = new RoleDTO();
		r.setName( roleRepresentation.getName() );
		r.setDescription( roleRepresentation.getDescription() );
		r.setClientRole( roleRepresentation.getClientRole() );
		return r;
	}

	private UsersResource getKeycloakUserResource() {
		Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTHURL).realm("master").username("admin").password("admin")
				.clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				.build();

		RealmResource realmResource = kc.realm(REALM);
		UsersResource userResource = realmResource.users();

		return userResource;
	}

	private RolesResource getKeycloakRoleResource() {
		Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTHURL).realm("master").username("admin").password("admin")
				.clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				.build();

		RealmResource realmResource = kc.realm(REALM);
		RolesResource roleResource = realmResource.roles();

		return roleResource;
	}

	private RoleMappingResource getKeycloakRoleMappingResource(UsersResource usersResource, String userId) {
		return usersResource.get(userId).roles();
	}

	private RealmResource getRealmResource() {
		Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTHURL).realm("master").username("admin").password("admin")
				.clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				.build();

		RealmResource realmResource = kc.realm(REALM);
		return realmResource;
	}

	private String sendPost(List<NameValuePair> urlParameters) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/token");

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		HttpResponse response = client.execute(post);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	private MappingDTO toMappingDto(RoleRepresentation roleRepresentation, String uid) {
		MappingDTO m = new MappingDTO();
		m.setUserId(uid);
		m.setRoleId(roleRepresentation.getId());
		m.setRoleName(roleRepresentation.getName());

		return m;
	}
}
