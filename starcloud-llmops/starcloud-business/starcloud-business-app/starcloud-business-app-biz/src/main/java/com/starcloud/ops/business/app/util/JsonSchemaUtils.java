package com.starcloud.ops.business.app.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import lombok.experimental.UtilityClass;

import java.util.Set;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@UtilityClass
public class JsonSchemaUtils {


    /**
     * 验证给定的 JSON 是否符合给定的 JSON Schema。
     *
     * @param jsonSchema JSON Schema
     */
    public static void validate(String jsonSchema) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema schema = factory.getSchema(jsonSchema);
        JsonNode schemaNode = schema.getSchemaNode();
        Set<ValidationMessage> validateSet = schema.validate(schemaNode);
        if (!validateSet.isEmpty()) {
            // 如果不符合 JSON Schema，则抛出异常
            // 处理校验结果
            StringBuilder sb = new StringBuilder();
            for (ValidationMessage validationMessage : validateSet) {
                sb.append(validationMessage.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("JSON Schema validation failed: \n" + sb);
        }
    }

    /**
     * 根据给定的 Java 类生成对应的 JSON Schema。
     *
     * @param clazz 给定的 Java 类
     * @return 生成的 JSON Schema
     */
    public static String generateJsonSchema(Class<?> clazz) {
        try {
            JacksonModule jacksonModule = new JacksonModule();
            SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_7, OptionPreset.PLAIN_JSON)
                    .with(jacksonModule);
            SchemaGeneratorConfig config = configBuilder.build();
            SchemaGenerator generator = new SchemaGenerator(config);
            JsonNode jsonNode = generator.generateSchema(clazz);
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

    public static void main(String[] args) {
        String jsonSchema = generateJsonSchema(CreativeContentDO.class);
        System.out.println(jsonSchema);
        validate(jsonSchema);
    }

}
