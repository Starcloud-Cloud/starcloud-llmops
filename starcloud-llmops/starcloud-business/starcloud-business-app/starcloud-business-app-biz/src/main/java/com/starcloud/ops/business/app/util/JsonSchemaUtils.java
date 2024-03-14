package com.starcloud.ops.business.app.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.starcloud.ops.business.app.api.xhs.material.dto.BookListCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeOptionDTO;
import com.starcloud.ops.business.app.enums.xhs.CreativeOptionModelEnum;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@UtilityClass
public class JsonSchemaUtils {

    /**
     * 根节点编码。
     */
    public static final String ROOT = "ROOT";

    /**
     * JSON Schema 的 $schema 字段。
     */
    public static final String JSON_SCHEMA = "$schema";

    /**
     * JSON Schema 的 properties 字段。
     */
    public static final String PROPERTIES = "properties";

    /**
     * JSON Schema 的 items 字段。
     */
    public static final String ITEMS = "items";

    /**
     * JSON Schema 的 type 字段。
     */
    public static final String TYPE = "type";

    /**
     * JSON Schema 的 description 字段。
     */
    public static final String DESCRIPTION = "description";

    /**
     * JSON Schema 的 title 字段。
     */
    public static final String TITLE = "title";

    /**
     * JSON Schema 的 object 类型。
     */
    public static final String OBJECT = "object";

    /**
     * JSON Schema 的 array 类型。
     */
    public static final String ARRAY = "array";

    /**
     * JSON Schema 的 string 类型。
     */
    public static final String STRING = "string";

    /**
     * JSON Schema 的 number 类型。
     */
    public static final String NUMBER = "number";

    /**
     * JSON Schema 的 integer 类型。
     */
    public static final String INTEGER = "integer";

    /**
     * JSON Schema 的 boolean 类型。
     */
    public static final String BOOLEAN = "boolean";

    public static final String ALL_OF = "allOf";


    /**
     * Jackson JSON 处理器的配置。
     * 用于反序列化和其他 JSON 操作的对象映射器 。
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModules(new JavaTimeModule()); // 解决 LocalDateTime 的序列化
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 根据给定的 Java 类生成对应的 JSON Schema。
     *
     * @param clazz 给定的 Java 类
     * @return 生成的 JSON Schema
     */
    public static String generateJsonSchemaStr(Class<?> clazz) {
        try {

            JsonNode jsonNode = generateJsonNode(clazz);
            DefaultIndenter defaultIndenter = new DefaultIndenter()
                    .withLinefeed("\n");
            DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter()
                    .withObjectIndenter(defaultIndenter);
            ObjectWriter objectWriter = new ObjectMapper()
                    .writer(defaultPrettyPrinter);
            // 生成 JSON Schema
            return objectWriter.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not pretty print json schema for " + clazz, e);
        }
    }


    /**
     * 根据给定的 Java 类生成对应的 JSON Schema。
     *
     * @param clazz 给定的 Java 类
     * @return 生成的 JSON Schema
     */
    @Deprecated
    public static JsonNode generateJsonNode(Class<?> clazz) {
        try {
            JacksonModule jacksonModule = new JacksonModule();
            SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)
                    .with(jacksonModule);
            SchemaGeneratorConfig config = configBuilder.build();

            SchemaGenerator generator = new SchemaGenerator(config);

            return generator.generateSchema(clazz);

        } catch (Exception e) {
            throw new RuntimeException("Could not generateJsonSchemaNode for " + clazz, e);
        }
    }


    /**
     * 根据给定的 Java 类生成对应的 JSON Schema。
     *
     * @param clazz 给定的 Java 类
     * @return 生成的 JSON Schema
     */
    public static JsonSchema generateJsonSchema(Class<?> clazz) {
        try {

            JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(OBJECT_MAPPER);

            return jsonSchemaGenerator.generateSchema(clazz);

        } catch (Exception e) {
            throw new RuntimeException("Could not generateSchema for " + clazz, e);
        }
    }

    /**
     * 根据给定的 Java 类生成对应的 JSON Schema。
     *
     * @param jsonNode 给定的 Java 类
     * @return 生成的 JSON Schema
     */
    public static String jsonNode2Str(Object jsonNode) {

        if (ObjectUtil.isNull(jsonNode)) {
            return null;
        }

        try {

            DefaultIndenter defaultIndenter = new DefaultIndenter()
                    .withLinefeed("\n");
            DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter()
                    .withObjectIndenter(defaultIndenter);
            ObjectWriter objectWriter = new ObjectMapper()
                    .writer(defaultPrettyPrinter);
            // 生成 JSON Schema
            return objectWriter.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JsonNode Could not pretty print json schema: " + e.getMessage(), e);
        }
    }

    /**
     * 将具体的类转换为 Option。
     *
     * @param clazz JSON Schema
     * @param code  编码
     * @param model 模型
     * @return 选项列表
     */
    public static CreativeOptionDTO jsonSchemaToOptions(Class<?> clazz, String code, String model) {
        String jsonSchema = generateJsonSchemaStr(clazz);
        return jsonSchemaToOptions(jsonSchema, code, code, StringUtils.EMPTY, model, Boolean.FALSE);
    }

    /**
     * 将具体的类转换为 Option。
     *
     * @param clazz JSON Schema
     * @param code  编码
     * @param name  名称
     * @param model 模型
     * @return 选项列表
     */
    public static CreativeOptionDTO jsonSchemaToOptions(Class<?> clazz, String code, String name, String model) {
        String jsonSchema = generateJsonSchemaStr(clazz);
        return jsonSchemaToOptions(jsonSchema, code, name, StringUtils.EMPTY, model, Boolean.FALSE);
    }

    /**
     * 将JSON Schema 转换为选项列表。
     *
     * @param jsonSchema JSON Schema
     * @param code       编码
     * @param model      模型
     * @return 选项列表
     */
    public static CreativeOptionDTO jsonSchemaToOptions(String jsonSchema, String code, String model) {
        return jsonSchemaToOptions(jsonSchema, code, code, StringUtils.EMPTY, model, Boolean.FALSE);
    }

    /**
     * 将JSON Schema 转换为选项列表。
     *
     * @param jsonSchema JSON Schema
     * @param code       编码
     * @param name       名称
     * @param model      模型
     * @return 选项列表
     */
    public static CreativeOptionDTO jsonSchemaToOptions(String jsonSchema, String code, String name, String model) {
        return jsonSchemaToOptions(jsonSchema, code, name, name, model, Boolean.FALSE);
    }

    /**
     * 将JSON Schema 转换为选项列表。
     *
     * @param jsonSchema  JSON Schema
     * @param code        编码
     * @param name        名称
     * @param description 描述
     * @param model       模型
     * @return 选项列表
     */
    public static CreativeOptionDTO jsonSchemaToOptions(String jsonSchema, String code, String name, String description, String model) {
        return jsonSchemaToOptions(jsonSchema, code, name, description, model, Boolean.FALSE);
    }


    public static JsonNode str2JsonNode(String jsonSchema) {

        try {

            JsonNode jsonNode = OBJECT_MAPPER.readTree(jsonSchema);
            // 检查是否为 JSON Schema，如果不是，则抛出异常
            Assert.isTrue(
                    ObjectUtil.isNotNull(jsonNode) &&
                            jsonNode.has(JSON_SCHEMA) && StringUtils.isNotBlank(jsonNode.get(JSON_SCHEMA).asText()),
                    "The given JSON is not a JSON Schema"
            );

//        if (jsonNode.has(ALL_OF)) {
//            jsonNode = jsonNode.get(ALL_OF).get(0);
//        }

            return jsonNode;

        } catch (Exception e) {
            //
            throw new RuntimeException(e.getMessage(), e);
        }

    }


    public static JsonSchema str2JsonSchema(String jsonSchema) {

        try {

            JsonSchema jsonNode = OBJECT_MAPPER.readValue(jsonSchema, ObjectSchema.class);

            return jsonNode;
        } catch (Exception e) {
            throw new RuntimeException("Could not str2JsonSchema for " + jsonSchema, e);
        }

    }

    /**
     * 将JSON Schema 转换为选项列表。
     *
     * @param jsonSchema   JSON Schema
     * @param code         编码
     * @param name         名称
     * @param description  描述
     * @param model        模型
     * @param isSplitArray 是否拆分数组
     * @return 选项列表
     */
    public static CreativeOptionDTO jsonSchemaToOptions(String jsonSchema, String code, String name, String description, String model, Boolean isSplitArray) {
        try {

            Assert.notBlank(jsonSchema, "JSON Schema must not be blank");
            Assert.notBlank(code, "Code must not be blank");
            Assert.notBlank(name, "Name must not be blank");
            Assert.notBlank(model, "Model must not be blank");

            // 获取下拉框类型的枚举值
            CreativeOptionModelEnum optionModel = CreativeOptionModelEnum.of(model);
            Assert.notNull(optionModel, "Model must be a valid enum value");

            JsonNode jsonNode = str2JsonNode(jsonSchema);

            if (jsonNode.has(ALL_OF)) {
                jsonNode = jsonNode.get(ALL_OF).get(0);
            }

            // 获取类型
            String type = getJsonSchemaFieldType(jsonNode);
            String filedCode = getCode(code, optionModel.getPrefix());

            // 构建根节点
            CreativeOptionDTO option = new CreativeOptionDTO();
            option.setParentCode(optionModel.getPrefix());
            option.setCode(filedCode);
            option.setName(name);
            option.setType(type);
            option.setModel(model);
            option.setDescription(StringUtils.isBlank(description) ? name : description);
            option.setChildren(getChildren(filedCode, jsonNode, model, isSplitArray));
            return option;
        } catch (Exception e) {
            throw new RuntimeException("Could not convert JSON Schema to options", e);
        }
    }

    /**
     * 获取 JSON Schema 的子节点。
     *
     * @param code         编码
     * @param jsonNode     JSON Schema 节点
     * @param model        模型
     * @param isSplitArray 是否拆分数组
     * @return 子节点列表
     */
    @NotNull
    private static List<CreativeOptionDTO> getChildren(String code, JsonNode jsonNode, String model, Boolean isSplitArray) {
        // 获取类型
        String type = getJsonSchemaFieldType(jsonNode);
        // 构建子节点列表
        List<CreativeOptionDTO> children;
        switch (type) {
            case OBJECT:
                JsonNode propertiesNode = jsonNode.get(PROPERTIES);
                children = objectNodeHandler(code, propertiesNode, model, isSplitArray);
                break;
            case ARRAY:
                JsonNode itemsNode = jsonNode.get(ITEMS);
                children = arrayNodeHandler(code, itemsNode, model, isSplitArray);
                break;
            case STRING:
            case NUMBER:
            case INTEGER:
            case BOOLEAN:
                children = Collections.emptyList();
                break;
            default:
                throw new IllegalArgumentException("Unsupported Json Schema data type: " + type);
        }
        return children;
    }

    /**
     * 处理 JSON Schema 的对象节点。
     *
     * @param parentCode   父编码
     * @param node         JSON Schema 的对象节点
     * @param model        模型
     * @param isSplitArray 是否拆分数组
     * @return 选项列表
     */
    private static List<CreativeOptionDTO> objectNodeHandler(String parentCode, JsonNode node, String model, Boolean isSplitArray) {

        List<CreativeOptionDTO> options = new ArrayList<>();

        // 如果父编码为空或者节点为空，则返回空列表
        if (StringUtils.isBlank(parentCode) || node == null) {
            return options;
        }
        // 获取节点的字段迭代器，如果没有字段，则返回空列表
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        if (fields == null || !fields.hasNext()) {
            return options;
        }

        // 遍历节点的字段，组装 JsonSchemaOption
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> fieldEntry = fields.next();
            String fieldCode = fieldEntry.getKey();
            JsonNode field = fieldEntry.getValue();
            String code = getCode(fieldCode, parentCode);
            CreativeOptionDTO option = new CreativeOptionDTO();
            option.setParentCode(parentCode);
            option.setCode(code);
            option.setName(getJsonSchemaFieldName(fieldCode, field));
            option.setDescription(getJsonSchemaFieldDescription(field));
            option.setType(getJsonSchemaFieldType(field));
            option.setModel(model);
            option.setChildren(getChildren(code, field, model, isSplitArray));
            options.add(option);
        }
        return options;
    }

    /**
     * 处理 JSON Schema 的数组节点。
     *
     * @param parentCode   父编码
     * @param node         JSON Schema 的数组节点
     * @param model        模型
     * @param isSplitArray 是否拆分数组
     * @return 选项列表
     */
    private static List<CreativeOptionDTO> arrayNodeHandler(String parentCode, JsonNode node, String model, Boolean isSplitArray) {

        List<CreativeOptionDTO> options = new ArrayList<>();

        // 如果父编码为空或者节点为空，则返回空列表
        if (StringUtils.isBlank(parentCode) || node == null) {
            return options;
        }

        // 如果不拆分数组，直接不需要处理，返回空子列表
        if (!isSplitArray) {
            return options;
        }

        // 如果节点是对象数组，则返回10个子节点

        return Collections.emptyList();
    }

    /**
     * 获取 JSON Schema 字段的类型。
     *
     * @param code       字段的编码
     * @param parentCode 父字段的编码
     * @return 字段的名称
     */
    private static String getCode(String code, String parentCode) {
        return /*parentCode + "." +*/ code;
    }

    /**
     * 获取 JSON Schema 字段的类型。
     *
     * @param field JSON Schema 字段
     * @return 字段的名称
     */
    private static String getJsonSchemaFieldType(JsonNode field) {
        // 获取 JSON Schema 的类型。如果没有类型，则默认为 object，因为 JSON Schema 的根节点类型默认为 object。
        // Java 中如果属性定义为 Object 类型，那么在 JSON Schema 中没有定义类型，也默认为 object 类型。
        if (!field.has(TYPE) || StringUtils.isBlank(field.get(TYPE).asText())) {
            return OBJECT;
        }

        // 获取类型
        return field.get(TYPE).asText();
    }

    /**
     * 获取 JSON Schema 字段的名称。
     *
     * @param fieldName 字段名
     * @param field     JSON Schema 字段
     * @return 字段的名称
     */
    private static String getJsonSchemaFieldName(String fieldName, JsonNode field) {
        // 优先使用 title，如果title不为空或者空字符串，则使用title
        if (field.has(TITLE) && field.get(TITLE) != null
                && StringUtils.isNoneBlank(field.get(TITLE).asText())) {
            return field.get(TITLE).asText();
        }
        // 如果title为空或者空字符串，则使用description，如果description不为空或者空字符串，则使用description
        if (field.has(DESCRIPTION) && field.get(DESCRIPTION) != null
                && StringUtils.isNoneBlank(field.get(DESCRIPTION).asText())) {
            return field.get(DESCRIPTION).asText();
        }
        // 如果title和description为空或者空字符串，则使用属性名
        return fieldName;
    }

    /**
     * 获取 JSON Schema 字段的描述。
     *
     * @param field JSON Schema 字段
     * @return 字段的描述
     */
    private static String getJsonSchemaFieldDescription(JsonNode field) {
        return field.has(DESCRIPTION) ?
                Optional.ofNullable(field.get(DESCRIPTION)).map(JsonNode::asText).orElse(StringUtils.EMPTY) : StringUtils.EMPTY;
    }

    public static void main(String[] args) {
        String string = generateJsonSchemaStr(BookListCreativeMaterialDTO.class);
        System.out.println(string);
        CreativeOptionDTO a = jsonSchemaToOptions(CreativeOptionDTO.class, "生成文本", CreativeOptionModelEnum.STEP_RESPONSE.name());
        System.out.println(a);
    }
}
