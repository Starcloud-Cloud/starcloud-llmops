package com.starcloud.ops.business.mission.controller.admin.vo.dto;

import com.starcloud.ops.business.enums.AccountTypeEnum;
import com.starcloud.ops.business.enums.AddressEnum;
import com.starcloud.ops.business.enums.GenderEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Collections;
import java.util.List;

@Data
@Schema(description = "领取限制")
public class ClaimLimitDTO {

    @Schema(description = "区域")
    @InEnum(value = AddressEnum.class, field = InEnum.EnumField.CODE, message = "区域[{value}]必须是: {values}")
    private List<String> address;

    @Schema(description = "性别")
    @InEnum(value = GenderEnum.class, field = InEnum.EnumField.CODE, message = "性别[{value}]必须是: {values}")
    private String gender;

//    @Schema(description = "帐号类型")
//    @InEnum(value = AccountTypeEnum.class, field = InEnum.EnumField.CODE, message = "帐号类型[{value}]必须是: {values}")
//    private String accountType;

//    @Schema(description = "最大粉丝数")
//    @Min(value = 0, message = "最大粉丝数要大于0")
//    private Integer maxFansNum;

    @Schema(description = "每人领取数量")
    @Min(value = 0, message = "每人领取数量要大于0")
    private Integer claimNum;


    public static ClaimLimitDTO defaultInstance() {
        ClaimLimitDTO claimLimitDTO = new ClaimLimitDTO();
        claimLimitDTO.setAddress(Collections.singletonList(AddressEnum.unlimited.getCode()));
        claimLimitDTO.setGender(GenderEnum.unlimited.getCode());
//        claimLimitDTO.setAccountType(AccountTypeEnum.unlimited.getCode());
        claimLimitDTO.setClaimNum(1);
//        claimLimitDTO.setMaxFansNum(Integer.MAX_VALUE);
        return claimLimitDTO;
    }
}
