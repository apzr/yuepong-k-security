package com.example.demo.services.impl;

import com.example.demo.dto.MappingDTO;
import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.UserCredentials;
import com.example.demo.dto.UserDTO;
import com.example.demo.services.AdminService;
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
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

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
public class AdminServiceImpl implements AdminService {

	private String SECRET_KEY = "keycloak";

	@Value("${keycloak.resource}")
	private String CLIENT_ID;

	@Value("${keycloak.auth-server-url}")
	private String AUTH_URL;

	@Value("${keycloak.realm}")
	private String REALM;

	public String getToken(UserCredentials userCredentials) {
		String responseToken = null;
		try {
			String username = userCredentials.getUsername();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("grant_type", "password"));
			urlParameters.add(new BasicNameValuePair("client_id", CLIENT_ID));
			urlParameters.add(new BasicNameValuePair("username", username));
			urlParameters.add(new BasicNameValuePair("password", userCredentials.getPassword()));
			urlParameters.add(new BasicNameValuePair("client_secret", SECRET_KEY));
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
			urlParameters.add(new BasicNameValuePair("client_id", CLIENT_ID));
			urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
			urlParameters.add(new BasicNameValuePair("client_secret", SECRET_KEY));
			responseToken = sendPost(urlParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseToken;
	}

	@Override
	public List<UserDTO> listUsers() {
		UsersResource usersResource = getKeycloakUserResource();
		List<UserRepresentation> userRepresentations = usersResource.list();
		return userRepresentations.stream().map(userRepresentation -> convertUser(userRepresentation)).collect(Collectors.toList());
	}

	@Override
	public UserDTO getUser(String uid) {
		UsersResource usersResource = getKeycloakUserResource();
		UserResource userResource = usersResource.get(uid);
		UserDTO user = convertUser(userResource.toRepresentation());
		Map<String, List<String>> attrs = userResource.toRepresentation().getAttributes();
		user.setAttrs(attrs);

		return user;
	}

	public String createUser(UserDTO userDTO) {
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
	public void updateUser(UserDTO userDTO) {
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
	public String deleteUser(String id) {
		UsersResource usersResource = getKeycloakUserResource();
		Response result = usersResource.delete(id);
		return result.getStatus()+"";
	}

	public int createRole(RoleDTO roleDTO) {
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
	public void updateRole(RoleDTO roleDTO) {
		RolesResource rolesResource = getKeycloakRoleResource();
		List<RoleRepresentation> roleToEditList = rolesResource
				.list().stream()
				.filter(rr -> roleDTO.getId().equals(rr.getId()))
				.collect(Collectors.toList());
		if(Objects.nonNull(roleToEditList) && !roleToEditList.isEmpty()){
			RoleRepresentation roleToEdit = roleToEditList.get(0);
			RoleResource roleResource = rolesResource.get(roleToEdit.getName());
			roleResource.update( copyProperty(roleDTO, roleToEdit) );
		}
	}

	@Override
	public void deleteRole(String name) {
		RolesResource rolesResource = getKeycloakRoleResource();
		rolesResource.deleteRole(name);
	}

	@Override
	public RoleDTO getRole(String name) {
		RolesResource rolesResource = getKeycloakRoleResource();
		RoleResource roleResource = rolesResource.get(name);
		return convertRole(roleResource.toRepresentation());
	}

	@Override
	public List<RoleDTO> listRoles() {
		RolesResource rolesResource = getKeycloakRoleResource();
		List<RoleRepresentation> roleRepresentations = rolesResource.list();
		return roleRepresentations.stream().map(roleRepresentation -> convertRole(roleRepresentation)).collect(Collectors.toList());
	}

	@Override
	public int createMapping(MappingDTO mappingDTO) {
		int statusId = 0;

		try {
			RolesResource rolesResource = getKeycloakRoleResource();
			List<RoleRepresentation> rolesToAdd = rolesResource
					.list().stream()
					.filter(role -> mappingDTO.getRoleId().equals(role.getId()))
					.collect(Collectors.toList());

			UsersResource usersResource = getKeycloakUserResource();
			RoleMappingResource roleMappingResource = getKeycloakRoleMappingResource(usersResource, mappingDTO.getUserId());
			roleMappingResource.realmLevel().add(rolesToAdd);
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
		List<RoleRepresentation> rm = roleMappingResource.realmLevel().listAll();

		return rm.stream().map(roleRepresentation -> toMappingDto(roleRepresentation, uid)).collect(Collectors.toList());
	}

	@Override
	public List<MappingDTO> listMappings() {
		List<MappingDTO> allMappings = new ArrayList();
		UsersResource usersResource = getKeycloakUserResource();
		usersResource.list().stream().forEach(userResource -> {
			RoleMappingResource roleMappingResource = getKeycloakRoleMappingResource(usersResource, userResource.getId());
			UserRepresentation userRepresentation = usersResource.get(userResource.getId()).toRepresentation();
			List<MappingDTO> userMappings = roleMappingResource
					.realmLevel()
					.listAll().stream()
					.map(roleRepresentation -> toMappingDto(roleRepresentation, userRepresentation))
					.collect(Collectors.toList());

			allMappings.addAll(userMappings);
		});

		return allMappings;
	}

	@Override
	public void deleteMapping(MappingDTO mappingDTO) {
		try {
			RolesResource rolesResource = getKeycloakRoleResource();
			List<RoleRepresentation> rolesToRemove = rolesResource
					.list().stream()
					.filter(role -> mappingDTO.getRoleId().equals(role.getId()))
					.collect(Collectors.toList());

			UsersResource usersResource = getKeycloakUserResource();
			RoleMappingResource roleMappingResource = getKeycloakRoleMappingResource(usersResource, mappingDTO.getUserId());
			roleMappingResource.realmLevel().remove(rolesToRemove);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private RoleRepresentation copyProperty(RoleDTO roleDTO, RoleRepresentation roleToEdit) {
		//基本信息
		if(Objects.nonNull(roleDTO.getName())){
			roleToEdit.setName(roleDTO.getName());
		}
		if(Objects.nonNull(roleDTO.getDescription())){
			roleToEdit.setDescription(roleDTO.getDescription());
		}
		if(Objects.nonNull(roleDTO.getClientRole())){
			roleToEdit.setClientRole(roleDTO.getClientRole());
		}

		return roleToEdit;
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
		r.setId( roleRepresentation.getId() );
		r.setName( roleRepresentation.getName() );
		r.setDescription( roleRepresentation.getDescription() );
		r.setClientRole( roleRepresentation.getClientRole() );
		return r;
	}

	private UsersResource getKeycloakUserResource() {
		Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTH_URL).realm("master").username("admin").password("admin")
				.clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				.build();

		RealmResource realmResource = kc.realm(REALM);
		UsersResource userResource = realmResource.users();

		return userResource;
	}

	private RolesResource getKeycloakRoleResource() {
		Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTH_URL).realm("master").username("admin").password("admin")
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
		Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTH_URL).realm("master").username("admin").password("admin")
				.clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				.build();

		RealmResource realmResource = kc.realm(REALM);
		return realmResource;
	}

	private String sendPost(List<NameValuePair> urlParameters) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(AUTH_URL + "/realms/" + REALM + "/protocol/openid-connect/token");

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

	private MappingDTO toMappingDto(RoleRepresentation roleRepresentation, UserRepresentation user) {
		MappingDTO m = toMappingDto(roleRepresentation, user.getId());
		m.setUserName(user.getUsername());
		return m;
	}

	private MappingDTO toMappingDto(RoleRepresentation roleRepresentation, String uid) {
		MappingDTO m = new MappingDTO();
		m.setUserId(uid);
		m.setRoleId(roleRepresentation.getId());
		m.setRoleName(roleRepresentation.getName());

		return m;
	}
}
