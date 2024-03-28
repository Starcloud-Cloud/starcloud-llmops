package com.starcloud.ops.business.app.domain.parser;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 它使用 JSON Schema 将 LLM 输出结果 转换为特定的对象类型。
 * 该解析器的工作原理是基于给定的 Java 类生成 JSON Schema，
 * 然后用于验证 LLM 输出结果并将其转换为所需的 Java 对象类型。
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Data
public class JsonSchemaParser implements OutputParser<JSONObject> {

    /**
     * 为目标类型生成的 JSON Schema 数据。
     */
    private final JsonSchema jsonSchema;

    public JsonSchemaParser(JsonSchema jsonSchema) {
        this.jsonSchema = jsonSchema;
    }


    /**
     * 将提供的文本解析为提供的类型对象。
     *
     * @param text 要解析的文本
     * @return 解析的对象
     */
    @Override
    public JSONObject parse(String text) {

        log.info("JsonSchemaParser start: {}", text);
        //兼容处理，针对多返回的内容
        text = StrUtil.replaceFirst(text, "```json", "", true);
        text = StrUtil.replaceLast(text, "```", "", true);

        JSONObject jsonObject = JSONUtil.parseObj(text);

        log.info("JsonSchemaParser parse: {}", jsonObject);

        return jsonObject;
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
        return String.format(template, JsonSchemaUtils.jsonSchema2Str(this.getJsonSchema()));
    }

}
