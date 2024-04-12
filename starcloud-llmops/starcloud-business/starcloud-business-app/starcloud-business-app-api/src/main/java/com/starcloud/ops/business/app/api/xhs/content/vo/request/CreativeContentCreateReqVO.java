package com.starcloud.ops.business.app.api.xhs.content.vo.request;

import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExecuteParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创建创作计划请求")
public class CreativeContentCreateReqVO implements java.io.Serializable {

    private static final long serialVersionUID = 7802210018358116217L;

    /**
     * 执行批次UID
     */
    @Schema(description = "执行批次")
    @NotBlank(message = "执行批次UID不能为空")
    private String batchUid;

    /**
     * 创作计划UID
     */
    @Schema(description = "创作计划UID")
    @NotBlank(message = "创作计划UID不能为空")
    private String planUid;

    /**
     * 会话UID
     */
    @Schema(description = "会话UID")
    @NotBlank(message = "会话UID 不能为空")
    private String conversationUid;

    /**
     * 创作内容类型
     */
    @Schema(description = "创作内容类型")
    @NotBlank(message = "创作内容类型不能为空")
    private String type;

    /**
     * 执行参数
     */
    @Schema(description = "执行参数")
    private CreativeContentExecuteParam executeParam;


}
