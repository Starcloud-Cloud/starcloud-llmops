package com.starcloud.ops.business.app.domain.entity.config;


import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.api.verification.Verification;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 聊天 使用的 prompt 定义配置
 */
@Data
public class ChatPromptConfig extends BaseConfigEntity {

    private String chatBasePromptV1 = "";

    @Override
    public List<Verification> validate(String uid, ValidateTypeEnum validateType) {
        return Collections.emptyList();
    }
}
