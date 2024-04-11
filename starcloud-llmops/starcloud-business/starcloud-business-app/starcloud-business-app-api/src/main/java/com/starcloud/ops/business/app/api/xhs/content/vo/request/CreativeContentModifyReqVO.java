package com.starcloud.ops.business.app.api.xhs.content.vo.request;

import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExecuteParam;
import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExecuteResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@Schema(defaultValue = "修改创作内容")
public class CreativeContentModifyReqVO implements java.io.Serializable {


    private static final long serialVersionUID = 9096954102590391143L;

    /**
     * 创作内容UID
     */
    @Schema(description = "创作内容UID")
    @NotBlank(message = "创作内容不能为空")
    private String uid;

    /**
     * 标签
     */
    @Schema(description = "标签")
    private List<String> tags;

    /**
     * 执行参数
     */
    @Schema(description = "执行参数")
    private CreativeContentExecuteParam executeParam;

    /**
     * 执行响应
     */
    @Schema(description = "执行响应")
    private CreativeContentExecuteResult executeResult;

}
