package com.starcloud.ops.business.app.domain.entity2.config;

import com.starcloud.ops.business.app.domain.entity2.action.LLMFunctionEntity;
import lombok.Data;

import java.util.List;

/**
 * 聊天应用配置DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
public class ChatConfigEntity extends BaseConfigEntity {




    private String code;

    /**
     * 挂载的 functions 列表
     */
    private List<LLMFunctionEntity> functions;


    @Override
    void validate() {

    }

}
