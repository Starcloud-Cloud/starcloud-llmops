package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request;

import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.RiskReplaceRespVO;
import com.starcloud.ops.business.app.enums.plugin.ProcessMannerEnum;
import com.starcloud.ops.business.app.util.SensitiveWordUtil;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.StringJoiner;

@Data
@Schema(description = "敏感词替换")
public class RiskReplaceReqVO {

    @Schema(description = "文案内容")
    @NotBlank(message = "文案内容不能为空")
    private String content;

    @Schema(description = "禁用词汇总")
    private String topRiskStr;

    @Schema(description = "敏感词汇总")
    private String lowRiskStr;

    @Schema(description = "处理方式")
    @InEnum(value = ProcessMannerEnum.class, field = InEnum.EnumField.CODE, message = "处理方式[{value}]必须在: [{values}] 范围内！")
    private String processManner;


    public RiskReplaceRespVO riskReplace() {
        StringJoiner sj = new StringJoiner(",");
        if (StringUtils.isNotBlank(topRiskStr)) {
            sj.add(topRiskStr);
        }
        if (StringUtils.isNotBlank(lowRiskStr)) {
            sj.add(lowRiskStr);
        }
        if (sj.length() == 0) {
            return new RiskReplaceRespVO(content);
        }
        for (String riskword : sj.toString().split(",")) {
            content = SensitiveWordUtil.replace(content, riskword, processManner);
        }
        return new RiskReplaceRespVO(content);
    }
}
