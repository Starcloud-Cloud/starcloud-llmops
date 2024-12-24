package com.starcloud.ops.business.app.controller.admin.prompt.vo.req;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询团队提示词")
public class PromptPageReqVO extends PageParam {

}
