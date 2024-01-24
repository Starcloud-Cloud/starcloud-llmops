package com.starcloud.ops.business.app.api.xhs.scheme.dto.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "自定义创作方案配置")
public class CustomCreativeSchemeConfigDTO implements java.io.Serializable {

    private static final long serialVersionUID = -1278431625060498370L;

    /**
     * 创作应用UID
     */
    @Schema(description = "创作应用UID")
    private String appUid;

    /**
     * 应用名称
     */
    @Schema(description = "创作应用名称")
    private String appName;

    /**
     * 应用版本号
     */
    @Schema(description = "创作应用版本号")
    private Integer version;

    /**
     * 抽象的 创作方案 流程节点配置
     */
    private List<BaseSchemeStepDTO> steps;

    /**
     * 验证
     */
    public void validate() {
        if (StrUtil.isBlank(appUid)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_APP_UID_REQUIRED);
        }
        if (CollectionUtil.isEmpty(steps)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_CONFIGURATION_NOT_NULL);
        }
        steps.forEach(BaseSchemeStepDTO::validate);
    }
}
