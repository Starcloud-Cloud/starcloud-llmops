package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request;

import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.model.content.CopyWritingContent;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

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
     * 执行响应
     */
    @Schema(description = "执行响应")
    private CreativeContentExecuteResult executeResult;

    /**
     * 校验
     */
    public void validate() {
        AppValidate.notBlank(uid, "创作内容UID不能为空！");
        AppValidate.notNull(executeResult, "创作内容不能为空！");
        CopyWritingContent copyWriting = executeResult.getCopyWriting();
        AppValidate.notNull(copyWriting, "文案内容不能为空！");
        AppValidate.notBlank(copyWriting.getTitle(), "文案标题不能为空！");
        AppValidate.notBlank(copyWriting.getContent(), "文案内容不能为空！");

    }

}
