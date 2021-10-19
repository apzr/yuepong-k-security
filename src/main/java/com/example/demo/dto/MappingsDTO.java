package com.example.demo.dto;

import lombok.Data;

import java.util.List;

/**
 * MappingsDTO
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/09/26 14:06:00
 **/
@Data
public class MappingsDTO {

    private String userId;
	private List<String> roleIds;
}
