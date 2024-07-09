package com.starcloud.ops.business.app.domain.entity.chat;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * llm标识
 *
 * @author starcloud
 */
@Getter
public enum ModelProviderEnum implements IEnumable<Integer> {

    GPT35(1, "默认模型3.5", "", ModelTypeEnum.GPT_3_5_TURBO),

    GPT4(2, "默认模型4.0", "chat:config:llm:gpt4", ModelTypeEnum.GPT_4_O),

    QWEN(3, "通义千问", "chat:config:llm:qwen", ModelTypeEnum.QWEN),

    QWEN_MAX(4, "通义千问Max", "chat:config:llm:qwen:max", ModelTypeEnum.QWEN_MAX);

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 标签
     */
    private final String label;

    /**
     * 权限
     */
    private final String permissions;

    /**
     * 模型类型
     */
    private final ModelTypeEnum modelType;

    /**
     * 构造函数
     *
     * @param code        编码
     * @param label       标签
     * @param permissions 权限
     * @param modelType   模型类型
     */
    ModelProviderEnum(Integer code, String label, String permissions, ModelTypeEnum modelType) {
        this.code = code;
        this.label = label;
        this.permissions = permissions;
        this.modelType = modelType;
    }

    /**
     * 通过编码，获得枚举
     *
     * @param name 编码
     * @return 枚举
     */
    public static ModelProviderEnum fromName(String name) {
        return Arrays.stream(values())
                .filter(e -> StringUtils.equals(e.name(), name))
                .findFirst()
                .orElse(GPT35);
    }
}