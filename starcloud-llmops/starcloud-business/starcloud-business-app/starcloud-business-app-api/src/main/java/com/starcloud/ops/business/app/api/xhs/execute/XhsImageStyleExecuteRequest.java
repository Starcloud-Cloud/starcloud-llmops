package com.starcloud.ops.business.app.api.xhs.execute;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "XhsImageStyleExecuteRequest", description = "小红书风格图片执行请求")
public class XhsImageStyleExecuteRequest implements java.io.Serializable {

    private static final long serialVersionUID = 5822526666346723864L;

    /**
     * 风格ID
     */
    @Schema(description = "风格ID")
    private String id;

    /**
     * 风格名称
     */
    @Schema(description = "风格名称")
    private String name;

    /**
     * 图片请求
     */
    @Valid
    @Schema(description = "图片请求")
    private List<XhsImageExecuteRequest> imageRequests;

}

