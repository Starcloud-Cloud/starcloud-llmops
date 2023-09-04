package com.starcloud.ops.framework.common.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 返回结果
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-02
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class BaseStreamResult implements Serializable {

    private static final long serialVersionUID = -8232082960327730897L;

    /**
     * 状态吗
     */
    private Integer code;

    /**
     * 内容
     */
    private String content;

    /**
     * 获取 流结果
     *
     * @param code    code
     * @param content content
     * @return 结果
     */
    public static BaseStreamResult of(Integer code, String content) {
        BaseStreamResult result = new BaseStreamResult();
        result.setCode(code);
        result.setContent(content);
        return result;
    }
}