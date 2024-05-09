package com.starcloud.ops.business.app.domain.parser;

/**
 * 此接口的实现：提供了如何解析生成结果为提供的类型对象。
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@FunctionalInterface
public interface Parser<T> {

    /**
     * 将提供的文本解析为提供的类型对象。
     *
     * @param text 要解析的文本
     * @return 解析的对象
     */
    T parse(String text);
}
