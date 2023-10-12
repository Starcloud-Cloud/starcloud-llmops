package com.starcloud.ops.business.log.api.message.vo.query;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(name = "AppLogMessagePageReqVO", description = "应用会话日志详情分页查询请求 VO")
public class AppLogMessagePageReqVO extends PageParam {

    private static final long serialVersionUID = 7128980457613201477L;

    /**
     * 应用 UID
     */
    @Schema(description = "应用 UID")
    @NotBlank(message = "应用 UID 不能为空")
    private String conversationUid;

    /**
     * 应用模型
     */
    @Schema(description = "应用模型")
    private String appMode;

    /**
     * 应用场景
     */
    @Schema(description = "应用场景")
    private String fromScene;

    /**
     * AI模型
     */
    @Schema(description = "AI模型")
    private String aiModel;

    /**
     * 应用状态
     */
    @Schema(description = "应用状态")
    private String status;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

}
