package com.starcloud.ops.business.app.domain.entity.variable;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * App 单个变量实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class VariableItemEntity {

    /**
     * 变量 label
     */
    private String label;

    /**
     * 变量 field
     */
    private String field;

    /**
     * 变量类型
     */
    private String type;

    /**
     * 变量分组
     */
    private String group;

    /**
     * 变量样式
     */
    private String style;

    /**
     * 变量排序
     */
    private Integer order;

    /**
     * 变量默认值
     */
    private Object defaultValue;

    /**
     * 变量值
     */
    private Object value;

    /**
     * 变量是否显示
     */
    private Boolean isShow;

    /**
     * 变量是否为点位
     */
    private Boolean isPoint;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 变量选项, 变量类型为 SELECT 时使用
     */
    private List<Option> options;

    /**
     * 变量校验规则
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {
        if ("MAX_TOKENS".equalsIgnoreCase(this.field)) {
            this.value = handleMaxTokens(this.value, 1000);
            this.defaultValue = this.value;
        }
        if ("TEMPERATURE".equalsIgnoreCase(this.field)) {
            this.value = handleTemperature(this.value, 0.7);
            this.defaultValue = this.value;
        }
    }

    /**
     * MaxTokens 变量校验规则
     *
     * @param value            变量值
     * @param defaultMaxTokens 默认值
     * @return 校验后的变量值
     */
    @SuppressWarnings("all")
    private static Integer handleMaxTokens(Object value, Integer defaultMaxTokens) {
        Integer maxTokens;
        try {
            maxTokens = Objects.isNull(value) ? defaultMaxTokens : Integer.valueOf(value.toString());
        } catch (NumberFormatException exception) {
            log.error("MaxTokens 变量校验规则异常，value：{}", value, exception);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.MAX_TOKENS_FORMAT_ERROR, value);
        }

        // 1 <= maxTokens <= 4000
//        if (maxTokens < 1 || maxTokens > 4000) {
//            throw ServiceExceptionUtil.exception(ErrorCodeConstants.MAX_TOKENS_OUT_OF_LIMIT, maxTokens);
//        }
        return maxTokens;
    }

    /**
     * Temperature 变量校验规则
     *
     * @param value              变量值
     * @param defaultTemperature 默认值
     * @return 校验后的变量值
     */
    @SuppressWarnings("all")
    private static Double handleTemperature(Object value, Double defaultTemperature) {
        Double temperature;
        try {
            temperature = Objects.isNull(value) ? defaultTemperature : Double.valueOf(value.toString());
        } catch (NumberFormatException exception) {
            log.error("Temperature 变量校验规则异常，value：{}", value, exception);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.TEMPERATURE_FORMAT_ERROR, value);
        }
        // 0.0 <= temperature <= 2.0
        if (temperature.compareTo(0.0) < 0 || temperature.compareTo(2.0) > 0) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.TEMPERATURE_OUT_OF_LIMIT, temperature);
        }
        return temperature;
    }

}
