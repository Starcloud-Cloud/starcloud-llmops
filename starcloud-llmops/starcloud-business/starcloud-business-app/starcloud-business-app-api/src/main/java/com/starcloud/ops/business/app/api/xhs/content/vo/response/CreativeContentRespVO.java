package com.starcloud.ops.business.app.api.xhs.content.vo.response;

import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExtendDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageDTO;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "创作内容")
public class CreativeContentRespVO {

    /**
     * 创作方案UID
     */
    @Schema(description = "创作方案UID")
    private String schemeUid;

    @Schema(description = "创作计划uid")
    private String planUid;

    @Schema(description = "业务uid")
    private String businessUid;

    /**
     * 会话UID
     */
    @Schema(description = "会话UID")
    private String conversationUid;

    @Schema(description = "文案模板Uid")
    private String copyWritingUid;

    @Schema(description = "使用的图片模板Uid")
    private String pictureTempUid;

    @Schema(description = "使用的图片列表")
    private List<String> usePicture;

    @Schema(description = "图片执行参数")
    private CreativePlanExecuteDTO pictureParams;

    @Schema(description = "文案执行参数")
    private CreativePlanExecuteDTO copyWritingParams;

    /**
     * {@link CreativeContentStatusEnum}
     */
    @Schema(description = "图片状态")
    private String pictureStatus;

    @Schema(description = "文案状态")
    private String copyWritingStatus;

    @Schema(description = "图片开始时间")
    private LocalDateTime pictureStartTime;

    @Schema(description = "图片结束时间")
    private LocalDateTime pictureEndTime;

    @Schema(description = "图片执行耗时")
    private Long pictureExecuteTime;


    @Schema(description = "文案开始时间")
    private LocalDateTime copyWritingStartTime;

    @Schema(description = "文案结束时间")
    private LocalDateTime copyWritingEndTime;

    @Schema(description = "文案执行耗时")
    private Long copyWritingExecuteTime;

    @Schema(description = "文案标题")
    private String copyWritingTitle;

    @Schema(description = "文案内容")
    private String copyWritingContent;

    @Schema(description = "文案文字数量")
    private String copyWritingCount;

    @Schema(description = "生成图片数量")
    private Integer pictureNum;

    @Schema(description = "生成图片结果")
    private List<CreativeImageDTO> pictureContent;

    @Schema(description = "图片失败信息")
    private String pictureErrorMsg;

    @Schema(description = "文案失败信息")
    private String copyWritingErrorMsg;

    @Schema(description = "图片重试次数")
    private Integer pictureRetryCount;

    @Schema(description = "文案重试次数")
    private Integer copyWritingRetryCount;

    @Schema(description = "图片拓展信息")
    private CreativeContentExtendDTO pictureExtend;

    @Schema(description = "文案拓展信息")
    private CreativeContentExtendDTO copyWritingExtend;

    @Schema(description = "是否绑定")
    private Boolean claim;

    /**
     * 喜欢数量
     */
    @Schema(description = "喜欢数量")
    private Boolean liked;

    /**
     * 是否是测试数据
     */
    @Schema(description = "是否是测试数据")
    private Boolean isTest;

    private Integer totalStep;

    private Integer successStepCount;
}
