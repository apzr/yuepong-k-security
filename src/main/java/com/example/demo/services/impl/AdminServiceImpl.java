package com.example.demo.services.impl;

import com.example.demo.dto.*;
import com.example.demo.services.AdminService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * ImplBak
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/26 11:12:45
 **/
@Service
public class AdminServiceImpl implements AdminService {

	private String SECRET_KEY = "keycloak";

	@Value("${keycloak.resource}")
	private String CLIENT_ID;

	@Value("${keycloak.auth-server-url}")
	private String AUTH_URL;

	@Value("${keycloak.realm}")
	private String REALM;

	@Autowired
	UsersResource userResource;

	public String getToken(UserCredentials userCredentials) {
		String responseToken = null;
		try {
			String username = userCredentials.getUsername();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("grant_type", "password"));
			urlParameters.add(new BasicNameValuePair("client_id", CLIENT_ID));
			urlParameters.add(new BasicNameValuePair("username", username));
			urlParameters.add(new BasicNameValuePair("password", userCredentials.getPassword()));
			urlParameters.add(new BasicNameValuePair("client_secret", SECRET_KEY));
			responseToken = sendPost(urlParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseToken;
	}

	public String getByRefreshToken(String refreshToken) {
		String responseToken = null;
		try {
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
			urlParameters.add(new BasicNameValuePair("client_id", CLIENT_ID));
			urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
			urlParameters.add(new BasicNameValuePair("client_secret", SECRET_KEY));
			responseToken = sendPost(urlParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseToken;
	}

	// after logout user from the keycloak system. No new access token will be issued.
	public void logoutUser(String userId) {
		userResource.get(userId).logout();
	}

	// Reset passowrd
	public void resetPassword(String newPassword, String userId) {
		// Define password credential
		CredentialRepresentation passwordCred = new CredentialRepresentation();
		passwordCred.setTemporary(false);
		passwordCred.setType(CredentialRepresentation.PASSWORD);
		passwordCred.setValue(newPassword.toString().trim());

		// Set password credential
		userResource.get(userId).resetPassword(passwordCred);

	}

	public String sendPost(List<NameValuePair> urlParameters) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(AUTH_URL + "/realms/" + REALM + "/protocol/openid-connect/token");

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		HttpResponse response = client.execute(post);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

}

