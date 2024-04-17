package com.tkzou.middleware.configcenter.server.mapper;

import com.tkzou.middleware.configcenter.server.domain.entity.ConfigFile;

import java.util.List;

/**
 * 配置中心存储层
 * 
 *
 * @author zoutongkun
 * @date 2022/9/29 23:42
 */
public interface ConfigMapper {

    /**
     * 保存
     *
     * @param configFile
     */
    void save(ConfigFile configFile);

    /**
     * 修改
     *
     * @param configFile
     */
    void update(ConfigFile configFile);

    /**
     * 删除文件
     *
     * @param fileId
     */
    void delete(String fileId);

    /**
     * 通过文件id查找
     *
     * @param fileId
     * @return
     */
    ConfigFile selectByFileId(String fileId);

    /**
     * 查找所有
     *
     * @return
     */
    List<ConfigFile> selectAll();

}
