package com.example.demo.services.impl;

import com.example.demo.dto.*;
import com.example.demo.services.AdminService;
import org.apache.commons.lang.StringUtils;
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
import org.keycloak.representations.idm.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		UsersResource usersResource = getKeycloakUsersResource();
		List<UserRepresentation> userRepresentations = usersResource.list();
		return userRepresentations.stream().map(userRepresentation -> convertUser(userRepresentation)).collect(Collectors.toList());
	}

	@Override
	public UserDTO getUser(String uid) {
		UsersResource usersResource = getKeycloakUsersResource();
		UserResource userResource = usersResource.get(uid);
		UserDTO user = convertUser(userResource.toRepresentation());

		return user;
	}

	public String createUser(UserDTO userDTO) {
		String uid = "-1";
		try {
			UsersResource userResource = getKeycloakUsersResource();

			UserRepresentation user = copyProperty(userDTO, new UserRepresentation() );

			// Create user
			Response result = userResource.create(user);
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
				userResource.get(uid).resetPassword(passwordCred);

				//默认角色
				try{
					RealmResource realmResource = getRealmResource();
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
			UsersResource userResource = getKeycloakUsersResource();
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
		UsersResource usersResource = getKeycloakUsersResource();
		Response result = usersResource.delete(id);
		return result.getStatus()+"";
	}

	@Override
	public void resetUser(String id) {
		UsersResource userResource = getKeycloakUsersResource();
		// Define password credential
		CredentialRepresentation passwordCred = new CredentialRepresentation();
		passwordCred.setTemporary(false);
		passwordCred.setType(CredentialRepresentation.PASSWORD);
		passwordCred.setValue("jjy123456");

		// Set password credential
		userResource.get(id).resetPassword(passwordCred);
	}

	@Override
	public String createRole(RoleDTO roleDTO) {
		RolesResource rolesResource = getKeycloakRoleResource();

		// Create role
		RoleRepresentation role = copyProperty(roleDTO, new RoleRepresentation());
		rolesResource.create(role);

		RoleResource roleResource = rolesResource.get(roleDTO.getName());
		return roleResource.toRepresentation().getId();
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
	public List<RoleDTO> getRole(RoleDTO conditions) {
		RolesResource rolesResource = getKeycloakRoleResource();
		List<RoleRepresentation> roles = getAllRoles();
		List<RoleRepresentation> filterRoles = roles.stream()
				.filter(role -> matchRole(role, conditions))
				.collect(Collectors.toList());

		return filterRoles.stream().map(role -> convertRole(role)).collect(Collectors.toList());
	}

	@Override
	public List<RoleDTO> listRoles() {
		RolesResource rolesResource = getKeycloakRoleResource();
		List<RoleRepresentation> roleRepresentations = getAllRoles();
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

			UsersResource usersResource = getKeycloakUsersResource();
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
		UsersResource usersResource = getKeycloakUsersResource();
		RoleMappingResource roleMappingResource = getKeycloakRoleMappingResource(usersResource, uid);
		List<RoleRepresentation> rm = roleMappingResource.realmLevel().listAll();

		return rm.stream().map(roleRepresentation -> toMappingDto(roleRepresentation, uid)).collect(Collectors.toList());
	}

	@Override
	public List<MappingDTO> listMappings() {
		List<MappingDTO> allMappings = new ArrayList();
		UsersResource usersResource = getKeycloakUsersResource();
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

			UsersResource usersResource = getKeycloakUsersResource();
			RoleMappingResource roleMappingResource = getKeycloakRoleMappingResource(usersResource, mappingDTO.getUserId());
			roleMappingResource.realmLevel().remove(rolesToRemove);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<GroupRepresentation> getGroupsByUser(String uid) {
		UsersResource usersResource = getKeycloakUsersResource();
		return usersResource.get(uid).groups();
	}

	@Override
	public GroupRepresentation getGroupById(String gid) {
		GroupsResource groupsResource = getKeycloakGroupsResource();
		return groupsResource.group(gid).toRepresentation();
	}

	@Override
	public List<RoleRepresentation> getGroupRoles(String gid) {
		GroupsResource groupsResource = getKeycloakGroupsResource();
		RoleMappingResource rolesResource = groupsResource.group(gid).roles();
		return rolesResource.realmLevel().listAll();
	}

	@Override
	public List<UserRepresentation> getGroupMembers(String gid) {
		GroupsResource groupsResource = getKeycloakGroupsResource();
		return groupsResource.group(gid).members();
	}

	@Override
	public List<GroupRepresentation> listGroups() {
		GroupsResource groupsResource = getKeycloakGroupsResource();
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
		UsersResource usersResource = getKeycloakUsersResource();
		UserResource userResource = usersResource.get(groupMappingDTO.getUserId());
		userResource.joinGroup(groupMappingDTO.getGroupId());
	}

	@Override
	public void leaveGroup(GroupMappingDTO groupMappingDTO) {
		UsersResource usersResource = getKeycloakUsersResource();
		UserResource userResource = usersResource.get(groupMappingDTO.getUserId());
		userResource.leaveGroup(groupMappingDTO.getGroupId());
	}

	// after logout user from the keycloak system. No new access token will be issued.
	public void logoutUser(String userId) {
		UsersResource userResource = getKeycloakUsersResource();
		userResource.get(userId).logout();
	}

	// Reset passowrd
	public void resetPassword(String newPassword, String userId) {
		UsersResource userResource = getKeycloakUsersResource();

		// Define password credential
		CredentialRepresentation passwordCred = new CredentialRepresentation();
		passwordCred.setTemporary(false);
		passwordCred.setType(CredentialRepresentation.PASSWORD);
		passwordCred.setValue(newPassword.toString().trim());

		// Set password credential
		userResource.get(userId).resetPassword(passwordCred);

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

	private RoleRepresentation copyProperty(RoleDTO roleDTO, RoleRepresentation roleToEdit) {
		Map<String, List<String>> attributes = Optional.ofNullable(roleToEdit.getAttributes()).orElse(new HashMap<>());

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

		//attr
		if(Objects.nonNull(roleDTO.getDisplayName()))
			attributes.put("displayName", Stream.of(roleDTO.getDisplayName()).collect(Collectors.toList()));
		if(Objects.nonNull(roleDTO.getIndex()))
			attributes.put("index", Stream.of(roleDTO.getIndex()).collect(Collectors.toList()));
		if(Objects.nonNull(roleDTO.getIcon()))
			attributes.put("icon", Stream.of(roleDTO.getIcon()).collect(Collectors.toList()));
		if(Objects.nonNull(roleDTO.getUrl()))
			attributes.put("url", Stream.of(roleDTO.getUrl()).collect(Collectors.toList()));
		if(Objects.nonNull(roleDTO.getType()))
			attributes.put("type", Stream.of(roleDTO.getType()).collect(Collectors.toList()));
		if(Objects.nonNull(roleDTO.getParent()))
			attributes.put("parent", Stream.of(roleDTO.getParent()).collect(Collectors.toList()));

		roleToEdit.setAttributes(attributes);

		return roleToEdit;
	}

	private UserDTO convertUser(UserRepresentation userRepresentation) {
		UserDTO u = new UserDTO();
		u.setId(userRepresentation.getId());
		u.setUsername( userRepresentation.getUsername() );
		u.setEmail(userRepresentation.getEmail() );
		u.setFirstName(userRepresentation.getFirstName());
		u.setLastName(userRepresentation.getLastName());
		u.setEnabled(userRepresentation.isEnabled());
		u.setRole( getMappingsByUser(userRepresentation.getId()) );

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

	private RoleDTO convertRole(RoleRepresentation roleRepresentation) {
		RoleDTO r = new RoleDTO();
		r.setId( roleRepresentation.getId() );
		r.setName( roleRepresentation.getName() );
		r.setDescription( roleRepresentation.getDescription() );
		r.setClientRole( roleRepresentation.getClientRole() );

		Map<String, List<String>> attr = roleRepresentation.getAttributes();
		if(Objects.nonNull(attr)){
			if(Objects.nonNull(attr.get("displayName")))
				r.setDisplayName( getAttr("displayName", attr) );
			if(Objects.nonNull(attr.get("index")))
				r.setIndex( getAttr("index", attr) );
			if(Objects.nonNull(attr.get("icon")))
				r.setIcon( getAttr("icon", attr) );
			if(Objects.nonNull(attr.get("url")))
				r.setUrl( getAttr("url", attr) );
			if(Objects.nonNull(attr.get("type")))
				r.setType( getAttr("type", attr) );
			if(Objects.nonNull(attr.get("parent")))
				r.setType( getAttr("parent", attr) );
		}
		return r;
	}

	private UsersResource getKeycloakUsersResource() {
		Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTH_URL).realm("master").username("admin").password("admin")
				.clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				.build();

		RealmResource realmResource = kc.realm(REALM);

		return realmResource.users();
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

	private GroupsResource getKeycloakGroupsResource() {
		Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTH_URL).realm("master").username("admin").password("admin")
				.clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				.build();

		RealmResource realmResource = kc.realm(REALM);

		return realmResource.groups();
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
		m.setDescription(roleRepresentation.getDescription());

		return m;
	}

	/**
	 * 角色条件查询
	 *
	 * @param role
     * @param conditions
	 * @return boolean
	 * @author apr
	 * @date 2021/10/19 9:56
	 */
	private boolean matchRole(RoleRepresentation role, RoleDTO conditions) {
		if(Objects.isNull(conditions))
			return false;

		Map<String, List<String>> attr = role.getAttributes();

		boolean idMatch = true;
		if(Objects.nonNull(conditions.getId()))
			idMatch = role.getId().equals(conditions.getId());

		boolean nameMatch = true;
		if(Objects.nonNull(conditions.getName()))
			nameMatch = role.getName().equals(conditions.getName());

		boolean descMatch = true;
		if(Objects.nonNull(conditions.getDescription()))
			descMatch = role.getDescription().indexOf(conditions.getDescription())!= -1;

		boolean displayMatch = true;
		if(Objects.nonNull(conditions.getDisplayName()))
			displayMatch = getAttr("displayName", attr).indexOf(conditions.getDisplayName()) != -1;

		boolean indexMatch = true;
		if(Objects.nonNull(conditions.getIndex()))
			indexMatch = getAttr("index", attr).equals(conditions.getIndex());

		boolean iconMatch = true;
		if(Objects.nonNull(conditions.getIcon()))
			iconMatch = getAttr("icon", attr).equals(conditions.getIcon());

		boolean urlMatch = true;
		if(Objects.nonNull(conditions.getUrl()))
			urlMatch = getAttr("url", attr).equals(conditions.getUrl());

		boolean typeMatch = true;
		if(Objects.nonNull(conditions.getType()))
			typeMatch = getAttr("type", attr).equals(conditions.getType());

		boolean parentMatch = true;
		if(Objects.nonNull(conditions.getParent()))
			parentMatch = getAttr("parent", attr).equals(conditions.getParent());

		return idMatch&&nameMatch&&descMatch&&displayMatch&&indexMatch&&iconMatch&&urlMatch&&typeMatch&&parentMatch;
	}

	private String getAttr(String attrName, Map<String, List<String>> attributes){
		String attr = "";
		if(Objects.nonNull(attrName) && Objects.nonNull(attributes))
			attr = StringUtils.join(attributes.get(attrName),", ");

		return Optional.ofNullable(attr).orElse("");
	}

	private List<RoleRepresentation> getAllRoles(){
		RolesResource rolesResource = getKeycloakRoleResource();
		List<RoleRepresentation> roleRepresentations = rolesResource.list();
		return roleRepresentations.stream()
				.map(rr -> rolesResource.get(rr.getName()).toRepresentation())
				.collect(Collectors.toList());
	}

}

