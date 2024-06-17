package com.starcloud.ops.business.app.api.app.vo.response.variable;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
     * 补充默认变量
     * 如果变量已存在则跳过
     *
     * @param variableItemRespVO
     */
    public void supplementStepVariable(List<VariableItemRespVO> variableItemRespVO) {
        if (CollectionUtil.isEmpty(variableItemRespVO)) {
            return;
        }
        if (CollectionUtil.isEmpty(variables)) {
            variables = variableItemRespVO;
            return;
        }

        for (VariableItemRespVO itemRespVO : variableItemRespVO) {
            boolean contains = variables.stream().anyMatch(variable -> Objects.equals(itemRespVO.getField(), variable.getField()));
            if (contains) {
                continue;
            }
            variables.add(itemRespVO);
        }
    }

    /**
     * 放入变量值
     *
     * @param variable 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(Map<String, Object> variable) {
        for (VariableItemRespVO item : variables) {
            if (variable.containsKey(item.getField())) {
                item.setValue(Optional.ofNullable(variable.get(item.getField())).orElse(""));
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

        if (CollectionUtil.isEmpty(this.variables) || CollectionUtil.isEmpty(variable.getVariables())) {
            return;
        }

        List<VariableItemRespVO> mergeVariableList = new ArrayList<>();

        Map<String, VariableItemRespVO> variableItemMap = variable.variables.stream()
                .collect(Collectors.toMap(VariableItemRespVO::getField, Function.identity()));

        for (VariableItemRespVO variableItem : this.variables) {
            if (!variableItemMap.containsKey(variableItem.getField()) ||
                    Objects.isNull(variableItemMap.get(variableItem.getField()))) {
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
}
