package com.example.demo.config;
/**
import com.example.demo.dto.UserDTO;
import org.keycloak.models.UserModel;

import java.util.stream.Collectors;


 * UserMapper
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 15:26:31

public class UserMapper {

    public UserDTO mapToUserDto(UserModel um) {
        return new UserDTO(
            um.getUsername(),
            um.getFirstName(),
            um.getLastName(),
            um.getId(),
            um.getEmail(),
            um.getAttributeStream("gender").collect(Collectors.joining())
        );
    }
}
**/