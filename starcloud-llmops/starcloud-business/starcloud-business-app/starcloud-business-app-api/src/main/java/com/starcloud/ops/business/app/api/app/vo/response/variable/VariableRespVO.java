package com.starcloud.ops.business.app.api.app.vo.response.variable;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用请求 action 请求对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用变量响应对象VO")
public class VariableRespVO implements Serializable {

    private static final long serialVersionUID = 8559448054450544431L;

    /**
     * 应用变量
     */
    @Schema(description = "应用变量")
    private List<VariableItemRespVO> variables;


    /**
     * 配置的数据
     * v2
     */
    @Schema(description = "配置的数据")
    private Object data;

    /**
     * jsonSchema
     * v2
     */
    @Schema(description = "JsonSchema配置")
    private String jsonSchema;

    /**
     * 补充默认变量
     * 如果变量已存在则跳过
     *
     * @param variableResponse 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void supplementStepVariable(VariableRespVO variableResponse) {
        if (Objects.isNull(variableResponse)) {
            return;
        }

        List<VariableItemRespVO> variableList = variableResponse.getVariables();
        if (CollectionUtil.isEmpty(variableList)) {
            return;
        }
        // 如果变量为空则直接赋值
        if (CollectionUtil.isEmpty(variables)) {
            variables = variableList;
            return;
        }
        // 否则进行变量的更新操作
        Map<String, VariableItemRespVO> variableItemMap = variables.stream().collect(Collectors.toMap(VariableItemRespVO::getField, Function.identity()));
        
        // 现在用户无法修改变量的其余属性，所以其余属性应该保证是系统提供。
        // 未来如果有变化，需要进行修改此处 TODO
        for (VariableItemRespVO itemResponse : variableList) {
            VariableItemRespVO originalVariableResponse = variableItemMap.get(itemResponse.getField());
            if (Objects.isNull(originalVariableResponse)) {
                continue;
            }

            itemResponse.setValue(originalVariableResponse.getValue());
            itemResponse.setDefaultValue(originalVariableResponse.getDefaultValue());
            itemResponse.setIsShow(originalVariableResponse.getIsShow());
        }
        variables = variableList;
    }

    /**
     * 获取变量列表
     *
     * @return 变量列表
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public List<VariableItemRespVO> variableList() {
        return CollectionUtil.emptyIfNull(this.getVariables()).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 根据变量的{@code field}获取变量，找不到时返回{@code null}
     *
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO getItem(String field) {
        if (StringUtils.isBlank(field)) {
            return null;
        }
        for (VariableItemRespVO item : variableList()) {
            if (field.equalsIgnoreCase(item.getField())) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据变量的{@code field}获取变量的值，并且将值转换为字符串，找不到时返回空字符串
     *
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getVariableToString(String field) {
        Object value = getVariable(field);
        if (Objects.isNull(value)) {
            return StringUtils.EMPTY;
        }
        return String.valueOf(value);
    }

    /**
     * 根据变量的{@code field}获取变量的值，找不到时返回null
     *
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getVariable(String field) {
        VariableItemRespVO variable = getItem(field);
        if (Objects.isNull(variable)) {
            return null;
        }
        return variable.getValue();
    }

    /**
     * 将变量为{@code field}的值设置为{@code value}
     *
     * @param field 变量的{@code field}
     * @param value 变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(String field, Object value) {
        if (StringUtils.isBlank(field)) {
            return;
        }
        for (VariableItemRespVO item : variableList()) {
            if (field.equalsIgnoreCase(item.getField())) {
                item.setValue(value);
                return;
            }
        }
    }

    /**
     * 合并变量
     *
     * @param variable 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void merge(VariableRespVO variable) {
        // 如果变量为空或者变量为空则不进行合并
        if (CollectionUtil.isEmpty(this.variables) || CollectionUtil.isEmpty(variable.getVariables())) {
            return;
        }

        List<VariableItemRespVO> mergeVariableList = new ArrayList<>();

        // 将变量转换为map
        Map<String, VariableItemRespVO> variableItemMap = variable.variables.stream()
                .collect(Collectors.toMap(VariableItemRespVO::getField, Function.identity()));

        for (VariableItemRespVO variableItem : this.variables) {
            // 如果最新的变量列表中没有原来的变量，则以最新的值为准。
            if (!variableItemMap.containsKey(variableItem.getField()) || Objects.isNull(variableItemMap.get(variableItem.getField()))) {
                mergeVariableList.add(variableItem);
                continue;
            }

            VariableItemRespVO item = variableItemMap.get(variableItem.getField());
            variableItem.merge(item);
            mergeVariableList.add(variableItem);
            variableItemMap.remove(variableItem.getField());
        }

        if (CollectionUtil.isNotEmpty(variableItemMap)) {
            variableItemMap.forEach((key, value) -> mergeVariableList.add(value));
        }

        this.variables = mergeVariableList;
    }

    public void supplementStepVariableValue(Map<String, WorkflowStepWrapperRespVO> supplementStepWrapperMap) {
        List<VariableItemRespVO> list = CollectionUtil.emptyIfNull(this.variables);
        for (VariableItemRespVO item : list) {
            String value = StringUtils.defaultIfBlank(String.valueOf(item.getValue()), StringUtils.EMPTY);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            value = value.replaceAll("上传素材", "素材库字段设置");
            item.setValue(value);
        }
        this.variables = list;
    }
}
