package com.starcloud.ops.business.app.domain.entity.variable;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.*;
import java.util.function.Consumer;
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
     * 获取当前步骤的所有变量的values
     *
     * @return
     */
    @JSONField(serialize = false)
    public Map<String, Object> getVariablesValues() {

        Map<String, Object> variablesValues = MapUtil.newHashMap();

        Optional.ofNullable(variables).orElse(new ArrayList<>()).forEach(variableItemEntity -> {

            Object value = !ObjectUtil.isEmpty(variableItemEntity.getValue()) ? variableItemEntity.getValue() : variableItemEntity.getDefaultValue();

            variablesValues.put(variableItemEntity.getField(), value);
        });

        return variablesValues;
    }


    /**
     * 获取指定key的变量
     *
     * @param field
     * @return
     */
    @JSONField(serialize = false)
    public VariableItemEntity getVariable(String field) {
        return null;
    }


    /**
     * 获取指定类型的变量集合
     *
     * @param type
     * @return
     */
    @JSONField(serialize = false)
    public List<VariableItemEntity> getVariables(String type) {
        return Optional.ofNullable(this.variables).orElse(new ArrayList<>()).stream().filter(variableItemEntity -> variableItemEntity.getType().equals(type)).collect(Collectors.toList());
    }


    @JSONField(serialize = false)
    public static <V> Map<String, V> coverMergeVariables(VariableEntity coverVariableEntity, VariableEntity variableEntity, Function<VariableItemEntity, V> consumer, String prefixKey) {

        List<VariableItemEntity> self = CollectionUtil.emptyIfNull(variableEntity.variables);
        self.addAll(CollectionUtil.emptyIfNull(coverVariableEntity.variables));

        Map<String, V> variablesValues = MapUtil.newHashMap();

        Optional.ofNullable(self).orElse(new ArrayList<>()).forEach(variableItemEntity -> {

            String allKey = generateKey(prefixKey, variableItemEntity.getField());

            if (consumer.apply(variableItemEntity) != null) {
                variablesValues.put(allKey.toUpperCase(), consumer.apply(variableItemEntity));
            }
        });

        return variablesValues;
    }

    public static String generateKey(String... keys) {

        return Arrays.stream(Optional.ofNullable(keys).orElse(new String[]{})).filter(StrUtil::isNotEmpty).map(String::toUpperCase).collect(Collectors.joining("."));
    }

}
