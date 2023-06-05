package com.starcloud.ops.business.app.api;


import com.starcloud.ops.business.app.api.dto.TemplateDTO;
import com.starcloud.ops.business.app.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 模版服务 API 实现
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Slf4j
@Service
public class TemplateApiImpl implements TemplateApi {

    @Resource
    private TemplateService templateService;

    /**
     * 根据模版 ID 获取模版信息
     *
     * @param id 模版 ID
     * @return 模版信息
     */
    @Override
    public TemplateDTO get(Long id) {
        return templateService.getById(id);
    }
}
