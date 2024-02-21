package com.starcloud.ops.business.app.api.xhs.content.vo.request;

import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExtendDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "创建创作计划")
public class CreativeContentCreateReqVO {

    /**
     * 创作方案UID
     */
    @Schema(description = "创作方案UID")
    @NotBlank(message = "创作方案UID 不能为空")
    private String schemeUid;

    @Schema(description = "创作计划uid")
    @NotBlank(message = "创作计划uid 不能为空")
    private String planUid;

    /**
     * {@link CreativeContentTypeEnum}
     * XhsCreativeContentTypeEnums.code
     */
    @Schema(description = "任务类型")
    @NotBlank(message = "任务类型 不能为空")
    private String type;

    @Schema(description = "业务uid")
    @NotBlank(message = "业务uid 不能为空")
    private String businessUid;

    @Schema(description = "会话UID")
    @NotBlank(message = "会话UID 不能为空")
    private String conversationUid;

    @Schema(description = "使用的图片/文案模板Uid")
    private String tempUid;

    @Schema(description = "使用的图片列表")
    private List<String> usePicture;

    @Schema(description = "执行参数")
    private CreativePlanExecuteDTO executeParams;

    @Schema(description = "拓展信息")
    private CreativeContentExtendDTO extend;

    @Schema(description = "是否测试")
    private Boolean isTest;

    @Schema(description = "标签")
    private List<String> tags;

}
