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
import java.util.Map;
import java.util.Objects;

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

    @GetMapping("/html/user_info")
    public String user_info(Model model, KeycloakAuthenticationToken authentication) {//KeycloakAuthenticationToken
        if(Objects.nonNull(authentication)){
            model.addAttribute("userName", authentication.getName());
            model.addAttribute("credentials", authentication.getAccount().getKeycloakSecurityContext().getTokenString());
            model.addAttribute("","");
        }

        return "userinfo";
    }

    @GetMapping("/api/user_info")
    public ResponseEntity<AccessToken> apiUserinfo(KeycloakAuthenticationToken authentication) {
        return ResponseEntity.ok( authentication.getAccount().getKeycloakSecurityContext().getToken() );
    }

	/*
	 * Creating user in keycloak passing UserDTO contains username, emailid,
	 * password, firtname, lastname
	 */
	@PostMapping(value = "/api/user/create")
	public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
		try {
			keyCloakAdminService_V1.createUserInKeyCloak(userDTO);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

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

	@GetMapping(value = "/api/user/list")
	public ResponseEntity<?> getUsersInKeyCloak() {
		try {
			List<UserDTO> users = keyCloakAdminService_V1.getUsersInKeyCloak();
			return ResponseEntity.ok(users);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

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
}
