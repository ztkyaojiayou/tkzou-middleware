package com.tkzou.middleware.configcenter.server.controller;

import com.tkzou.middleware.configcenter.server.servie.ConfigService;
import com.tkzou.middleware.configcenter.server.domain.entity.ConfigFile;
import com.tkzou.middleware.configcenter.server.domain.request.CreateConfigRequest;
import com.tkzou.middleware.configcenter.server.domain.request.UpdateConfigRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 配置中心
 * @author zoutongkun
 */
@RestController
@RequestMapping("/tkzou/config")
public class ConfigController {

    @Resource
    private ConfigService configService;

    @PostMapping
    public String create(@RequestBody CreateConfigRequest createConfigRequest) {
        return configService.save(createConfigRequest.getName(), createConfigRequest.getExtension(),
                createConfigRequest.getContent());
    }

    @PutMapping
    public void update(@RequestBody UpdateConfigRequest updateConfigRequest) {
        configService.update(updateConfigRequest.getFileId(), updateConfigRequest.getName(),
                updateConfigRequest.getExtension(), updateConfigRequest.getContent());
    }

    @DeleteMapping("/{fileId}")
    public void delete(@PathVariable("fileId") String fileId) {
        configService.delete(fileId);
    }

    @GetMapping("/{fileId}")
    public ConfigFile select(@PathVariable("fileId") String fileId) {
        return configService.selectByFileId(fileId);
    }

    @GetMapping("/all")
    public List<ConfigFile> selectAll() {
        return configService.selectAll();
    }

}
