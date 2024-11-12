package cn.iocoder.yudao.module.system.controller.admin.socail.vo.user;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 社交用户更新昵称 Request VO")
@Data
@ToString(callSuper = true)
public class SocialUserUpdateSimpleReqVO  {

    @Schema(description = "主键(自增策略)", requiredMode = Schema.RequiredMode.REQUIRED, example = "14569")
    @NotNull(message = "用户编号不能为空")
    private Long id;

    @Schema(description = "社交昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "示例昵称")
    @Size(max = 30, message = "社交昵称长度不能超过30个字符")
    private String nickname;
}
