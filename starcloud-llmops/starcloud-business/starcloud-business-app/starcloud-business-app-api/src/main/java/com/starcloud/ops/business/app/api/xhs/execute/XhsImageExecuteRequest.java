package com.starcloud.ops.business.app.api.xhs.execute;

import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageTemplateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "XhsImageRequest", description = "小红书图片请求")
public class XhsImageExecuteRequest implements java.io.Serializable {

    private static final long serialVersionUID = 465619511287771606L;

    /**
     * 图片模板
     */
    @Schema(description = "图片模板")
    @NotBlank(message = "图片模板不能为空")
    private String id;

    /**
     * 图片模板名称
     */
    @Schema(description = "图片模板名称")
    private String name;

    /**
     * 应用UID
     */
    @Schema(description = "图片序号")
    private Integer index;

    /**
     * 是否是主图
     */
    @Schema(description = "是否是主图")
    private Boolean isMain;

    /**
     * 图片生成参数
     */
    @Schema(description = "图片生成参数")
    private Map<String, Object> params;

}
