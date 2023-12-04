package com.starcloud.ops.business.app.dal.databoject.xhs.content;

import com.starcloud.ops.business.app.enums.xhs.content.XhsCreativeContentStatusEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class XhsCreativeContentDTO {

    /**
     * 创作方案UID
     */
    @Schema(description = "创作方案UID")
    private String schemeUid;

    @Schema(description = "创作计划uid")
    private String planUid;

    @Schema(description = "业务uid")
    private String businessUid;

    @Schema(description = "文案模板Uid")
    private String copyWritingUid;

    @Schema(description = "使用的图片模板Uid")
    private String pictureTempUid;

    @Schema(description = "使用的图片列表")
    private String usePicture;

    @Schema(description = "图片执行参数")
    private String pictureParams;

    @Schema(description = "文案执行参数")
    private String copyWritingParams;

    /**
     * {@link XhsCreativeContentStatusEnums}
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

    /**
     * 文案生成结果
     */
    @Schema(description = "文案生成结果")
    private String copyWritingResult;

    @Schema(description = "生成图片数量")
    private Integer pictureNum;

    @Schema(description = "生成图片结果")
    private String pictureContent;

    @Schema(description = "图片失败信息")
    private String pictureErrorMsg;

    @Schema(description = "文案失败信息")
    private String copyWritingErrorMsg;

    @Schema(description = "图片重试次数")
    private Integer pictureRetryCount;

    @Schema(description = "文案重试次数")
    private Integer copyWritingRetryCount;

    @Schema(description = "图片拓展信息")
    private String pictureExtend;

    @Schema(description = "文案拓展信息")
    private String copyWritingExtend;

    @Schema(description = "是否认领")
    private Boolean claim;

    /**
     * 喜欢数量
     */
    @Schema(description = "喜欢数量")
    private Long likeCount;
}
