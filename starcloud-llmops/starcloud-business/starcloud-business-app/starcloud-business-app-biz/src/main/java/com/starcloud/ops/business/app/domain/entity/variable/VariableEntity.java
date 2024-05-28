package com.starcloud.ops.business.app.domain.entity.variable;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.JsonSchemaFactory;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ValueTypeSchema;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    @JsonIgnore
    @JSONField(serialize = false)
    public ObjectSchema getJsonSchema() {

        JsonSchemaFactory jsonSchemaFactory = new JsonSchemaFactory();

        ObjectSchema objectSchema = jsonSchemaFactory.objectSchema();
        //objectSchema.set$schema(SpecVersion.VersionFlag.V202012.getId());

        Map<String, JsonSchema> properties = new HashMap<>();

        for (VariableItemEntity variableItem : this.getVariables()) {

            //现在只支持string
            if (Arrays.asList(AppVariableStyleEnum.INPUT.name(),
                            AppVariableStyleEnum.TEXTAREA.name(),
                            AppVariableStyleEnum.IMAGE.name(),
                            AppVariableStyleEnum.SELECT.name())
                    .contains(variableItem.getStyle())) {

                ValueTypeSchema valueTypeSchema = new StringSchema();
                valueTypeSchema.setTitle(variableItem.getLabel());
                valueTypeSchema.setDescription(variableItem.getDescription() + "-" + AppVariableStyleEnum.IMAGE.name().toLowerCase());
                valueTypeSchema.setDefault((String.valueOf(Optional.ofNullable(variableItem.getValue()).orElseGet(variableItem::getDefaultValue))));

                if (Arrays.asList(AppVariableStyleEnum.SELECT.name()).contains(variableItem.getType())) {
                    valueTypeSchema.setEnums(Optional.ofNullable(variableItem.getOptions()).orElse(new ArrayList<>()).stream().map(Option::getValue).map(String::valueOf).collect(Collectors.toSet()));
                }

                properties.put(variableItem.getField(), valueTypeSchema);
            }
        }

        objectSchema.setProperties(properties);

        return objectSchema;
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

    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getVariableItem(String key) {
        return this.variables.stream()
                .filter(item -> item.getField().equalsIgnoreCase(key))
                .findFirst().orElse(null);
    }
}
