package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 *
	 */
	private String id;
	/*
	 * 用户名
	 */
	private String username;
	/*
	 * 密码
	 */
	private String password;
	/*
	 * email
	 */
	private String email;
	/*
	 * 名
	 */
	private String firstName;
	/*
	 * 姓
	 */
	private String lastName;
	/*
	 * 性别
	 */
	private String gender;
	/*
	 * 状态
	 */
    private Boolean enabled;
    /*
	 * 岗位
	 */
	private String jobs;
	/*
	 * 联系方式
	 */
	private String mobile;
	/*
	 * 修改人
	 */
	private String updateBy;
	/*
	 * 修改时间
	 */
	private Date updateAt;

	private List<MappingDTO> role;

	public UserDTO() {
	}
}
