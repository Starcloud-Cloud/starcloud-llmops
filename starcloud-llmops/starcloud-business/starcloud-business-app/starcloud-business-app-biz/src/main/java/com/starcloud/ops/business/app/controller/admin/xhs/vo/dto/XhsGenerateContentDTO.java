package com.starcloud.ops.business.app.controller.admin.xhs.vo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "XhsGenerateContentDTO", description = "小红书内容生成结果")
public class XhsGenerateContentDTO implements java.io.Serializable {

    private static final long serialVersionUID = -3739624595360011704L;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;
    
}
