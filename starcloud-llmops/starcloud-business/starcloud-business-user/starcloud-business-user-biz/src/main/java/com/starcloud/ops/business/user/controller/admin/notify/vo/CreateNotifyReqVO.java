package com.starcloud.ops.business.user.controller.admin.notify.vo;

import cn.iocoder.yudao.module.system.controller.admin.notify.vo.template.NotifyTemplateSendReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "发送通知")
public class CreateNotifyReqVO extends NotifyTemplateSendReqVO {

    private String batchCode;

}
