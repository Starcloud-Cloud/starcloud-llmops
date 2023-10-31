package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 流量词拓展结果 DTO
 */
@NoArgsConstructor
@Data
public class PrepareReposeDTO {

    /**
     * 站点
     */
    @Schema(description = "站点")
    private String market;

    /**
     * 畅销变体的 ASIN
     */
    @Schema(description = "畅销变体的 ASIN")
    private List<String> diamondList;

    /**
     * 使用 当前变体  拓词，获得流量词数：
     */
    @Schema(description = "当前变体流量词数")
    private Integer results;
    /**
     * 使用 畅销变体  拓词，获得流量词数：
     */
    @Schema(description = "畅销变体流量词数")
    private Integer diamondResults;
    /**
     * 使用 全部变体  拓词，获得流量词数
     */
    @Schema(description = "全部变体流量词数")
    private Integer variationResults;


}
