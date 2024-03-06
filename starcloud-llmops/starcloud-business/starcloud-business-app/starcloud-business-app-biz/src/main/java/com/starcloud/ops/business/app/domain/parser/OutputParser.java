package com.starcloud.ops.business.app.domain.parser;

/**
 * 将(原始)LLM输出转换为类型为的结构化响应。
 * 其中 {@link FormatProvider#getFormat()} 方法应该提供所需转换的格式。
 * 其中 {@link Parser#parse(String)} 方法应该提供如何解析生成结果为提供的类型对象。
 *
 * @param <T> 指定所需的响应类型
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public interface OutputParser<T> extends Parser<T>, FormatProvider {


}
