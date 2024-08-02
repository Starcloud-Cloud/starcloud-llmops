package com.starcloud.ops.business.app.domain.entity.variable;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
     * 数量
     */
    private Integer count;

    /**
     * 变量校验规则
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {
        AppValidate.notBlank(this.getField(), "应用步骤变量 field 不能为空!");
    }


}
