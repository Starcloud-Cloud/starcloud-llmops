package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response;

import com.starcloud.ops.business.app.model.content.CreativeContentExecuteResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@Schema(description = "创作内容资料配置响应VO")
public class CreativeContentShareResultRespVO implements Serializable {

    private static final long serialVersionUID = -5912604668141498L;

    /**
     * 创作内容uid
     */
    @Schema(description = "创作内容uid")
    private String uid;

    /**
     * 生成结果
     */
    @Schema(description = "生成结果")
    private CreativeContentExecuteResult executeResult;

}
