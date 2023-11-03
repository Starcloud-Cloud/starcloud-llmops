package com.starcloud.ops.business.app.api.xhs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@Schema(name = "XhsExecuteResponse", description = "小红书z执行结果")
public class XhsExecuteResponse implements java.io.Serializable {

    private static final long serialVersionUID = 5822526666346723864L;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 应用执行响应数据
     */
    @Schema(description = "应用执行响应数据")
    private XhsAppExecuteResponse app;

    /**
     * 图片响应数据
     */
    @Schema(description = "图片生成参数")
    private List<XhsImageExecuteResponse> images;

}

