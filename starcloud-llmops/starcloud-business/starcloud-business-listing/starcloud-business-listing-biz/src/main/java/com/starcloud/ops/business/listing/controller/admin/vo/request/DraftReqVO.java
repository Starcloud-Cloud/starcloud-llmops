package com.starcloud.ops.business.listing.controller.admin.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.listing.dto.DraftConfigDTO;
import com.starcloud.ops.business.listing.enums.DraftTypeEnum;
import com.starcloud.ops.business.listing.enums.SellerSpriteMarketEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @InEnum(value = SellerSpriteMarketEnum.class, message = "站点[{value}]必须是: {values}")
    private String endpoint;

    @Schema(description = "类型")
    @NotNull(message = "类型不能为空")
    @InEnum(value = DraftTypeEnum.class, field = InEnum.EnumField.CODE, message = "类型[{value}]必须是: {values}")
    private Integer type;

    @Schema(description = "增加关键词")
    private List<String> keys;

    @Schema(description = "标题")
    @Length(max = 500, message = "标题长度不能大于 500 位")
    private String title;

    @Schema(description = "asin")
    @Length(max = 20, message = "asin长度不能大于 20 位")
    private String asin;

    @Schema(description = "草稿配置")
    private DraftConfigDTO draftConfig;

    @Schema(description = "五点描述")
    private Map<String, String> fiveDesc;

    @Schema(description = "产品描述")
    private String productDesc;

    @Schema(description = "搜索词")
    private String searchTerm;

    @Schema(description = "草稿名称")
    private String draftName;
}
