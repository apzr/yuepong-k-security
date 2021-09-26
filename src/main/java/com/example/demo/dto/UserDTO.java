package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String userName;

	private String emailAddress;

	private String firstName;

	private String lastName;

	private String password;

	public UserDTO() {
	}
}
