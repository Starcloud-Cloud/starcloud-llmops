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
import lombok.experimental.UtilityClass;

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
     * @return 是否符合
     */
    public static Boolean validate(String jsonSchema) {


        return null;
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
            SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)
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

}
