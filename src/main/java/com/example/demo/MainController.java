package com.example.demo;

import com.example.demo.dto.*;
import com.example.demo.services.AdminService;
import com.yuepong.jdev.api.bean.ResponseResult;
import com.yuepong.jdev.code.CodeMsgs;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@CrossOrigin
@Controller
public class MainController {

    @Autowired
	AdminService adminService;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    /*
	 * 测试
	 *
	 * @param model 
     * @param authentication
	 * @return java.lang.String
	 * @author apr
	 * @date 2021/10/12 9:42
	 */
    @GetMapping("/html/user_info")
    public String user_info(Model model, KeycloakAuthenticationToken authentication) {//KeycloakAuthenticationToken
        if(Objects.nonNull(authentication)){
            model.addAttribute("userName", authentication.getName());
            model.addAttribute("credentials", authentication.getAccount().getKeycloakSecurityContext().getTokenString());
            model.addAttribute("","");
        }

        return "userinfo";
    }

    /**
	 * 解析token
	 *
	 * @param authentication 
	 * @return org.springframework.http.ResponseEntity<org.keycloak.representations.AccessToken>
	 * @author apr
	 * @date 2021/10/12 9:41
	 */
    @GetMapping("/user_info")
    public ResponseEntity<?> apiUserinfo(KeycloakAuthenticationToken authentication) {
		AccessToken info = authentication.getAccount().getKeycloakSecurityContext().getToken();
		return ResponseResult.success("请求成功", info).response();
    }
    
    /**********************************/
    /***************USER***************/
    /**********************************/

    /*
	 * 创建用户
	 *
	 * @param userDTO 
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:42
	 */
	@PostMapping(value = "/user/create")
	public ResponseEntity<?> create(@RequestBody UserDTO userDTO, KeycloakAuthenticationToken authentication) {
		String id;
		try {
			userDTO.setUpdateBy(authentication.getName());
			id = adminService.createUser(userDTO);
		} catch (Exception ex) {
			id = "-1";
		}

		return "-1".equals(id)
				? ResponseResult.error("请求失败", id).response()
				: ResponseResult.success("请求成功", id).response();
	}

	/*
	 * 修改用户
	 *
	 * @param userDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:42
	 */
	@PostMapping(value = "/user/edit")
	public ResponseEntity<?> edit(@RequestBody UserDTO userDTO, KeycloakAuthenticationToken authentication) {
		try {
			userDTO.setUpdateBy(authentication.getName());
			adminService.updateUser(userDTO);
			return ResponseResult.success().response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 删除用户
	 *
	 * @param uid
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
	@DeleteMapping(value = "/user/{uid}")
	public ResponseEntity<?> deleteUser(@PathVariable String uid) {
		try {
			String result = adminService.deleteUser(uid);
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 重置密码
	 *
	 * @param userDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
	@PostMapping(value = "/user/reset")
	public ResponseEntity<?> resetUser(@RequestBody UserDTO userDTO) {
		try {
			adminService.resetUser(userDTO.getId());
			return ResponseResult.success().response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 查询用户
	 *
	 * @param uid
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
	@GetMapping(value = "/user/{uid}")
	public ResponseEntity<?> getUser(@PathVariable String uid) {
		try {
			UserDTO user = adminService.getUser(uid);
			return ResponseResult.success("请求成功", user).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 用户列表
	 *
	 * @param
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
	@GetMapping(value = "/user/list")
	public ResponseEntity<?> listUsers() {
		try {
			List<UserDTO> result = adminService.listUsers();
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

    /**********************************/
    /***************ROLE***************/
    /**********************************/

    /**
	 * 创建角色
	 *
	 * @param roleDTO 
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:42
	 */
	@PostMapping(value = "/role/create")
	public ResponseEntity<?> create(@RequestBody RoleDTO roleDTO) {
		try {
			String role_id = adminService.createRole(roleDTO);
			return ResponseResult.success("请求成功", role_id).response();
		} catch (Exception ex) {
			ResponseResult<String> err = ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR, ex.getMessage(), roleDTO.getName());
			return ResponseResult.response(err);
		}
	}

	/**
	 * 编辑角色
	 *
	 * @param roleDTO 
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 11:43
	 */
	@PostMapping(value = "/role/edit")
	public ResponseEntity<?> edit(@RequestBody RoleDTO roleDTO) {
		try {
			adminService.updateRole(roleDTO);
			return ResponseResult.success().response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 删除角色
	 *
	 * @param name
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
	@DeleteMapping(value = "/role/{name}")
	public ResponseEntity<?> deleteRole(@PathVariable String name){
		try {
			adminService.deleteRole(name);
			return ResponseResult.success().response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 查询角色
	 *
	 * @param role_id
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
	@GetMapping(value = "/role/{role_id}")
	public ResponseEntity<?> getRole(@PathVariable String role_id) {
		try {
			RoleDTO result = adminService.getRole(role_id);
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@PostMapping(value = "/role")
	public ResponseEntity<?> getRole(@RequestBody RoleDTO roleDTO) {
		try {
			List<RoleDTO> result = adminService.getRole(roleDTO);
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 角色列表
	 *
	 * @param  
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:44
	 */
	@GetMapping(value = "/role/list")
	public ResponseEntity<?> listRoles() {
		try {
			List<RoleDTO>  result = adminService.listRoles();
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

    /**********************************/
    /*************USERROLE*************/
    /**********************************/

	/**
	 * 关联用户角色关系
	 *
	 * @param mappingDTO 
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
    @PostMapping(value = "/mapping/create")
	public ResponseEntity<?> createMapping(@RequestBody MappingDTO mappingDTO) {
		try {
			adminService.createMapping(mappingDTO);
			return ResponseResult.success().response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@PostMapping(value = "/mapping/createMany")
	public ResponseEntity<?> createMapping(@RequestBody MappingsDTO mappingsDTO) {
		try {
			adminService.createMapping(mappingsDTO);
			return ResponseResult.success().response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}


	/**
	 * deleteMapping
	 *
	 * @param mappingDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 14:22
	 */
    @DeleteMapping(value = "/mapping/delete")
	public ResponseEntity<?> deleteMapping(@RequestBody MappingDTO mappingDTO) {
		try {
			adminService.deleteMapping(mappingDTO);
			return ResponseResult.success().response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 查询单用户所有关联
	 *
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:45
	 */
	@GetMapping(value = "/mapping/list")
	public ResponseEntity<?> listMappings() {
		try {
			List<MappingDTO> result = adminService.listMappings();
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 列出所有用户的所有权限
	 *
	 * @param uid
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:45
	 */
	@GetMapping(value = "/mapping/{uid}")
	public ResponseEntity<?> getRolesByUser(@PathVariable String uid) {
		try {
			List<MappingDTO> result = adminService.getMappingsByUser(uid);
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

    /**********************************/
    /***************MENU***************/
    /**********************************/
    @PostMapping(value = "/group/create")
	public ResponseEntity<?> create(@RequestBody GroupDTO groupDTO) {
		try {
			String groupId = adminService.create(groupDTO);
			return ResponseResult.success("请求成功", groupId).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}


	@GetMapping(value = "/group/uid/{uid}")
	public ResponseEntity<?> getGroupByUser(@PathVariable String uid) {
		try {
			List<GroupRepresentation> result = adminService.getGroupsByUser(uid);
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@GetMapping(value = "/group/id/{gid}")
	public ResponseEntity<?> getGroupById(@PathVariable String gid) {
		try {
			GroupRepresentation result = adminService.getGroupById(gid);
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}


	@GetMapping(value = "/group/list")
	public ResponseEntity<?> listGroups() {
		try {
			List<GroupRepresentation> result = adminService.listGroups();
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@GetMapping(value = "/group/roles/{gid}")
	public ResponseEntity<?> getRolesByGroupId(@PathVariable String gid) {
		try {
			List<RoleRepresentation> result = adminService.getGroupRoles(gid);
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@GetMapping(value = "/group/members/{gid}")
	public ResponseEntity<?> getMembersByGroupId(@PathVariable String gid) {
		try {
			List<UserRepresentation> result = adminService.getGroupMembers(gid);
			return ResponseResult.success("请求成功", result).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 向组中添加用户
	 *
	 * @param groupMappingDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/13 10:59
	 */
    @PostMapping(value = "/group/user/join")
	public ResponseEntity<?> joinGroup(@RequestBody GroupMappingDTO groupMappingDTO) {
		try {
			adminService.joinGroup(groupMappingDTO);
			return ResponseResult.success().response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 从组中移除用户
	 *
	 * @param groupMappingDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/13 10:59
	 */
    @PostMapping(value = "/group/user/leave")
	public ResponseEntity<?> leaveGroup(@RequestBody GroupMappingDTO groupMappingDTO) {
		try {
			adminService.leaveGroup(groupMappingDTO);
			return ResponseResult.success().response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 向组中添加角色
	 *
	 * @param groupDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/13 10:59
	 */
    @PostMapping(value = "/group/role/join")
	public ResponseEntity<?> joinGroup(@RequestBody GroupDTO groupDTO) {
		try {
			adminService.joinGroup(groupDTO);
			return ResponseResult.success().response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 从组中移除用角色
	 *
	 * @param groupDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/13 10:59
	 */
    @PostMapping(value = "/group/role/leave")
	public ResponseEntity<?> leaveGroup(@RequestBody GroupDTO groupDTO) {
		try {
			adminService.leaveGroup(groupDTO);
			return ResponseResult.success().response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}
}
