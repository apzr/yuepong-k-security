package com.example.demo;

import com.example.demo.dto.GroupMappingDTO;
import com.example.demo.dto.MappingDTO;
import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.services.AdminService;
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
    @GetMapping("/api/user_info")
    public ResponseEntity<AccessToken> apiUserinfo(KeycloakAuthenticationToken authentication) {
        return ResponseEntity.ok( authentication.getAccount().getKeycloakSecurityContext().getToken() );
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
	@PostMapping(value = "/api/user/create")
	public ResponseEntity<?> create(@RequestBody UserDTO userDTO) {
		try {
			return ResponseEntity.ok( adminService.createUser(userDTO) );
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
		}
	}

	/*
	 * 修改用户
	 *
	 * @param userDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:42
	 */
	@PostMapping(value = "/api/user/edit")
	public ResponseEntity<?> edit(@RequestBody UserDTO userDTO) {
		try {
			adminService.updateUser(userDTO);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
	@DeleteMapping(value = "/api/user/{uid}")
	public ResponseEntity<?> deleteUser(@PathVariable String uid) {
		try {
			return ResponseEntity.ok( adminService.deleteUser(uid) );
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
	@GetMapping(value = "/api/user/{uid}")
	public ResponseEntity<?> getUser(@PathVariable String uid) {
		try {
			UserDTO user = adminService.getUser(uid);
			return ResponseEntity.ok(user);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
	@GetMapping(value = "/api/user/list")
	public ResponseEntity<?> listUsers() {
		try {
			List<UserDTO> users = adminService.listUsers();
			return ResponseEntity.ok(users);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
	@PostMapping(value = "/api/role/create")
	public ResponseEntity<?> create(@RequestBody RoleDTO roleDTO) {
		try {
			adminService.createRole(roleDTO);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
	@PostMapping(value = "/api/role/edit")
	public ResponseEntity<?> edit(@RequestBody RoleDTO roleDTO) {
		try {
			adminService.updateRole(roleDTO);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
	@DeleteMapping(value = "/api/role/{name}")
	public ResponseEntity<?> deleteRole(@PathVariable String name){
		try {
			adminService.deleteRole(name);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
		}
	}

	/**
	 * 查询角色
	 *
	 * @param rid
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
	@GetMapping(value = "/api/role/{rid}")
	public ResponseEntity<?> getRole(@PathVariable String rid) {
		try {
			RoleDTO role = adminService.getRole(rid);
			return ResponseEntity.ok(role);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
	@GetMapping(value = "/api/role/list")
	public ResponseEntity<?> listRoles() {
		try {
			List<RoleDTO>  roles = adminService.listRoles();
			return ResponseEntity.ok(roles);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
    @PostMapping(value = "/api/mapping/create")
	public ResponseEntity<?> createMapping(@RequestBody MappingDTO mappingDTO) {
		try {
			adminService.createMapping(mappingDTO);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
    @DeleteMapping(value = "/api/mapping/delete")
	public ResponseEntity<?> deleteMapping(@RequestBody MappingDTO mappingDTO) {
		try {
			adminService.deleteMapping(mappingDTO);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
		}
	}

	/**
	 * 查询单用户所有关联
	 *
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:45
	 */
	@GetMapping(value = "/api/mapping/list")
	public ResponseEntity<?> listMappings() {
		try {
			List<MappingDTO> mappings = adminService.listMappings();
			return ResponseEntity.ok(mappings);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
	@GetMapping(value = "/api/mapping/{uid}")
	public ResponseEntity<?> getRolesByUser(@PathVariable String uid) {
		try {
			List<MappingDTO> mappings = adminService.getMappingsByUser(uid);
			return ResponseEntity.ok(mappings);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
		}
	}

    /**********************************/
    /***************MENU***************/
    /**********************************/
	@GetMapping(value = "/api/group/uid/{uid}")
	public ResponseEntity<?> getGroupByUser(@PathVariable String uid) {
		try {
			List<GroupRepresentation> groups = adminService.getGroupsByUser(uid);
			return ResponseEntity.ok(groups);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
		}
	}

	@GetMapping(value = "/api/group/id/{gid}")
	public ResponseEntity<?> getGroupById(@PathVariable String gid) {
		try {
			GroupRepresentation group = adminService.getGroupById(gid);
			return ResponseEntity.ok(group);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
		}
	}


	@GetMapping(value = "/api/group/list")
	public ResponseEntity<?> listGroups() {
		try {
			List<GroupRepresentation> groups = adminService.listGroups();
			return ResponseEntity.ok(groups);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
		}
	}

	@GetMapping(value = "/api/group/roles/{gid}")
	public ResponseEntity<?> getRolesByGroupId(@PathVariable String gid) {
		try {
			List<RoleRepresentation> roles = adminService.getGroupRoles(gid);
			return ResponseEntity.ok(roles);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
		}
	}

	@GetMapping(value = "/api/group/members/{gid}")
	public ResponseEntity<?> getMembersByGroupId(@PathVariable String gid) {
		try {
			List<UserRepresentation> users = adminService.getGroupMembers(gid);
			return ResponseEntity.ok(users);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
    @PostMapping(value = "/api/group/join")
	public ResponseEntity<?> joinGroup(@RequestBody GroupMappingDTO groupMappingDTO) {
		try {
			adminService.joinGroup(groupMappingDTO);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
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
    @PostMapping(value = "/api/group/leave")
	public ResponseEntity<?> leaveGroup(@RequestBody GroupMappingDTO groupMappingDTO) {
		try {
			adminService.leaveGroup(groupMappingDTO);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.ok(ex.getMessage());
		}
	}
}
