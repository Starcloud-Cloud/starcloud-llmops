package com.starcloud.ops.business.app.util;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

/**
 * 获取国际化消息工具类 <br>
 * 从国际化配置文件中根据配置文件key值获取value
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2020-06-22
 */
@SuppressWarnings("unused")
@Component
public class MessageUtil implements MessageSourceAware, DisposableBean {

    /**
     * 国际化消息bean
     */
    private static MessageSource messageSource;

    /**
     * 如果未找到消息，则返回默认消息
     *
     * @param code           国际化消息配置文件中的code,
     *                       要查找的消息代码，例如 'calculator.noRateSet'。鼓励 MessageSource 用户将消息名称基于合格的类或包名称，避免潜在的冲突并确保最大的清晰度
     * @param defaultMessage 查找失败时返回的默认消息
     * @param args           将为消息中的参数填充的参数数组，如果没有则为 null
     *                       <pre>{@code A0001(code) = there is {0}, I am {1}}</pre>
     *                       会把{0}, {1} 替换为参数中数据
     * @param locale         进行查找的语言环境
     * @return 如果查找成功则解析消息，否则默认消息作为参数传递（可能为空）或者为国际化配置文件中的key值 {@link String}
     */
    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return messageSource.getMessage(code, args, defaultMessage, Objects.isNull(locale) ? LocaleContextHolder.getLocale() : locale);
    }

    /**
     * 如果未找到消息，则返回默认消息
     *
     * @param code           国际化消息配置文件中的code,
     *                       要查找的消息代码，例如 'calculator.noRateSet'。鼓励 MessageSource 用户将消息名称基于合格的类或包名称，避免潜在的冲突并确保最大的清晰度
     * @param defaultMessage 查找失败时返回的默认消息
     * @param args           将为消息中的参数填充的参数数组，如果没有则为 null
     *                       <pre>{@code A0001(code) = there is {0}, I am {1}}</pre>
     *                       会把{0}, {1} 替换为参数中数据
     * @return 如果查找成功则解析消息，否则默认消息作为参数传递（可能为空）或者为国际化配置文件中的key值 {@link String}
     */
    public static String getMessage(String code, Object[] args, String defaultMessage) {
        return messageSource.getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
    }

    /**
     * 尝试解决该消息。如果找不到消息，则视为错误
     *
     * @param code   国际化消息配置文件中的code,
     *               要查找的消息代码，例如 'calculator.noRateSet'。鼓励 MessageSource 用户将消息名称基于合格的类或包名称，避免潜在的冲突并确保最大的清晰度
     * @param args   将为消息中的参数填充的参数数组，如果没有则为 null
     *               <pre>{@code A0001(code) = there is {0}, I am {1}}</pre>
     *               会把{0}, {1} 替换为参数中数据
     * @param locale 进行查找的语言环境
     * @return the resolved message (never {@code null})
     * @throws NoSuchMessageException 如果没有找到相应的消息
     * @see java.text.MessageFormat
     */
    public static String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return messageSource.getMessage(code, args, Objects.isNull(locale) ? LocaleContextHolder.getLocale() : locale);
    }

    /**
     * 获取国际化消息
     *
     * @param code 错误码
     * @param args 参数
     * @return 国际化消息
     */
    public static String getMessage(String code, Object... args) {
        return getMessage(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * 尝试使用传入的 {@link MessageSourceResolvable} 参数中包含的所有属性来解析消息
     * <p>注意：我们必须在此方法上抛出 {@link NoSuchMessageException}，因为在调用此方法时，我们无法确定可解析的 {@code defaultMessage} 属性是 {@code null} 还是不是。
     *
     * @param resolvable 值对象存储解析消息所需的属性 （可能包括默认消息）
     * @param locale     进行查找的语言环境
     * @return 已解决的消息（从不 {@code null}，因为即使是 {@link MessageSourceResolvable} 提供的默认消息也需要为非空）
     * @throws NoSuchMessageException 如果没有找到相应的消息（并且 {@link MessageSourceResolvable} 没有提供默认消息）
     * @see MessageSourceResolvable#getCodes()
     * @see MessageSourceResolvable#getArguments()
     * @see MessageSourceResolvable#getDefaultMessage()
     * @see java.text.MessageFormat
     */
    public static String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return messageSource.getMessage(resolvable, locale);
    }

    /**
     * set方式注入国际化 {@link MessageSource}
     *
     * @param messageSource 国际化总接口 {@link MessageSource}
     */
    @Override
    public void setMessageSource(@NonNull MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    /**
     * 会在Bean销毁阶段调用
     */
    @Override
    public void destroy() {
        MessageUtil.messageSource = null;
    }
}
