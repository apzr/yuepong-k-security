package com.example.demo;

import com.example.demo.dto.MappingDTO;
import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.services.KeyCloakAdminService_V0;
import com.example.demo.services.KeyCloakAdminService_V1;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
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
    KeyCloakAdminService_V0 keyCloakAdminService_V0;

    @Autowired
    KeyCloakAdminService_V1 keyCloakAdminService_V1;

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
	public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
		try {
			return ResponseEntity.ok( keyCloakAdminService_V1.createUserInKeyCloak(userDTO) );
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/*
	 * 创建用户
	 *
	 * @param userDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:42
	 */
	@PostMapping(value = "/api/user/edit")
	public ResponseEntity<?> editUser(@RequestBody UserDTO userDTO) {
		try {
			keyCloakAdminService_V1.updateUserInKeyCloak(userDTO);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 查询用户详情
	 *
	 * @param id
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
	@GetMapping(value = "/api/user/{id}")
	public ResponseEntity<?> getUserInKeyCloak(@PathVariable String id) {
		try {
			UserDTO user = keyCloakAdminService_V1.getUserInKeyCloak(id);
			return  ResponseEntity.ok(user);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

		/**
	 * 查询用户详情
	 *
	 * @param id
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
	@DeleteMapping(value = "/api/user/{id}")
	public ResponseEntity<?> deleteUserInKeyCloak(@PathVariable String id) {
		try {
			return ResponseEntity.ok( keyCloakAdminService_V1.deleteUserInKeyCloak(id) );
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 查询所有用户
	 *
	 * @param
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
	@GetMapping(value = "/api/user/list")
	public ResponseEntity<?> getUsersInKeyCloak() {
		try {
			List<UserDTO> users = keyCloakAdminService_V1.getUsersInKeyCloak();
			return  ResponseEntity.ok(users);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
	public ResponseEntity<?> createUser(@RequestBody RoleDTO roleDTO) {
		try {
			keyCloakAdminService_V1.createRoleInKeyCloak(roleDTO);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 查询所有角色
	 *
	 * @param  
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:44
	 */
	@GetMapping(value = "/api/role/list")
	public ResponseEntity<?> getRolesInKeyCloak() {
		try {
			List<RoleDTO>  roles = keyCloakAdminService_V1.getRolesInKeyCloak();
			return ResponseEntity.ok(roles);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
	public ResponseEntity<?> createMappingInKeyCloak(@RequestBody MappingDTO mappingDTO) {
		try {
			keyCloakAdminService_V1.createMappingInKeyCloak(mappingDTO);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * 查询所有关联
	 *
	 * @param userDTO 
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:45
	 */
	@GetMapping(value = "/api/mapping/list")
	public ResponseEntity<?> getMappingsInKeyCloak(@RequestBody UserDTO userDTO) {
		try {
			List<MappingDTO> mappings = keyCloakAdminService_V1.getMappingsByUser(userDTO.getId());
			return ResponseEntity.ok(mappings);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

    /**********************************/
    /***************MENU***************/
    /**********************************/

}
