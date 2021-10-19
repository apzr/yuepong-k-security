package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	/**
	 * 角色代码, 理解为Code
	 */
	private String name;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 显示名称
	 */
	private String displayName;

	/**
	 * 序号
	 */
	private String index;

	/**
	 * 图标
	 */
	private String icon;

	/**
	 * 指向链接
	 */
	private String url;

	/**
	 * 类型
	 */
	private String type;

	/**
	 * 父节点
	 */
	private String parent;

	/**
	 * 客户端角色还是域角色
	 */
	private Boolean clientRole;

	public RoleDTO() {
	}
}
