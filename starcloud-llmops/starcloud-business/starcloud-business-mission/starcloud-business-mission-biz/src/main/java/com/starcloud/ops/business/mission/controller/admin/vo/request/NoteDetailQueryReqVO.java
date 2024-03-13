package com.starcloud.ops.business.mission.controller.admin.vo.request;

import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "查询小红书笔记内容")
public class NoteDetailQueryReqVO {

    @Schema(description = "笔记url")
//    @Pattern(regexp = XHS_URL_REGEX, message = "发布链接为浏览器访问地址，如： https://www.xiaohongshu.com/explore/24位数字和字母")
    private String noteUrl;

    @Schema(description = "素材类型")
    @NotBlank(message = "素材类型不能为空")
    @InEnum(value = MaterialTypeEnum.class, field = InEnum.EnumField.CODE, message = "素材类型({value}) 必须属于: {values}")
    private String materialType;
}
