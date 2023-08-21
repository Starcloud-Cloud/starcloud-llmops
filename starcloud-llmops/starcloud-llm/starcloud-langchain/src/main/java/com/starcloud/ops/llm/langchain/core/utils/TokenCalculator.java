package com.starcloud.ops.llm.langchain.core.utils;

import com.knuddels.jtokkit.api.ModelType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class TokenCalculator {

    public static ModelType fromName(String model) {
        return ModelType.fromName(model).orElse(ModelType.GPT_3_5_TURBO);
    }

    public static BigDecimal getTextPrice(Long tokens, ModelType modelType) {
        BigDecimal price = new BigDecimal(tokens).
                divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP)
                .multiply(getUnitPrice(modelType, true), MathContext.DECIMAL32);
        return price;
    }

    public static BigDecimal getTextPrice(Long tokens, ModelType modelType, Boolean isOutput) {
        BigDecimal price = new BigDecimal(tokens).
                divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP)
                .multiply(getUnitPrice(modelType, isOutput), MathContext.DECIMAL32);
        return price;
    }


    public static BigDecimal getUnitPrice(ModelType modelType, Boolean isOutput) {
        // todo  不同模型计算基数补充
        BigDecimal unitPrice;
        switch (modelType) {
            case TEXT_EMBEDDING_ADA_002:
                unitPrice = new BigDecimal(0.0001);
                break;
            case GPT_3_5_TURBO:
            case TEXT_DAVINCI_003:
                unitPrice = isOutput ? new BigDecimal(0.0015) : new BigDecimal(0.002);
                break;
            case GPT_3_5_TURBO_16K:
                unitPrice = isOutput ? new BigDecimal(0.003) : new BigDecimal(0.004);
                break;
            case GPT_4:
                unitPrice = isOutput ? new BigDecimal(0.03) : new BigDecimal(0.06);
                break;
            case GPT_4_32K:
                unitPrice = isOutput ? new BigDecimal(0.06) : new BigDecimal(0.12);
                break;
            default:
                unitPrice = new BigDecimal(0);
        }
        return unitPrice;
    }


}
