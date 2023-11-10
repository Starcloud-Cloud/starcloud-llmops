package com.starcloud.ops.business.app.api.operate.request;

import com.starcloud.ops.business.app.enums.operate.AppOperateTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 应用操作管理表DO，operate 表示操作类型，LIKE 标识喜欢，VIEW 标识查看，DOWNLOAD 标识下载
 *
 * @author admin
 * @since 2023-06-12
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "应用操作管理表请求类")
public class AppOperateReqVO implements Serializable {

    private static final long serialVersionUID = -2376678376100185771L;

    /**
     * 应用 UID
     */
    @Schema(description = "应用 UID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "应用 UID 不能为空")
    private String appUid;

    /**
     * 版本号
     */
    @Schema(description = "应用版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer version;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "操作类型不能为空")
    @InEnum(value = AppOperateTypeEnum.class, message = "操作类型[{value}]必须在: [LIKE, VIEW] 范围内！")
    private String operate;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;


    private Long tenantId;

}
