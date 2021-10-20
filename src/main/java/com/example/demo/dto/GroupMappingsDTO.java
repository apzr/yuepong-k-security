package com.example.demo.dto;

import lombok.Data;

import java.util.List;

/**
 * GroupMappingsDTO
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 16:43:33
 **/
@Data
public class GroupMappingsDTO {
    private List<String> userIds;
    private String groupId;
}
