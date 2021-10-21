package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.services.GroupService;
import com.example.demo.services.MappingService;
import com.example.demo.services.RoleService;
import com.example.demo.services.UserService;
import com.yuepong.jdev.api.bean.ResponseResult;
import com.yuepong.jdev.code.CodeMsgs;
import com.yuepong.jdev.exception.BizException;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GroupController
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/21 09:29:42
 **/
@CrossOrigin
@Controller
@RequestMapping("/group")
public class GroupController extends MainController {

    @PostMapping(value = "/create")
	public ResponseEntity<?> create(@RequestBody GroupRepresentation group) {
		try {
			String groupId = groupService.create(group);
			return ResponseResult.success("请求成功", groupId).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),group.getName()).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}


	@GetMapping(value = "/uid/{uid}")
	public ResponseEntity<?> getGroupByUser(@PathVariable String uid) {
		try {
			List<GroupRepresentation> result = groupService.getGroupsByUser(uid);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),uid).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@PostMapping(value = "/name")
	public ResponseEntity<?> searchGroup(@RequestBody GroupRepresentation group) {
		try {
			GroupRepresentation result = groupService.search(group);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(), group).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}


	@GetMapping(value = "/id/{gid}")
	public ResponseEntity<?> getGroupById(@PathVariable String gid) {
		try {
			GroupRepresentation result = groupService.getGroupById(gid);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),gid).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}


	@GetMapping(value = "/list")
	public ResponseEntity<?> listGroups() {
		try {
			List<GroupRepresentation> result = groupService.listGroups();
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage()).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@GetMapping(value = "/roles/{gid}")
	public ResponseEntity<?> getRolesByGroupId(@PathVariable String gid) {
		try {
			List<RoleRepresentation> result = groupService.getGroupRoles(gid);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),gid).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	@GetMapping(value = "/members/{gid}")
	public ResponseEntity<?> getMembersByGroupId(@PathVariable String gid) {
		try {
			List<UserRepresentation> result = groupService.getGroupMembers(gid);
			return ResponseResult.success("请求成功", result).response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),gid).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}

	/**
	 * 向组中添加用户
	 *
	 * @param groupMappingsDTO
	 * @return org.springframework.http.ResponseEntity<?>
	 * @author apr
	 * @date 2021/10/13 10:59
	 */
    @PostMapping(value = "/user/join")
	public ResponseEntity<?> joinGroup(@RequestBody GroupMappingsDTO groupMappingsDTO) {
		try {
			groupService.joinGroup(groupMappingsDTO);
			return ResponseResult.success().response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),groupMappingsDTO).response();
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
    @PostMapping(value = "/user/leave")
	public ResponseEntity<?> leaveGroup(@RequestBody GroupMappingDTO groupMappingDTO) {
		try {
			groupService.leaveGroup(groupMappingDTO);
			return ResponseResult.success().response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),groupMappingDTO).response();
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
    @PostMapping(value = "/role/join")
	public ResponseEntity<?> joinGroup(@RequestBody GroupDTO groupDTO) {
		try {
			groupService.joinGroup(groupDTO);
			return ResponseResult.success().response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),groupDTO).response();
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
    @PostMapping(value = "/role/leave")
	public ResponseEntity<?> leaveGroup(@RequestBody GroupDTO groupDTO) {
		try {
			groupService.leaveGroup(groupDTO);
			return ResponseResult.success().response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage(),groupDTO).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
	}
}
