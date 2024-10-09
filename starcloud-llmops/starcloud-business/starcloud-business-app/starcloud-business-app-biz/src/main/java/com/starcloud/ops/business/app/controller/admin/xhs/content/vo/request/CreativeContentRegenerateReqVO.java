package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request;

import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author nacoyer
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创建创作计划请求")
public class CreativeContentRegenerateReqVO implements java.io.Serializable {

    private static final long serialVersionUID = -5141167918720038523L;

    /**
     * 创作内容UID
     */
    @Schema(description = "创作内容UID")
    @NotBlank(message = "创作内容UID不能为空！")
    private String uid;

    /**
     * 执行参数
     */
    @Schema(description = "执行参数")
    @NotNull(message = "执行参数不能为空！")
    private CreativeContentExecuteParam executeParam;

    @Schema(description = "来源 APP/MARKET")
    private String source;

    @Schema(description = "执行计划uid")
    private String planUid;

    /**
     * 基础校验
     */
    public void validate(ValidateTypeEnum validateType) {
        AppValidate.notBlank(uid, "创作内容UID不能为空！");
        AppValidate.notBlank(source, "来源不能为空！");
        AppValidate.notNull(executeParam, "执行参数不能为空！");
        executeParam.validate(validateType);
    }
}
