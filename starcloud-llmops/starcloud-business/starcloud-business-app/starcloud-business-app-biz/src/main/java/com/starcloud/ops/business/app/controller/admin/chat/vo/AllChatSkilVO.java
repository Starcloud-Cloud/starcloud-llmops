package com.starcloud.ops.business.app.controller.admin.chat.vo;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "所有技能")
public class AllChatSkilVO {

    @Schema(description = "系统技能")
    List<ChatSkillVO> systemSkill;

    @Schema(description = "我的应用")
    List<AppRespVO> appRespList;

    @Schema(description = "应用市场")
    List<AppMarketRespVO> marketRespList;

}
