package cn.iocoder.yudao.module.mp.controller.admin.message.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "管理后台 - 模板消息发送 Request VO")
public class MpTemplateSendReqVO {

    @Schema(description = "公众号粉丝的编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "公众号粉丝的编号不能为空")
    private Long mpUserId;

    @Schema(description = "模板消息id")
    private String templateId;

    @Schema(description = "模板消息")
    private List<WxMpTemplateData> data;
}
