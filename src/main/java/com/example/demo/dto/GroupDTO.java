package com.example.demo.dto;

import lombok.Data;
import org.keycloak.representations.idm.GroupRepresentation;

import java.util.List;

/**
 * GroupDTO
 * 组, 菜单
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 08:52:31
 **/
@Data
public class GroupDTO extends GroupRepresentation {
    private List<String> role_ids;
    private List<String> subGroup_ids;
}
