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
 * User-role
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/21 09:30:18
 **/
@CrossOrigin
@Controller
@RequestMapping("/mapping")
public class MappingController extends MainController {

	/**
	 * 关联用户角色关系
	 *
	 * @param mappingDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/12 9:43
	 */
    @PostMapping(value = "/create")
	public ResponseEntity<?> createMapping(@RequestBody MappingDTO mappingDTO) {
		try {
			mappingService.createMapping(mappingDTO);
			return ResponseResult.success().response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),mappingDTO).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@PostMapping(value = "/createMany")
	public ResponseEntity<?> createMapping(@RequestBody MappingsDTO mappingsDTO) {
		try {
			mappingService.createMapping(mappingsDTO);
			return ResponseResult.success().response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),mappingsDTO).response();
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
    @DeleteMapping(value = "/delete")
	public ResponseEntity<?> deleteMapping(@RequestBody MappingDTO mappingDTO) {
		try {
			mappingService.deleteMapping(mappingDTO);
			return ResponseResult.success().response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),mappingDTO).response();
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
	@GetMapping(value = "/list")
	public ResponseEntity<?> listMappings() {
		try {
			List<MappingDTO> result = mappingService.listMappings();
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage()).response();
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
	@GetMapping(value = "/{uid}")
	public ResponseEntity<?> getRolesByUser(@PathVariable String uid) {
		try {
			List<MappingDTO> result = mappingService.getMappingsByUser(uid);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),uid).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

}
