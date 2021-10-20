package com.example.demo.util;

import com.example.demo.dto.MappingDTO;
import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.UserDTO;
import org.apache.commons.lang.StringUtils;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 工具类
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 13:58:45
 **/
public class Utils {

    public static MappingDTO toMapping(RoleRepresentation roleRepresentation, UserRepresentation user) {
		MappingDTO m = toMapping(roleRepresentation, user.getId());
		m.setUserName(user.getUsername());
		return m;
	}

	public static MappingDTO toMapping(RoleRepresentation roleRepresentation, String uid) {
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
	public static boolean matchRole(RoleRepresentation role, RoleDTO conditions) {
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

	public static RoleRepresentation copyProperty(RoleDTO roleDTO, RoleRepresentation roleToEdit) {
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

	public static RoleDTO toRole(RoleRepresentation roleRepresentation) {
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
				r.setParent( getAttr("parent", attr) );
		}
		return r;
	}

	public static UserRepresentation copyProperty(UserDTO userDTO, UserRepresentation userToEdit) {
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

	public static  UserDTO toUser(UserRepresentation userRepresentation) {
		UserDTO u = new UserDTO();
		u.setId(userRepresentation.getId());
		u.setUsername( userRepresentation.getUsername() );
		u.setEmail(userRepresentation.getEmail() );
		u.setFirstName(userRepresentation.getFirstName());
		u.setLastName(userRepresentation.getLastName());
		u.setEnabled(userRepresentation.isEnabled());
		//u.setRole( mappingService.getMappingsByUser(userRepresentation.getId()) );

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

	public static String getAttr(String attrName, Map<String, List<String>> attributes){
		String attr = "";
		if(Objects.nonNull(attrName) && Objects.nonNull(attributes))
			attr = StringUtils.join(attributes.get(attrName),", ");

		return Optional.ofNullable(attr).orElse("");
	}

	public static String getMessage(int httpStatusCode) {
		return null;
	}
}
