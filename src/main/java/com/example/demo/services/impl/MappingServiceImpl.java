package com.example.demo.services.impl;

import com.example.demo.dto.MappingDTO;
import com.example.demo.dto.MappingsDTO;
import com.example.demo.services.MappingService;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * MappingServiceImpl
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 10:32:35
 **/
@Service
public class MappingServiceImpl implements MappingService {
	@Autowired
	RolesResource rolesResource;

	@Autowired
	UsersResource usersResource;

	@Override
	public int createMapping(MappingDTO mappingDTO) {
		int statusId = 0;

		try {
			List<RoleRepresentation> rolesToAdd = rolesResource
					.list().stream()
					.filter(role -> mappingDTO.getRoleId().equals(role.getId()))
					.collect(Collectors.toList());

			RoleMappingResource roleMappingResource = usersResource.get(mappingDTO.getUserId()).roles();
			roleMappingResource.realmLevel().add(rolesToAdd);
		} catch (Exception e) {
			statusId = -1;
			e.printStackTrace();
		}

		return statusId;
	}

	@Override
	public int createMapping(MappingsDTO mappingsDTO) {
		AtomicInteger count = new AtomicInteger();
		mappingsDTO.getRoleIds().stream().forEach(ms -> {
			MappingDTO m = new MappingDTO();
			m.setUserId(mappingsDTO.getUserId());
			m.setRoleId(ms);

			count.addAndGet(createMapping(m));
		});

		return count.get();
	}

	@Override
	public void deleteMapping(MappingDTO mappingDTO) {
		try {
			List<RoleRepresentation> rolesToRemove = rolesResource
					.list().stream()
					.filter(role -> mappingDTO.getRoleId().equals(role.getId()))
					.collect(Collectors.toList());

			RoleMappingResource roleMappingResource = usersResource.get(mappingDTO.getUserId()).roles();
			roleMappingResource.realmLevel().remove(rolesToRemove);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<MappingDTO> getMappingsByUser(String uid) {
		RoleMappingResource roleMappingResource = usersResource.get(uid).roles();
		List<RoleRepresentation> rm = roleMappingResource.realmLevel().listAll();

		return rm.stream().map(roleRepresentation -> toMappingDto(roleRepresentation, uid)).collect(Collectors.toList());
	}

	@Override
	public List<MappingDTO> listMappings() {
		List<MappingDTO> allMappings = new ArrayList();
		usersResource.list().stream().forEach(userResource -> {
			RoleMappingResource roleMappingResource = usersResource.get(userResource.getId()).roles();
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


}
