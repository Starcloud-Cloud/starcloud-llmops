package com.starcloud.ops.business.app.api.chat.config.dto;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "聊天菜单配置")
public class ChatMenuConfigDTO implements BaseExpandConfigDTO {

    @Schema(description = "关键词")
    private String key;

    @Schema(description = "回复内容")
    private String value;

    @Schema(description = "隐藏按钮")
    private Boolean isButton;

    @Schema(description = "图片")
    private List<String> picture;

    @Override
    public void valid() {
        if (StringUtil.isBlank(key)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(50000001, "聊天菜单配置关键词不能为空"));
        }

        if (StringUtil.isBlank(value)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(50000001, "聊天菜单配置回复内容不能为空"));
        }
    }
}
