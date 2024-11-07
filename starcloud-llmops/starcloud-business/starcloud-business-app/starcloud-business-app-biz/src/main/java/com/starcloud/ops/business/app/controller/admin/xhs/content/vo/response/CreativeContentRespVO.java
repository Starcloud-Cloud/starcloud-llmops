package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response;

import com.starcloud.ops.business.app.api.app.dto.AppExecuteProgress;
import com.starcloud.ops.business.app.model.content.CopyWritingContent;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteParam;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteResult;
import com.starcloud.ops.business.app.model.content.RedBookSignature;
import com.starcloud.ops.business.app.model.content.ImageContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创作内容响应")
public class CreativeContentRespVO implements java.io.Serializable {

    private static final long serialVersionUID = -8602264554633829827L;

    /**
     * 创作内容UID
     */
    @Schema(description = "创作内容UID")
    private String uid;

    /**
     * 执行批次UID
     */
    @Schema(description = "执行批次UID")
    private String batchUid;

    /**
     * 创作计划UID
     */
    @Schema(description = "创作计划UID")
    private String planUid;

    /**
     * 会话UID
     */
    @Schema(description = "会话UID")
    private String conversationUid;

    /**
     * 创作内容类型
     */
    @Schema(description = "创作内容类型")
    private String type;

    /**
     * 创作内容来源
     */
    @Schema(description = "创作内容来源")
    private String source;

    /**
     * 执行请求
     */
    @Schema(description = "执行请求")
    private CreativeContentExecuteParam executeParam;

    /**
     * 执行结果
     */
    @Schema(description = "执行响应")
    private CreativeContentExecuteResult executeResult;

//
//    /**
//     * 生成的内容
//     * @return
//     */
//    @Schema(description = "生成的内容")
//    public CopyWritingContent getContent() {
//        return this.getExecuteResult().getCopyWriting();
//    }
//
//
//    /**
//     * 生成的图片列表
//     * @return
//     */
//    @Schema(description = "图片")
//    public List<ImageContent> getImageList() {
//         return this.getExecuteResult().getImageList();
//    }



    /**
     * 执行开始时间
     */
    @Schema(description = "执行开始时间")
    private LocalDateTime startTime;

    /**
     * 执行结束时间
     */
    @Schema(description = "执行结束时间")
    private LocalDateTime endTime;

    /**
     * 执行耗时
     */
    @Schema(description = "执行耗时")
    private Long elapsed;

    /**
     * 执行状态
     */
    @Schema(description = "执行状态")
    private String status;

    /**
     * 重试次数
     */
    @Schema(description = "重试次数")
    private Integer retryCount;

    /**
     * 执行失败信息
     */
    @Schema(description = "执行失败信息")
    private String errorMessage;

    /**
     * 是否喜欢
     */
    @Schema(description = "是否喜欢")
    private Boolean liked;

    /**
     * 是否绑定
     */
    @Schema(description = "是否绑定")
    private Boolean claim;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String creator;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 执行进度
     */
    @Schema(description = "执行进度")
    private AppExecuteProgress progress;
}
