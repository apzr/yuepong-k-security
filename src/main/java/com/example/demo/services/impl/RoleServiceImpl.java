package com.example.demo.services.impl;

import com.example.demo.dto.RoleDTO;
import com.example.demo.services.RoleService;
import org.apache.commons.lang.StringUtils;
import org.keycloak.admin.client.resource.RoleByIdResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * RoleServiceImpl
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 10:31:21
 **/
@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	RolesResource rolesResource;

	@Autowired
	RoleByIdResource rolesByIdResource;

	@Override
	public String createRole(RoleDTO roleDTO) {
		// Create role
		RoleRepresentation role = copyProperty(roleDTO, new RoleRepresentation());
		rolesResource.create(role);

		RoleResource roleResource = rolesResource.get(roleDTO.getName());
		return roleResource.toRepresentation().getId();
	}

	@Override
	public void updateRole(RoleDTO roleDTO) {
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
		rolesResource.deleteRole(name);
	}

	@Override
	public RoleDTO getRole(String id) {
		RoleRepresentation roleRepresentation = getRoleById(id);
		return convertRole(roleRepresentation);
	}

	@Override
	public List<RoleDTO> getRole(RoleDTO conditions) {
		List<RoleRepresentation> roles = getAllRoles();
		List<RoleRepresentation> filterRoles = roles.stream()
				.filter(role -> matchRole(role, conditions))
				.collect(Collectors.toList());

		return filterRoles.stream().map(role -> convertRole(role)).collect(Collectors.toList());
	}

	@Override
	public List<RoleDTO> listRoles() {
		List<RoleRepresentation> roleRepresentations = getAllRoles();
		return roleRepresentations.stream().map(roleRepresentation -> convertRole(roleRepresentation)).collect(Collectors.toList());
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

	private List<RoleRepresentation> getAllRoles(){
		List<RoleRepresentation> roleRepresentations = rolesResource.list();
		return roleRepresentations.stream()
				.map(rr -> rolesResource.get(rr.getName()).toRepresentation())
				.collect(Collectors.toList());
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
				r.setParent( getAttr("parent", attr) );
		}
		return r;
	}

	private String getAttr(String attrName, Map<String, List<String>> attributes){
		String attr = "";
		if(Objects.nonNull(attrName) && Objects.nonNull(attributes))
			attr = StringUtils.join(attributes.get(attrName),", ");

		return Optional.ofNullable(attr).orElse("");
	}
}
