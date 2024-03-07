package com.starcloud.ops.business.app.domain.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

/**
 * 它使用 JSON Schema 将 LLM 输出结果 转换为特定的对象类型。
 * 该解析器的工作原理是基于给定的 Java 类生成 JSON Schema，
 * 然后用于验证 LLM 输出结果并将其转换为所需的 Java 对象类型。
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public class BeanOutputParser<T> implements OutputParser<T> {

    /**
     * 为目标类型生成的 JSON Schema 数据。
     */
    private String jsonSchema;

    /**
     * 目标类型的类对象。
     */
    private final Class<T> clazz;

    /**
     * Jackson JSON 处理器的配置。
     * 用于反序列化和其他 JSON 操作的对象映射器 。
     */
    private final ObjectMapper objectMapper;

    /**
     * 用目标类初始化的构造函数。
     *
     * @param clazz 目标类型的类对象
     */
    public BeanOutputParser(Class<T> clazz) {
        this(clazz, null);
    }

    /**
     * 用目标类型、自定义对象映射器的构造函数
     */
    public BeanOutputParser(Class<T> clazz, ObjectMapper objectMapper) {
        Objects.requireNonNull(clazz, "Java Class cannot be null;");
        this.clazz = clazz;
        this.objectMapper = objectMapper != null ? objectMapper : getObjectMapper();
        // 生成 JSON Schema
        generateSchema();
    }

    /**
     * 生成目标类的 JSON Schema。
     */
    private void generateSchema() {
//        try {
//            JacksonModule jacksonModule = new JacksonModule();
//            SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(DRAFT_2020_12, PLAIN_JSON)
//                    .with(jacksonModule);
//            SchemaGeneratorConfig config = configBuilder.build();
//            SchemaGenerator generator = new SchemaGenerator(config);
//            JsonNode jsonNode = generator.generateSchema(this.clazz);
//            ObjectWriter objectWriter = new ObjectMapper()
//                    .writer(new DefaultPrettyPrinter().withObjectIndenter(new DefaultIndenter().withLinefeed("\n")));
//            this.jsonSchema = objectWriter.writeValueAsString(jsonNode);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Could not pretty print json schema for " + this.clazz, e);
//        }
    }

    /**
     * 为 ObjectMapper 配置并返回 对象映射器。
     *
     * @return ObjectMapper.
     */
    protected ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * @return 返回一个字符串，其中包含有关如何格式化生成结果的提示词。
     */
    @Override
    public String getFormat() {
        return null;
    }

    /**
     * 将提供的文本解析为提供的类型对象。
     *
     * @param text 要解析的文本
     * @return 解析的对象
     */
    @Override
    public T parse(String text) {
        return null;
    }
}
