package com.starcloud.ops.business.app.domain.entity.config;


import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import lombok.Data;

/**
 * 聊天 使用的 prompt 定义配置
 */
@Data
public class ChatPromptConfig extends BaseConfigEntity {

    private String chatBasePromptV1 = "";


    @Override
    public void validate(ValidateTypeEnum validateType) {

    }
}
