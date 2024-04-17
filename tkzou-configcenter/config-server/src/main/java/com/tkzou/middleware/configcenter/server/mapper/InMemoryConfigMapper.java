package com.tkzou.middleware.configcenter.server.mapper;

import com.tkzou.middleware.configcenter.server.domain.entity.ConfigFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用内存存储
 *
 * @author zoutongkun
 */
public class InMemoryConfigMapper implements ConfigMapper {
    /**
     * 保存配置文件的map
     * key：fieldId，value：配置文件内容
     */
    private final Map<String, ConfigFile> configFileMap = new ConcurrentHashMap<>();

    @Override
    public void save(ConfigFile configFile) {
        configFileMap.put(configFile.getFileId(), configFile);
    }

    @Override
    public void update(ConfigFile configFile) {
        configFileMap.put(configFile.getFileId(), configFile);
    }

    @Override
    public void delete(String fileId) {
        configFileMap.remove(fileId);
    }

    @Override
    public ConfigFile selectByFileId(String fileId) {
        return configFileMap.get(fileId);
    }

    @Override
    public List<ConfigFile> selectAll() {
        return new ArrayList<>(configFileMap.values());
    }

}
