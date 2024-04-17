package com.tkzou.middleware.configcenter.server.servie;

import com.tkzou.middleware.configcenter.server.domain.entity.ConfigFile;
import com.tkzou.middleware.configcenter.server.mapper.ConfigMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * config manager
 *
 * @author zoutongkun
 * @date 2022/9/30 00:01
 */
@Component
public class ConfigService {

    @Resource
    private ConfigMapper configMapper;

    /**
     * 保存配置文件
     *
     * @param name
     * @param extension
     * @param content
     * @return
     */
    public String save(String name, String extension, String content) {
        ConfigFile configFile = new ConfigFile();
        configFile.setFileId(UUID.randomUUID().toString());
        configFile.setName(name);
        configFile.setExtension(extension);
        configFile.setContent(content);
        configFile.setLastUpdateTimestamp(System.currentTimeMillis());

        configMapper.save(configFile);

        return configFile.getFileId();
    }

    /**
     * 更新配置文件
     *
     * @param fileId
     * @param name
     * @param extension
     * @param content
     */
    public void update(String fileId, String name, String extension, String content) {
        ConfigFile configFile = new ConfigFile();
        configFile.setFileId(fileId);
        configFile.setName(name);
        configFile.setExtension(extension);
        configFile.setContent(content);
        configFile.setLastUpdateTimestamp(System.currentTimeMillis());
        configMapper.update(configFile);
    }

    /**
     * 删除配置文件
     *
     * @param fileId
     */
    public void delete(String fileId) {
        configMapper.delete(fileId);
    }

    /**
     * 根据文件id获取文件信息
     *
     * @param fileId
     * @return
     */
    public ConfigFile selectByFileId(String fileId) {
        return configMapper.selectByFileId(fileId);
    }

    /**
     * 获取所有文件
     *
     * @return
     */
    public List<ConfigFile> selectAll() {
        return configMapper.selectAll();
    }

}
