package com.example.demo.controller;

import com.example.demo.dto.MappingDTO;
import com.example.demo.services.*;
import com.yuepong.jdev.api.bean.ResponseResult;
import com.yuepong.jdev.code.CodeMsgs;
import com.yuepong.jdev.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@CrossOrigin
@Controller
public class MainController {
    @Autowired
	UserService userService;
    @Autowired
	RoleService roleService;
    @Autowired
	GroupService groupService;
    @Autowired
	MappingService mappingService;

    protected ResponseEntity<?> response(Object serviceData){
        try {
			return ResponseResult.success("请求成功").response();
		} catch (BizException be) {
			return ResponseResult.obtain(CodeMsgs.SERVICE_BASE_ERROR,be.getMessage()).response();
		} catch (Exception ex) {
			return ResponseResult.error(ex.getMessage()).response();
		}
    }
}
