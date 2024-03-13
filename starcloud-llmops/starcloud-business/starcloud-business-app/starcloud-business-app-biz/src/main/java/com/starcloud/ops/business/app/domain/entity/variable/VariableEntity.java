package com.starcloud.ops.business.app.domain.entity.variable;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeOptionDTO;
import com.starcloud.ops.business.app.enums.xhs.CreativeOptionModelEnum;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import lombok.Data;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * App 变量实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class VariableEntity {

    /**
     * json 格式通过 json ObjectMapper 去实现双向转换,方便处理
     */
    private Object jsonParams;

    /**
     * 应用变量
     */
    private List<VariableItemEntity> variables;


    /**
     * 获取 JsonSchema 格式的参数对象
     */
    public JsonNode getJsonSchema() {

        for (VariableItemEntity variableItem : this.getVariables()) {

        }

        return null;
    }

    /**
     * 获取指定类型的变量集合
     *
     * @param variable     待合并的变量
     * @param stepVariable 待合并的步骤变量
     * @param consumer     变量值处理函数
     * @param prefixKey    变量前缀
     * @param <V>          变量值类型
     * @return 合并后的变量集合
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static <V> Map<String, V> mergeVariables(VariableEntity variable, VariableEntity stepVariable, Function<VariableItemEntity, V> consumer, String prefixKey) {

        // 定义一个合并的变量集合。逐个添加，防止出现引用问题
        List<VariableItemEntity> mergeVariableList = new ArrayList<>();

        // variableEntity 变量集合
        List<VariableItemEntity> variablesList = Optional.ofNullable(stepVariable).map(VariableEntity::getVariables).orElse(new ArrayList<>());
        if (CollectionUtil.isNotEmpty(variablesList)) {
            mergeVariableList.addAll(variablesList);
        }

        // coverVariableEntity 变量集合
        List<VariableItemEntity> coverVariablesList = Optional.ofNullable(variable).map(VariableEntity::getVariables).orElse(new ArrayList<>());
        if (CollectionUtil.isNotEmpty(coverVariablesList)) {
            mergeVariableList.addAll(coverVariablesList);
        }

        // 合并变量集合
        Map<String, V> variablesMap = MapUtil.newHashMap();
        mergeVariableList.forEach(item -> {
            String allKey = generateKey(prefixKey, item.getField());
            if (consumer.apply(item) != null) {
                variablesMap.put(allKey.toUpperCase(), consumer.apply(item));
            }
        });

        return variablesMap;
    }

    /**
     * 生成变量Key
     *
     * @param keys 变量Key
     * @return 变量Key
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static String generateKey(String... keys) {
        return Arrays.stream(Optional.ofNullable(keys).orElse(new String[]{})).filter(StrUtil::isNotEmpty).map(String::toUpperCase).collect(Collectors.joining("."));
    }

    /**
     * 变量 校验
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {
        if (CollectionUtil.isNotEmpty(this.variables)) {
            this.variables.forEach(VariableItemEntity::validate);
        }
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(String key, Object value) {
        for (VariableItemEntity variable : this.variables) {
            if (variable.getField().equals(key)) {
                variable.setValue(value);
            }
        }
    }
}
