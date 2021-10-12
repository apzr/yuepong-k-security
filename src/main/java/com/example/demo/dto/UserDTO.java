package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String userName;

	private String emailAddress;

	private String firstName;

	private String lastName;

	private String password;

	private Map<String, List<String>> attrs;

	public UserDTO() {
	}
}
