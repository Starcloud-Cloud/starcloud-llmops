package com.starcloud.ops.business.app.api.log.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExecuteResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-09
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "图片生成应用日志实体")
public class AppLogMessageRespVO extends LogMessageDetailRespVO {

    private static final long serialVersionUID = 7881937997878461879L;

    /**
     * 消息内容
     */
    @Schema(description = "消耗 token 数量")
    private Integer tokens;

    /**
     * 消息内容
     */
    @Schema(description = "价格")
    private BigDecimal price;

    /**
     * 消息内容
     */
    @Schema(description = "价格单位")
    private String currency;
    /**
     * 应用信息
     */
    @Schema(description = "应用信息")
    private AppRespVO appInfo;

    /**
     * 计划 uid
     */
    @Schema(description = "计划 uid")
    private String planUid;

    /**
     * 计划批次 uid
     */
    @Schema(description = "计划批次 uid")
    private String planBatchUid;

    /**
     * 内容 uid
     */
    @Schema(description = "内容 uid")
    private String contentUid;

    /**
     * 执行结果
     */
    @Schema(description = "执行结果")
    private CreativeContentExecuteResult executeResult;


    /**
     * 消息类型
     */
    private String msgType;

}
