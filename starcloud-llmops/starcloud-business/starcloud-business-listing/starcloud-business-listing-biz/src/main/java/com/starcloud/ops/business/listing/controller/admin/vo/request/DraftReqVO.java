package com.starcloud.ops.business.listing.controller.admin.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.listing.dto.DraftConfigDTO;
import com.starcloud.ops.business.listing.enums.SellerSpriteMarketEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "保存新版草稿")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DraftReqVO {

    @Schema(description = "草稿Uid")
    private String uid;

    @Schema(description = "版本")
    @Min(value = 1, message = "草稿版本必须大于零")
    private Integer version;

    @Schema(description = "站点")
    @NotBlank(message = "站点不能为空")
    @InEnum(value = SellerSpriteMarketEnum.class, message = "应用类型[{value}]必须是: {values}")
    private String endpoint;

    @Schema(description = "关键词")
    private List<String> keys;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "asin")
    private String asin;

    @Schema(description = "草稿配置")
    private DraftConfigDTO draftConfig;

    @Schema(description = "五点描述")
    private Map<String,String> fiveDesc;

    @Schema(description = "产品描述")
    private String productDesc;

    @Schema(description = "搜索词")
    private String searchTerm;


}
