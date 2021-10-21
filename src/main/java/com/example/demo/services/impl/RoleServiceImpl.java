package com.example.demo.services.impl;

import com.example.demo.dto.RoleDTO;
import com.example.demo.services.RoleService;
import com.example.demo.util.Utils;
import org.keycloak.admin.client.resource.RoleByIdResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RoleServiceImpl
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 10:31:21
 **/
@Service
public class RoleServiceImpl extends AdminServiceImpl implements RoleService {

	@Override
	public String createRole(RoleDTO roleDTO) {
		// Create role
		RoleRepresentation role = Utils.copyProperty(roleDTO, new RoleRepresentation());
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
			roleResource.update( Utils.copyProperty(roleDTO, roleToEdit) );
		}
	}

	@Override
	public void deleteRole(String id) {
		rolesByIdResource.deleteRole(id);
	}

	@Override
	public RoleDTO getRole(String id) {
		RoleRepresentation roleRepresentation = getRoleById(id);
		return Utils.toRole(roleRepresentation);
	}

	@Override
	public List<RoleDTO> getRole(RoleDTO conditions) {
		List<RoleRepresentation> roles = getAllRoles();
		List<RoleRepresentation> filterRoles = roles.stream()
				.filter(role -> Utils.matchRole(role, conditions))
				.collect(Collectors.toList());

		return filterRoles.stream().map(Utils::toRole).collect(Collectors.toList());
	}

	@Override
	public List<RoleDTO> listRoles() {
		List<RoleRepresentation> roleRepresentations = getAllRoles();
		return roleRepresentations.stream().map(Utils::toRole).collect(Collectors.toList());
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


	private List<RoleRepresentation> getAllRoles(){
		List<RoleRepresentation> roleRepresentations = rolesResource.list();
		return roleRepresentations.stream()
				.map(rr -> rolesResource.get(rr.getName()).toRepresentation())
				.collect(Collectors.toList());
	}


}
