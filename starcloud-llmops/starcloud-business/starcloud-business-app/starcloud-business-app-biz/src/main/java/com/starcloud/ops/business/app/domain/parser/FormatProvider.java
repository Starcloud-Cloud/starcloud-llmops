package com.starcloud.ops.business.app.domain.parser;

/**
 * 此接口的实现：提供了如何格式化生成结果的提示词
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public interface FormatProvider {

    /**
     * @return 返回一个字符串，其中包含有关如何格式化生成结果的提示词。
     */
    String getFormat();

}