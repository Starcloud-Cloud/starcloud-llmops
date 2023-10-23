package com.starcloud.ops.business.listing.controller.admin.vo.response;

import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.DepartmentsDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.GkDatasDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.MonopolyAsinDtosDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.SearchesTrendDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "关键词原数据明细")
public class KeywordMetadataBasicRespVO {

    private Long id;

    @Schema(description = "站点 ID")
    private Long marketId;

    /**
     * 关键词
     */
    @Schema(description = "关键词")
    private String keyword;

    /**
     * 月搜索量 注意 日平均前端计算
     */
    private Long searches;
    /**
     * 月购买量
     */
    private Long purchases;
    /**
     * 月购买量比列
     */
    private Double purchaseRate;

}
