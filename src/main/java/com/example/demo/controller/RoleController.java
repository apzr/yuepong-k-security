package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.services.GroupService;
import com.example.demo.services.MappingService;
import com.example.demo.services.RoleService;
import com.example.demo.services.UserService;
import com.yuepong.jdev.api.bean.ResponseResult;
import com.yuepong.jdev.code.CodeMsgs;
import com.yuepong.jdev.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RoleController
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/21 09:30:09
 **/
@CrossOrigin
@Controller
@RequestMapping("/role")
public class RoleController extends MainController {

    /**
	 * 创建角色
	 *
	 * @param roleDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:42
	 */
	@PostMapping(value = "/create")
	public ResponseEntity<?> create(@RequestBody RoleDTO roleDTO) {
		try {
			String role_id = roleService.createRole(roleDTO);
			return ResponseResult.success("请求成功", role_id).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),roleDTO.getName()).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
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
	@PostMapping(value = "/edit")
	public ResponseEntity<?> edit(@RequestBody RoleDTO roleDTO) {
		try {
			roleService.updateRole(roleDTO);
			return ResponseResult.success().response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),roleDTO).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 删除角色
	 *
	 * @param role_id
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
	@DeleteMapping(value = "/{role_id}")
	public ResponseEntity<?> deleteRole(@PathVariable String role_id){
		try {
			roleService.deleteRole(role_id);
			return ResponseResult.success().response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),role_id).response();
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
	@GetMapping(value = "/{role_id}")
	public ResponseEntity<?> getRole(@PathVariable String role_id) {
		try {
			RoleDTO result = roleService.getRole(role_id);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),role_id).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@PostMapping(value = "")
	public ResponseEntity<?> getRole(@RequestBody RoleDTO roleDTO) {
		try {
			List<RoleDTO> result = roleService.getRole(roleDTO);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR, be.getMessage(),roleDTO).response();
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
	@GetMapping(value = "/list")
	public ResponseEntity<?> listRoles() {
		try {
			List<RoleDTO>  result = roleService.listRoles();
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage()).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

}
