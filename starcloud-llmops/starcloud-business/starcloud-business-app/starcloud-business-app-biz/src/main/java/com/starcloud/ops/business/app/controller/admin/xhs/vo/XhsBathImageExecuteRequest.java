package com.starcloud.ops.business.app.controller.admin.xhs.vo;

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
@Schema(name = "XhsBathImageExecuteRequest", description = "小红书批量图片生成请求")
public class XhsBathImageExecuteRequest implements java.io.Serializable {

    private static final long serialVersionUID = 5822526666346723864L;

    /**
     * 图片请求
     */
    @Valid
    @Schema(description = "图片请求")
    private List<XhsImageExecuteRequest> imageRequests;

}

