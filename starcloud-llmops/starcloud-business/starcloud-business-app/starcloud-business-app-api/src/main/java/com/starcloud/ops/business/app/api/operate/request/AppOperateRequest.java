package com.starcloud.ops.business.app.api.operate.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 模版操作管理表DO，operate 表示操作类型，LIKE 标识喜欢，VIEW 标识查看，DOWNLOAD 标识下载
 *
 * @author admin
 * @since 2023-06-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "模版操作管理表请求类")
public class AppOperateRequest implements Serializable {

    private static final long serialVersionUID = -2376678376100185771L;

    /**
     * 模版 UID
     */
    @Schema(description = "模版 UID")
    @NotBlank(message = "模版 UID 不能为空")
    private String templateUid;

    /**
     * 版本号
     */
    @Schema(description = "模版版本号")
    @NotBlank(message = "模版版本号不能为空")
    private String version;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    @NotBlank(message = "操作类型不能为空")
    private String operate;

}
