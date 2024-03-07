package com.starcloud.ops.business.app.domain.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;

import java.util.Map;
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
    private final String jsonSchema;

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
        this.jsonSchema = JsonSchemaUtils.generateJsonSchema(clazz);
    }

    /**
     * 将提供的文本解析为提供的类型对象。
     *
     * @param text 要解析的文本
     * @return 解析的对象
     */
    @Override
    public T parse(String text) {
        try {
            text = this.jsonSchemaToInstance(text);
            return this.objectMapper.readValue(text, this.clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return 返回一个字符串，其中包含有关如何格式化生成结果的提示词。
     */
    @Override
    public String getFormat() {
        String template = "Your response should be in JSON format.\n" +
                "Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.\n" +
                "Do not include markdown code blocks in your response.\n" +
                "Here is the JSON Schema instance your output must adhere to:\n" +
                "```\n %s \n```\n";
        return String.format(template, this.jsonSchema);
    }

    /**
     * 将JSON Schema 转换为基于给定文本的实例。
     *
     * @param text 要转换的文本
     * @return 从JSON Schema 生成的JSON实例，如果输入不是 JSON Schema ，则为原始文本。
     */
    @SuppressWarnings("unchecked")
    private String jsonSchemaToInstance(String text) {
        try {
            Map<String, Object> map = this.objectMapper.readValue(text, Map.class);
            if (map.containsKey("$schema")) {
                return this.objectMapper.writeValueAsString(map.get("properties"));
            }
        } catch (Exception e) {
            // Ignore
        }
        return text;
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
}
