package com.starcloud.ops.business.app.controller.admin.xhs.vo.request;

import com.starcloud.ops.business.app.api.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.dto.XhsCreativeContentExtendDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "创建创作计划")
public class XhsCreativeContentCreateReq {

    @Schema(description = "创作计划uid")
    @NotBlank(message = "创作计划uid 不能为空")
    private String planUid;

    /**
     * {@link com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentTypeEnums}
     * XhsCreativeContentTypeEnums.code
     */
    @Schema(description = "任务类型")
    @NotBlank(message = "任务类型 不能为空")
    private String type;

    @Schema(description = "业务uid")
    @NotBlank(message = "业务uid 不能为空")
    private String businessUid;

    @Schema(description = "使用的图片/文案模板Uid")
    private String tempUid;

    @Schema(description = "使用的图片列表")
    private List<String> usePicture;

    @Schema(description = "执行参数")
    private CreativePlanExecuteDTO executeParams;

    @Schema(description = "拓展信息")
    private XhsCreativeContentExtendDTO extend;

}
