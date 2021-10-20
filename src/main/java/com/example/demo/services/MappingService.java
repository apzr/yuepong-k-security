package com.example.demo.services;

import com.example.demo.dto.MappingDTO;
import com.example.demo.dto.MappingsDTO;

import java.util.List;

/**
 * MappingService
 * <p>
 * <br/>
 *
 * @author apr
 * @date 2021/10/20 10:32:35
 **/
public interface MappingService {
    /**
     * 创建用户角色映射
     *
     * @param mappingDTO
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    int createMapping(MappingDTO mappingDTO) ;
    int createMapping(MappingsDTO mappingsDTO);

    /**
     * 删除角色用户映射
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    void deleteMapping(MappingDTO mappingDTO) ;

    /**
     * 获取映射列表
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    List<MappingDTO> getMappingsByUser(String uid) ;

    /**
     * 获取映射列表
     *
     * @return int
     * @author apr
     * @date 2021/9/26 11:19
     */
    List<MappingDTO> listMappings() ;
}
