package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.services.GroupService;
import com.example.demo.services.MappingService;
import com.example.demo.services.RoleService;
import com.example.demo.services.UserService;
import com.yuepong.jdev.api.bean.ResponseResult;
import com.yuepong.jdev.code.CodeMsgs;
import com.yuepong.jdev.exception.BizException;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * UserController
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/21 09:29:53
 **/
@CrossOrigin
@Controller
@RequestMapping("/user")
public class UserController extends MainController {

    /*
	 * 创建用户
	 *
	 * @param userDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:42
	 */
	@PostMapping(value = "/create")
	public ResponseEntity<?> create(@RequestBody UserDTO userDTO, KeycloakAuthenticationToken authentication) {
		//TODO:error handle
		String id;
		try {
			userDTO.setUpdateBy(authentication.getName());
			id = userService.createUser(userDTO);
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
	@PostMapping(value = "/edit")
	public ResponseEntity<?> edit(@RequestBody UserDTO userDTO, KeycloakAuthenticationToken authentication) {
		try {
			userDTO.setUpdateBy(authentication.getName());
			userService.updateUser(userDTO);
			return ResponseResult.success().response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(), userDTO).response();
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
	@DeleteMapping(value = "/{uid}")
	public ResponseEntity<?> deleteUser(@PathVariable String uid) {
		try {
			String result = userService.deleteUser(uid);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(), uid).response();
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
	@PostMapping(value = "/reset")
	public ResponseEntity<?> resetUser(@RequestBody UserDTO userDTO) {
		try {
			userService.resetUser(userDTO.getId());
			return ResponseResult.success().response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(), userDTO).response();
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
	@GetMapping(value = "/{uid}")
	public ResponseEntity<?> getUser(@PathVariable String uid) {
		try {
			UserDTO user = userService.getUser(uid);
			return ResponseResult.success("请求成功", user).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(), uid).response();
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
	@GetMapping(value = "/list")
	public ResponseEntity<?> listUsers() {
		try {
			List<UserDTO> result = userService.listUsers();
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage()).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@GetMapping(value = "/list/{start}/{size}")
	public ResponseEntity<?> pageUsers(@PathVariable String start, @PathVariable String size) {
		try {
			List<UserDTO> result = userService.pageUsers(start, size);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage()).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@GetMapping(value = "/search")
	public ResponseEntity<?> searchUsers(@QueryParam("id")String id, @QueryParam("name")String name) {
		try {
			UserDTO query = new UserDTO();
			query.setId(id);
			List<UserDTO> result = userService.search(query);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(), id).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@GetMapping(value = "/listRoles/{uid}")
	public ResponseEntity<?> listRoles(@PathVariable String uid) {
		try {
			List<UserDTO> result = userService.listRoles(uid);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(), uid).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}
}
