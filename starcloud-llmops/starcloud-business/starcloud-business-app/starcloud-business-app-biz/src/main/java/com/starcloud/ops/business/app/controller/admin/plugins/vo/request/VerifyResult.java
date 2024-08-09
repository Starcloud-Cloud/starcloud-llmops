package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "验证成功")
public class VerifyResult {

    @Schema(description = "验证成功进度")
    private String status;

    @Schema(description = "验证信息")
    private String msg;

    @Schema(description = "验证成功")
    private boolean verifyState;

    @Schema(description = "输入")
    private Map<String, Object> arguments;

    @Schema(description = "输出")
    private Object output;

    @Schema(description = "输出类型")
    private String outputType;

    /**
     * 对话创建的时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
     */
    private Integer createdAt;

    /**
     * 对话结束的时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
     */
    private Integer completedAt;

}
