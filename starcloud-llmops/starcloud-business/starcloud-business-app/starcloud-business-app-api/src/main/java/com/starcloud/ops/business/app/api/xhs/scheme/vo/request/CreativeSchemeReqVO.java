package com.starcloud.ops.business.app.api.xhs.scheme.vo.request;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CreativeSchemeConfigurationDTO;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 创作方案DO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class CreativeSchemeReqVO implements java.io.Serializable {

    private static final long serialVersionUID = 33863755137653429L;

    /**
     * 创作方案名称
     */
    @NotBlank(message = "创作方案名称不能为空")
    @Schema(description = "创作方案名称")
    private String name;

    /**
     * 创作方案类型
     */
    @Schema(description = "创作方案类型")
    private String type;

    /**
     * 创作方案类目
     */
    @NotBlank(message = "创作方案类目不能为空")
    @Schema(description = "创作方案类目")
    private String category;

    /**
     * 创作方案标签
     */
    @Schema(description = "创作方案标签")
    private List<String> tags;

    /**
     * 创作方案描述
     */
    @Schema(description = "创作方案描述")
    private String description;

    /**
     * 创作方案配置信息
     */
    @Schema(description = "创作方案配置信息")
    private CreativeSchemeConfigurationDTO configuration;

    /**
     * 创作方案图片
     */
    @Schema(description = "创作方案图片")
    private List<String> useImages;

    /**
     * 物料
     */
    @Schema(description = "创作方案物料")
    private String materiel;

    /**
     * 校验创作方案
     */
    public void validate() {
        if (StrUtil.isBlank(name)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_NAME_REQUIRED);
        }
        if (StrUtil.isBlank(category)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_CATEGORY_REQUIRED, name);
        }
        configuration.validate();
    }
}
