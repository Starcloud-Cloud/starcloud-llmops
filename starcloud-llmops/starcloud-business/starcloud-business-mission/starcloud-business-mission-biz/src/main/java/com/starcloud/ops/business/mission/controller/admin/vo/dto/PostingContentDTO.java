package com.starcloud.ops.business.mission.controller.admin.vo.dto;

import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.business.app.model.content.ImageContent;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.CONTENT_INCONSISTENT;

@Data
@Schema(description = "发帖内容")
public class PostingContentDTO {

    @Schema(description = "发帖标题")
    private String title;

    @Schema(description = "发帖内容")
    private String text;

    @Schema(description = "发帖图片")
    private List<ImageContent> picture;

    public void validPostingContent(XhsNoteDetailRespVO noteDetail) {
        if (noteDetail != null
                && StringUtils.equals(this.title, noteDetail.getTitle())) {
            return;
        }

        if (Objects.equals("cn-test", SpringUtil.getActiveProfile())) {
            return;
        }
        throw exception(CONTENT_INCONSISTENT);
    }
}
