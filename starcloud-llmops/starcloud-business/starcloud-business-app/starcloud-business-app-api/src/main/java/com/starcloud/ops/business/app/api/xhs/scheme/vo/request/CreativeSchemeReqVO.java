package com.starcloud.ops.business.app.api.xhs.scheme.vo.request;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeConfigDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeExampleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeReferenceDTO;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeModeEnum;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

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
     * 创作方案模式
     */
    @Schema(description = "创作方案模式")
    @NotBlank(message = "创作方案模式不能为空")
    private String mode;

    /**
     * 创作方案参考
     */
    @Valid
    @NotEmpty(message = "创作方案参考内容不能为空！")
    @Schema(description = "创作方案参考内容")
    private List<CreativeSchemeReferenceDTO> refers;

    /**
     * 创作方案配置信息
     */
    @Valid
    @NotNull(message = "创作方案配置信息不能为空！")
    @Schema(description = "创作方案配置信息")
    private CreativeSchemeConfigDTO configuration;

    /**
     * 创作方案图片
     */
    @Schema(description = "创作方案图片")
    private List<String> useImages;

    /**
     * 创作方案示例
     */
    @Schema(description = "创作方案示例")
    private List<CreativeSchemeExampleDTO> example;

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
        if (StrUtil.isBlank(mode)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_MODE_REQUIRED, name);
        }
        if (!IEnumable.contains(mode, CreativeSchemeModeEnum.class)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_MODE_NOT_SUPPORTED, mode, name);
        }
        if ((CollectionUtil.isEmpty(refers))) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_REFERS_NOT_EMPTY, name);
        }
        if (Objects.isNull(configuration)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_CONFIGURATION_NOT_NULL, name);
        }
        configuration.validate(name);
    }

}
