package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.ContainerTypeSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.SimpleTypeSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.starcloud.ops.business.app.api.ocr.OcrGeneralDTO;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.domain.entity.workflow.JsonDataDefSchema;
import com.starcloud.ops.business.app.domain.entity.workflow.JsonDocsDefSchema;
import com.starcloud.ops.business.app.enums.materiallibrary.ColumnTypeEnum;
import com.starcloud.ops.business.app.model.content.CopyWritingContent;
import com.starcloud.ops.business.app.model.creative.CreativeOptionDTO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

            JsonSchema jsonSchema = generateJsonSchema(clazz);
            return generateJsonSchemaStr(jsonSchema);

        } catch (Exception e) {
            throw new RuntimeException("Could not pretty print json schema for " + clazz);
        }
    }

    /**
     * 根据给定的 Java 类生成对应的 JSON Schema。
     *
     * @return 生成的 JSON Schema
     */
    public static String generateJsonSchemaStr(JsonSchema jsonSchema) {
        try {

            DefaultIndenter defaultIndenter = new DefaultIndenter()
                    .withLinefeed("\n");
            DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter()
                    .withObjectIndenter(defaultIndenter);
            ObjectWriter objectWriter = new ObjectMapper()
                    .writer(defaultPrettyPrinter);
            objectWriter.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
            // 生成 JSON Schema
            return objectWriter.writeValueAsString(jsonSchema);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not pretty print json schema", e);

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
     * @return 生成的 JSON Schema
     */
    public static JsonSchema generateCopyWritingJsonSchema() {
        try {

            JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(OBJECT_MAPPER);
            JsonSchema jsonSchema = jsonSchemaGenerator.generateSchema(CopyWritingContent.class);
            ObjectSchema objectSchema = jsonSchema.asObjectSchema();
            Map<String, JsonSchema> properties = objectSchema.getProperties();
            for (Map.Entry<String, JsonSchema> entry : properties.entrySet()) {
                if ("title".equals(entry.getKey()) || "content".equals(entry.getKey())) {
                    StringSchema schema = entry.getValue().asStringSchema();
                    schema.setTitle(schema.getDescription());
                }
                if ("tagList".equals(entry.getKey())) {
                    ArraySchema schema = entry.getValue().asArraySchema();
                    schema.setTitle(schema.getDescription());
                }
            }
            return jsonSchema;

        } catch (Exception e) {
            throw new RuntimeException("Could not generateSchema for " + CopyWritingContent.class.getSimpleName(), e);
        }
    }

    /**
     * 根据给定的 Java 类生成对应的 JSON Schema。
     *
     * @return 生成的 JSON Schema
     */
    public static JsonSchema generateJsonDataDefSchema() {
        try {

            JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(OBJECT_MAPPER);
            JsonSchema jsonSchema = jsonSchemaGenerator.generateSchema(JsonDataDefSchema.class);
            ObjectSchema objectSchema = jsonSchema.asObjectSchema();
            Map<String, JsonSchema> properties = objectSchema.getProperties();
            for (Map.Entry<String, JsonSchema> entry : properties.entrySet()) {
                if ("data".equals(entry.getKey())) {
                    StringSchema schema = entry.getValue().asStringSchema();
                    schema.setTitle(schema.getDescription());
                }
            }
            return jsonSchema;

        } catch (Exception e) {
            throw new RuntimeException("Could not generateSchema for " + JsonDataDefSchema.class.getSimpleName(), e);
        }
    }

    /**
     * 根据给定的 Java 类生成对应的 JSON Schema。
     *
     * @return 生成的 JSON Schema
     */
    public static String generateCopyWritingJsonSchemaStr() {
        return generateJsonSchemaStr(generateCopyWritingJsonSchema());
    }

    /**
     * 根据给定的 Java 类生成对应的 JSON Schema。
     *
     * @return 生成的 JSON Schema
     */
    public static String generateJsonDataDefSchemaStr() {
        return generateJsonSchemaStr(generateJsonDataDefSchema());
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

            objectWriter.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
            // 生成 JSON Schema
            return objectWriter.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JsonNode Could not pretty print json schema: " + e.getMessage(), e);
        }
    }


    /**
     * 根据给定的 Java 类生成对应的 JSON Schema。
     *
     * @param jsonSchema 给定的 Java 类
     * @return 生成的 JSON Schema
     */
    public static String jsonSchema2Str(JsonSchema jsonSchema) {

        try {

            DefaultIndenter defaultIndenter = new DefaultIndenter()
                    .withLinefeed("\n");
            DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter()
                    .withObjectIndenter(defaultIndenter);
            ObjectWriter objectWriter = new ObjectMapper()
                    .writer(defaultPrettyPrinter);
            objectWriter.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
            // 生成 JSON Schema
            return objectWriter.writeValueAsString(jsonSchema);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JsonNode Could not pretty print json schema: " + e.getMessage(), e);
        }
    }


    public static JsonNode str2JsonNode(String jsonSchema) {

        try {

            JsonNode jsonNode = OBJECT_MAPPER.readTree(jsonSchema);
            // 检查是否为 JSON Schema，如果不是，则抛出异常
//            Assert.isTrue(
//                    ObjectUtil.isNotNull(jsonNode) &&
//                            jsonNode.has(JSON_SCHEMA) && StringUtils.isNotBlank(jsonNode.get(JSON_SCHEMA).asText()),
//                    "The given JSON is not a JSON Schema"
//            );

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

            JsonSchema jsonNode = OBJECT_MAPPER.readValue(jsonSchema, ContainerTypeSchema.class);

            return jsonNode;
        } catch (Exception e) {
            throw new RuntimeException("Could not str2JsonSchema for " + jsonSchema, e);
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

    /**
     * 获取 JSON Schema 增加字段拓展类型
     *
     * @param clazz
     * @return
     */
    public static JsonSchema expendGenerateJsonSchema(Class<?> clazz) {
        JsonSchema jsonSchema = generateJsonSchema(clazz);
        if (!(jsonSchema instanceof ObjectSchema)) {
            return jsonSchema;
        }

        Map<String, JsonSchema> properties = ((ObjectSchema) jsonSchema).getProperties();
        Map<String, Field> fieldMap = Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toMap(Field::getName, Function.identity()));

        for (String fieldName : properties.keySet()) {
            JsonSchema fieldSchema = properties.get(fieldName);
            Field field = fieldMap.get(fieldName);
            if (Objects.isNull(field)) {
                continue;
            }
            FieldDefine fieldDefine = field.getAnnotation(FieldDefine.class);
            if (Objects.isNull(fieldDefine)) {
                continue;
            }
            String description = fieldSchema.getDescription();
            fieldSchema.setRequired(fieldDefine.required());
            fieldSchema.setDescription(description + "-" + fieldDefine.type().getTypeCode());

//            ExpandStringSchema expandStringSchema = new ExpandStringSchema();
//            BeanUtil.copyProperties(fieldSchema, expandStringSchema);
//            expandStringSchema.setExpandType(fieldDefine.type().getTypeCode());
//            properties.put(fieldName, expandStringSchema);
        }
        return jsonSchema;
    }

    /**
     * 素材自定义配置生成 jsonschema
     * 暂时只使用第一个素材库的标题
     *
     * @param uid
     * @return
     */
    public static JsonSchema expendGenerateJsonSchema(String uid) {
        ObjectSchema obj = new ObjectSchema();


        MaterialLibraryService materialLibraryService = SpringUtil.getBean(MaterialLibraryService.class);

        MaterialLibraryAppReqVO appReqVO = new MaterialLibraryAppReqVO();
        appReqVO.setAppUid(uid);
        MaterialLibraryRespVO libraryRespVO = materialLibraryService.getMaterialLibraryByApp(appReqVO);
        if (Objects.isNull(libraryRespVO)) {
            return obj;
        }
        List<MaterialLibraryTableColumnRespVO> tableMeta = libraryRespVO.getTableMeta();
        if (CollectionUtil.isEmpty(tableMeta)) {
            return obj;
        }

        Map<String, JsonSchema> properties = new LinkedHashMap<>(tableMeta.size());
        for (MaterialLibraryTableColumnRespVO columnRespVO : tableMeta) {
            StringSchema schema = new StringSchema();
            schema.setTitle(columnRespVO.getColumnName());
            schema.setDescription("" + "-" + columnRespVO.getColumnType());
            properties.put(columnRespVO.getColumnCode(), schema);
            if (ColumnTypeEnum.IMAGE.getCode().equals(columnRespVO.getColumnType())) {
                ObjectSchema ocrSchema = (ObjectSchema) generateJsonSchema(OcrGeneralDTO.class);
                Map<String, JsonSchema> ocrSchemaProperties = ocrSchema.getProperties();
                for (String key : ocrSchemaProperties.keySet()) {
                    JsonSchema jsonSchema = ocrSchemaProperties.get(key);
                    if (jsonSchema instanceof SimpleTypeSchema) {
                        SimpleTypeSchema simpleTypeSchema = (SimpleTypeSchema) jsonSchema;
                        simpleTypeSchema.setTitle(simpleTypeSchema.getDescription());
                    }
                }
                ocrSchemaProperties.remove("url");
                ocrSchemaProperties.remove("data");
                ocrSchema.setDescription(columnRespVO.getColumnName() + "_ext");
                properties.put(columnRespVO.getColumnCode() + "_ext", ocrSchema);
            }
        }
        obj.setProperties(properties);
        return obj;
    }

    public JsonSchema getOutVariableJsonSchema(String uid) {
        //构造一层 array schema
        ObjectSchema docSchema = (ObjectSchema) JsonSchemaUtils.generateJsonSchema(JsonDocsDefSchema.class);
        docSchema.setTitle("上传素材");
        docSchema.setDescription("上传素材步骤。应用中可以存在一个此步骤。");
        ArraySchema arraySchema = (ArraySchema) docSchema.getProperties().get("docs");
        // 素材自定义配置
        ObjectSchema materialSchema = (ObjectSchema) JsonSchemaUtils.expendGenerateJsonSchema(uid);
        arraySchema.setItemsSchema(materialSchema);
        return docSchema;
    }

}
