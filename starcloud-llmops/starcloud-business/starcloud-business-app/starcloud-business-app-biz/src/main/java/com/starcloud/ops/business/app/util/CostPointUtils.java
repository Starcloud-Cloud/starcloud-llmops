package com.starcloud.ops.business.app.util;

import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public class CostPointUtils {

    /**
     * GPT-4 模型列表
     */
    private static final List<String> GPT_4_MODEL_LIST = Arrays.asList(
            ModelTypeEnum.GPT_4.getName(),
            ModelTypeEnum.GPT_4_TURBO.getName(),
            ModelTypeEnum.GPT_4_32K.getName()
    );

    /**
     * 根据模型获取消耗的权益点数
     *
     * @param model 模型
     * @return 权益点数
     */
    public static Integer obtainMagicBeanCostPoint(String model) {
        model = StringUtils.isNoneBlank(model) ? model : ModelTypeEnum.GPT_3_5_TURBO_16K.getName();
        // GPT-4 模型消耗30个权益点
        if (GPT_4_MODEL_LIST.contains(model)) {
            return 15;
        }
        return 1;
    }

    /**
     * 海报生成消耗的权益点数
     *
     * @return 权益点数
     */
    public static Integer obtainPosterCostPoint() {
        return 1;
    }

}
