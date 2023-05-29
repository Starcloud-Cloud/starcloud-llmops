package com.starcloud.ops.business.app.api;


import com.starcloud.ops.business.app.dal.mysql.LlmBusinessAppMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public abstract class TemplateApiImpl implements TemplateApi {

    @Resource
    private LlmBusinessAppMapper llmBusinessAppMapper;

//    @Autowired
//    private SsAigcTemplateDOMapper templateDOMapper;
//
//    @Autowired
//    private TemplateCache templateCache;
//
//    @Autowired
//    private RuoyiClient ruoyiClient;

}
