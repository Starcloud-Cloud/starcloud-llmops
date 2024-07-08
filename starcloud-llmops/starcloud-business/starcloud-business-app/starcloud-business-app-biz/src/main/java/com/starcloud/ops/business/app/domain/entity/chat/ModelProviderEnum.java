package com.starcloud.ops.business.app.domain.entity.chat;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
     * 获取枚举的定义和大模型的映射关系
     *
     * @return 枚举
     */
    public static List<Option> options() {
        return Arrays.stream(ModelProviderEnum.values())
                .sorted(Comparator.comparingInt(ModelProviderEnum::ordinal))
                .map(ModelProviderEnum::option)
                .collect(Collectors.toList());
    }

    /**
     * 获取枚举的定义和大模型的映射关系
     *
     * @param model 模型
     * @return 枚举
     */
    public static Option option(ModelProviderEnum model) {
        Option option = new Option();
        option.setLabel(model.name());
        option.setValue(model.getModelType().getName());
        option.setPermissions(model.getPermissions());
        return option;
    }

    /**
     * 根据模型获取到权限信息
     *
     * @param model 模型
     * @return 权限信息
     */
    public static String getPermissions(String model) {
        return Arrays.stream(ModelProviderEnum.values())
                .filter(modelProviderEnum -> modelProviderEnum.name().equals(model))
                .findFirst()
                .map(ModelProviderEnum::getPermissions)
                .orElse(StringUtils.EMPTY);
    }
}