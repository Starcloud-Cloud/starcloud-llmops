package com.starcloud.ops.business.listing.controller.admin.vo.request;

import com.starcloud.ops.business.listing.enums.SellerSpriteMarketEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "修改词库")
public class DictModifyReqVO {

    @Schema(description = "词库uid")
    @NotBlank(message = "词库uid不能为空")
    private String uid;

    @Schema(description = "词库名称")
    private String name;


    @Schema(description = "状态")
    private Boolean enable;

    @Schema(description = "站点")
    @InEnum(value = SellerSpriteMarketEnum.class, message = "应用类型[{value}]必须是: {values}")
    private String endpoint;

}
