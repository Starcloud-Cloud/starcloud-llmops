package com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.verification.VerificationUtils;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "创作计划修改请求")
public class CreativePlanModifyReqVO implements Serializable {

    private static final long serialVersionUID = 7513433575049699291L;

    /**
     * 创作计划UID
     */
    @NotBlank(message = "创作计划更新失败！创作计划UID是必须的！！")
    // @Schema(description = "创作计划UID")
    private String uid;

    /**
     * 创作计划来源
     */
    @Schema(description = "创作计划来源")
    // @NotBlank(message = "创作计划来源不能为空！")
    private String source;

    /**
     * 创作计划详细配置信息
     */
    @Schema(description = "创作计划应用配置信息")
    // @Valid
    // @NotNull(message = "创作计划配置信息不能为空！")
    private CreativePlanConfigurationDTO configuration;

    /**
     * 生成数量
     */
    @Schema(description = "生成数量")
    // @NotNull(message = "创作计划生成数量不能为空！")
    // @Min(value = 1, message = "创作计划生成数量最小值为 1")
    // @Max(value = 100, message = "创作计划生成数量最大值为 100")
    private Integer totalCount;

    /**
     * 校验类型
     */
    @Schema(description = "校验类型")
    // @InEnum(value = ValidateTypeEnum.class, message = "校验类型不支持！")
    private String validateType;

    private Integer e;

    /**
     * 更新校验
     */
    public List<Verification> validate() {
        List<Verification> verifications = new ArrayList<>();
        VerificationUtils.notBlankCreative(verifications, uid, uid, "创作计划更新失败！创作计划UID是必须的！");
        VerificationUtils.notBlankCreative(verifications, validateType, uid, "创作计划更新失败！是否需要校验不能为空！");
        if (!IEnumable.contains(validateType, ValidateTypeEnum.class)) {
            VerificationUtils.addVerificationCreative(verifications, uid, "创作计划更新失败！校验类型不支持！");
        }

        VerificationUtils.notNullCreative(verifications, configuration, uid, "创作计划更新失败！创作计划配置信息不能为空！");
        VerificationUtils.notBlankCreative(verifications, source, uid, "创作计划更新失败！创作计划来源不能为空！");

        if (ValidateTypeEnum.EXECUTE.name().equals(validateType)) {
            VerificationUtils.notNullCreative(verifications, totalCount, uid, "创作计划更新失败！创作计划生成数量不能为空！");
        }

        ValidateTypeEnum validateTypeEnum = ValidateTypeEnum.valueOf(validateType);

        boolean isValidateApp = validateApp == null || validateApp;
        List<Verification> validationList = configuration.validate(uid, validateTypeEnum, isValidateApp);
        verifications.addAll(validationList);
        return verifications;
    }

    /**
     * 是否需要校验，默认需要校验
     */
    @Schema(description = "是否需要校验")
    private Boolean validate;

    /**
     * 是否需要校验，默认需要校验
     */
    @Schema(description = "是否需要校验")
    private Boolean validateApp;
}
