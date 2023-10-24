package com.starcloud.ops.business.listing.controller.admin.vo.request;

import com.starcloud.ops.business.listing.enums.SellerSpriteMarketEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "新建词库")
@Data
public class DictCreateReqVO {

    @Schema(description = "词库名称")
    @NotBlank(message = "词库名称不能为空")
    private String name;

    @Schema(description = "站点")
    @NotBlank(message = "站点不能为空")
    @InEnum(value = SellerSpriteMarketEnum.class, message = "应用类型[{value}]必须是: {values}")
    private String endpoint;

    @Schema(description = "关键词")
    private List<String> keys;

}
