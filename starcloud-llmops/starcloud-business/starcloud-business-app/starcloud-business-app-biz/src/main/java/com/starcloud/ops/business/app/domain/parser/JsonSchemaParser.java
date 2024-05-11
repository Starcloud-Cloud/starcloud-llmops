package com.starcloud.ops.business.app.domain.parser;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

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
public class JsonSchemaParser implements OutputParser<JSON> {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 决定解析器是否允许JSON字符串包含未加引号的控制字符(值小于32的ASCII字符，包括制表符和换行字符)。如果feature设置为false，如果遇到这样的字符将抛出异常。
        objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModules(new JavaTimeModule());
    }

    /**
     * 为目标类型生成的 JSON Schema 数据。
     */
    private final JsonSchema jsonSchema;

    public JsonSchemaParser(JsonSchema jsonSchema) {
        this.jsonSchema = jsonSchema;
    }


    /**
     * 将提供的文本解析为提供的类型对象。
     * <p>
     * 因为 jsonSchema 可能使对象或者数组。所以统一返回 JsonNode
     *
     * @param text 要解析的文本
     * @return 解析的对象
     */
    @Override
    public JSON parse(String text) {
        //兼容处理，针对多返回的内容
        try {
            log.info("生成结果格式化处理开始({}) 原始值: {}", this.getClass().getSimpleName(), text);
            text = StrUtil.replaceFirst(text, "```json", "", true);
            text = StrUtil.replaceLast(text, "```", "", true);
            // 先进行正常的 JSON 格式化处理
            JSON json = JSONUtil.parse(text);
            log.info("生成结果格式化处理结束({}) 处理之后的值: {}", this.getClass().getSimpleName(), json);
            return json;
        } catch (Exception e) {
            try {
                log.error("生成结果格式化处理异常({})：{}: {}", this.getClass().getSimpleName(), e.getClass(), e.getMessage());
                text = StrUtil.replace(text, "\r", "");
                text = StrUtil.replace(text, "\\\n", "\n");
                // 使用较为宽松的方式解析 JSON 字符串
                JSON result = parseJSON(text);
                log.info("生成结果二次格式化处理结束({}) 处理之后的值: {}", this.getClass().getSimpleName(), result);
                return result;
            } catch (Exception exception) {
                log.error("二次处理 生成结果格式化处理异常({})：{}: {}", this.getClass().getSimpleName(), text, e.getMessage(), e);
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_JSON_RESULT_PARSE_ERROR);
            }
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
        return String.format(template, JsonSchemaUtils.jsonSchema2Str(this.getJsonSchema()));
    }

    /**
     * 解析 JSON 字符串为 JSONObject 对象
     *
     * @param text JSON 字符串
     * @return JSONObject 对象
     */
    @SuppressWarnings("all")
    public static JSON parseJSON(String text) {
        AppValidate.notBlank(text, "AI生成结果不存在！请稍候重试");
        try {
            // 利用 Jackson 解析 JSON 字符串：objectMapper
            // 需要配置 JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS，JSON字符串包含未加引号的控制字符(值小于32的ASCII字符，包括制表符和换行字符)
            JSON json = objectMapper.readValue(text, JSON.class);
            // 再进行一次 JSON 格式化，保证 JSON 格式正确
            String strValue = JSONUtil.toJsonStr(json);
            return JSONUtil.parse(strValue);
        } catch (IOException e) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_JSON_RESULT_PARSE_ERROR);
        }
    }

}
