package com.starcloud.ops.business.app.domain.parser;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
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
        //兼容处理，针对多返回的内容
        try {
            log.info("生成结果格式化处理开始({}) 原始值: {}", this.getClass().getSimpleName(), text);
            text = StrUtil.replaceFirst(text, "```json", "", true);
            text = StrUtil.replaceLast(text, "```", "", true);
            // 解析 JSON
            JSONObject jsonObject = JSONUtil.parseObj(text);
            log.info("生成结果格式化处理结束({}) 处理之后的值: {}", this.getClass().getSimpleName(), jsonObject);
            return jsonObject;
        } catch (Exception e) {
            try {
                log.error("生成结果格式化处理异常({})：{}: {}", this.getClass().getSimpleName(), e.getClass(), e.getMessage());
                // 二次处理一下
                text = quoteJson(text);
                // 解析 JSON
                JSONObject result = JSONUtil.parseObj(text);
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
     * 处理 json 值转义问题，简单处理。后续可能有别的问题。
     *
     * @param str json 字符串
     * @return 处理后的字符串
     */
    private static String quoteJson(String str) {
        // 如果字符串为空，直接返回
        if (StrUtil.isBlank(str)) {
            return str;
        }

        // 按照 ":" 分割字符串
        String[] split = str.split("(?<=\")\\s*:\\s*(?=\")");
        // 如果分割后的数组长度小于 2，直接返回
        if (split.length < 2) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            // 如果是偶数数，直接拼接
            if (i % 2 == 0) {
                sb.append(split[i]);
                continue;
            }

            // 拼接 :
            sb.append(":");

            // 如果是偶数，拼接前面的字符串
            String value = StrUtil.trim(split[i]);
            // 如果是以 " 开头 和 以 " 结尾，拼接直接拼接
            if (value.startsWith("\"") && value.endsWith("\"")) {
                // 剩下的进行转义处理，先去掉前后的 "
                value = StrUtil.removePrefix(value, "\"");
                value = StrUtil.removeSuffix(value, "\"");

                // 转义处理
                value = StrUtil.replace(value, "\n", "\\n");
                // 加上前后的 "
                value = StrUtil.format("\"{}\"", value);
                // 拼接
                sb.append(value);
                continue;
            }
            if (value.startsWith("\"") && value.endsWith("}")) {
                // 剩下的进行转义处理，先去掉前后的 "
                value = StrUtil.removePrefix(value, "\"");
                // 截取到 \" 之前的字符串
                value = StrUtil.subBefore(value, "\"", true);
                // 转义处理
                value = StrUtil.replace(value, "\n", "\\n");
                // 加上前后的 "
                value = StrUtil.format("\"{} \"}", value);
                // 拼接
                sb.append(value);
                continue;
            }

            sb.append(value);

        }
        return sb.toString();
    }

}
