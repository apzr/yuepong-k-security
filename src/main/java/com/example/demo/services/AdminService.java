package com.example.demo.services;

import com.example.demo.dto.*;

/**
 * KeyCloakAdminService
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/17 16:46:02
 **/
public interface AdminService {

    /**
     * getToken
     *
     * @param userCredentials
     * @return java.lang.String
     * @author apr
     * @date 2021/9/26 11:19
     */
    String getToken(UserCredentials userCredentials) ;

    /**
     * getByRefreshToken
     *
     * @param refreshToken
     * @return java.lang.String
     * @author apr
     * @date 2021/9/26 11:19
     */
    String getByRefreshToken(String refreshToken) ;

	/**
     * 登出
     *
     * @param userId
     * @return void
     * @author apr
     * @date 2021/9/26 11:19
     */
    void logoutUser(String userId);

	/**
     * 重设密码
     *
     * @param newPassword
     * @param userId
     * @return void
     * @author apr
     * @date 2021/9/26 11:19
     */
    void resetPassword(String newPassword, String userId);

}
