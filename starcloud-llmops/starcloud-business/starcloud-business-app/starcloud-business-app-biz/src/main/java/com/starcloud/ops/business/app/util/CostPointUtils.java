package com.starcloud.ops.business.app.util;

import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private static final List<String> GPT_4_MODEL_LIST = Arrays.asList(ModelTypeEnum.GPT_4.getName(), ModelTypeEnum.GPT_4_TURBO.getName(), ModelTypeEnum.GPT_4_32K.getName());

    /**
     * 根据模型获取消耗的权益点数
     *
     * @param model 模型
     * @return 权益点数
     */
    public static Integer obtainMagicBeanCostPoint(String model, Long tokens) {
        Integer modelPrice = computationalPower(model);
        Integer tokenPrice = calculationTokens(tokens);
        return modelPrice * tokenPrice;
    }

    /**
     * 获取模型单价，gpt4 模型单价为15, 其他模型单价为1
     *
     * @param model 模型
     * @return 模型单价
     */
    private static Integer computationalPower(String model) {
        model = StringUtils.isNoneBlank(model) ? model : ModelTypeEnum.GPT_3_5_TURBO.getName();
        // GPT-4 模型消耗15个权益点
        if (GPT_4_MODEL_LIST.contains(model)) {
            return 15;
        }
        return 1;
    }

    /**
     * 计算token 消耗的权益点数
     * 权益点数小于1000，消耗1个权益点
     * 权益点数大于等于1000，每500个token消耗1个权益点
     *
     * @param token token
     * @return 权益点数
     */
    private static Integer calculationTokens(Long token) {
        if (token < 1000) {
            return 1;
        }
        BigDecimal bigDecimal = new BigDecimal(token);
        return bigDecimal.divide(new BigDecimal(500), 0, RoundingMode.FLOOR).intValue();
    }

}
