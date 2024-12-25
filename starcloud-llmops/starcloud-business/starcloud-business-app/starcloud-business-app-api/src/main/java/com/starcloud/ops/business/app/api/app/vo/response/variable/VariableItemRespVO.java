package com.starcloud.ops.business.app.api.app.vo.response.variable;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用变量响应对象VO")
public class VariableItemRespVO implements Serializable {

    private static final long serialVersionUID = -8180140995465871920L;

    /**
     * 变量 label
     */
    @Schema(description = "变量 label")
    private String label;

    /**
     * 变量 field
     */
    @Schema(description = "变量 field")
    private String field;

    /**
     * 变量类型
     */
    @Schema(description = "变量类型")
    private String type;

    /**
     * 变量样式
     */
    @Schema(description = "变量样式")
    private String style;

    /**
     * 变量分组
     */
    @Schema(description = "变量分组")
    private String group;

    /**
     * 变量排序
     */
    @Schema(description = "变量排序")
    private Integer order;

    /**
     * 变量默认值
     */
    @Schema(description = "变量默认值")
    private Object defaultValue;

    /**
     * 变量值
     */
    @Schema(description = "变量值")
    private Object value;

    /**
     * 变量是否显示
     */
    @Schema(description = "变量是否显示")
    private Boolean isShow;

    /**
     * 变量是否为点位
     */
    @Schema(description = "变量是否为点位")
    private Boolean isPoint;

    /**
     * 是否升级<br>
     * 如果该值为 true, 则保留用户配置的值，否则则使用系统默认值。
     */
    private Boolean isKeepUserValue;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 变量选项, 变量类型为 SELECT 时使用
     */
    @Schema(description = "变量选项")
    private List<Option> options;

    /**
     * 数量
     */
    @Schema(description = "数量")
    private Integer count;

    /**
     * 添加选项
     *
     * @param label label
     * @param value value
     * @return this
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO addOption(String label, Object value) {
        if (CollectionUtil.isEmpty(this.options)) {
            this.options = Collections.emptyList();
        }
        List<Option> optionList = new ArrayList<>(this.options);
        optionList.add(Option.of(label, value));
        this.options = optionList;
        return this;
    }

    /**
     * 添加选项
     *
     * @param label label
     * @param value value
     * @return this
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO addOption(String label, Object value, String description) {
        if (CollectionUtil.isEmpty(this.options)) {
            this.options = Collections.emptyList();
        }
        List<Option> optionList = new ArrayList<>(this.options);
        Option option = Option.of(label, value);
        option.setDescription(description);
        optionList.add(option);
        this.options = optionList;
        return this;
    }

    /**
     * 构建文本变量
     *
     * @param field 字段
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static VariableItemRespVO ofTextVariable(String field, String label) {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(field);
        variableItem.setLabel(label);
        variableItem.setDescription(label);
        variableItem.setDefaultValue(null);
        variableItem.setValue(null);
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }

    /**
     * 构建图片变量
     *
     * @param field 字段
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static VariableItemRespVO ofImageVariable(String field, String label) {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(field);
        variableItem.setLabel(label);
        variableItem.setDescription(label);
        variableItem.setDefaultValue(null);
        variableItem.setValue(null);
        variableItem.setOrder(1);
        variableItem.setType("IMAGE");
        variableItem.setStyle("IMAGE");
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }

    /**
     * 合并变量
     *
     * @param item 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void merge(VariableItemRespVO item) {
        // 如果变量是可见的，则使用原来的值，否则最用最新变量的值。
        if (Objects.nonNull(isKeepUserValue) && this.isKeepUserValue) {
            this.defaultValue = item.getDefaultValue();
            this.value = item.getValue();
        }
    }
}
