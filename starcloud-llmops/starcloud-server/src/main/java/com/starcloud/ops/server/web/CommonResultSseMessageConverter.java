package com.starcloud.ops.server.web;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.framework.common.api.dto.BaseStreamResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-07-12
 */
@SuppressWarnings("all")
public class CommonResultSseMessageConverter implements HttpMessageConverter<CommonResult> {

    /**
     * Return the list of media types supported by this converter. The list may
     * not apply to every possible target element type and calls to this method
     * should typically be guarded via {@link #canWrite(Class, MediaType)
     * canWrite(clazz, null}. The list may also exclude MIME types supported
     * only for a specific class. Alternatively, use
     * {@link #getSupportedMediaTypes(Class)} for a more precise list.
     *
     * @return the list of supported media types
     */
    @NotNull
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.TEXT_EVENT_STREAM);
    }

    /**
     * Indicates whether the given class can be read by this converter.
     *
     * @param clazz     the class to test for readability
     * @param mediaType the media type to read (can be {@code null} if not specified);
     *                  typically the value of a {@code Content-Type} header.
     * @return {@code true} if readable; {@code false} otherwise
     */
    @Override
    public boolean canRead(@NotNull Class<?> clazz, MediaType mediaType) {
        return false;
    }

    /**
     * Read an object of the given type from the given input message, and returns it.
     *
     * @param clazz        the type of object to return. This type must have previously been passed to the
     *                     {@link #canRead canRead} method of this interface, which must have returned {@code true}.
     * @param inputMessage the HTTP input message to read from
     * @return the converted object
     * @throws HttpMessageNotReadableException in case of conversion errors
     */
    @Override
    public CommonResult read(@NotNull Class<? extends CommonResult> clazz, @NotNull HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        return null;
    }

    /**
     * Write the given object to the given output message.
     *
     * @param clazz     the class to test for writability
     * @param mediaType the media type to write (can be {@code null} if not specified);
     *                  typically the value of an {@code Accept} header.
     * @return true return if the object can be written.
     */
    @Override
    public boolean canWrite(@NotNull Class<?> clazz, MediaType mediaType) {
        return CommonResult.class.isAssignableFrom(clazz) && MediaType.TEXT_EVENT_STREAM.equals(mediaType);
    }

    /**
     * Write an object to the given output message.
     *
     * @param commonResult  the object to write to the output message. The type of this object must have previously been
     *                      passed to the {@link #canWrite canWrite} method of this interface, which must have returned {@code true}.
     * @param mediaType     the content type to use when writing. May be {@code null} to indicate that the
     *                      default content type of the converter must be used. If not {@code null}, this media type must have
     *                      previously been passed to the {@link #canWrite canWrite} method of this interface, which must have
     *                      returned {@code true}.
     * @param outputMessage the message to write to
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void write(@NotNull CommonResult commonResult, MediaType mediaType, @NotNull HttpOutputMessage outputMessage) throws IOException {
        // 构建 StreamResult 对象
        BaseStreamResult result = BaseStreamResult.of(Boolean.FALSE, commonResult.getCode(), commonResult.getMsg());
        // 结构和 SseEmitter 的 send() 方法一致
        StringBuilder sb = new StringBuilder("data:");
        sb.append(JSONUtil.toJsonStr(result));
        sb.append("\n");

        // 写入数据
        outputMessage.getBody().write(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

}
