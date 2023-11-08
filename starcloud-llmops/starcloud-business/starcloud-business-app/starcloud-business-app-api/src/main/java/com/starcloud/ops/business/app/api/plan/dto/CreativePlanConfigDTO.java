package com.starcloud.ops.business.app.api.plan.dto;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.xhs.XhsImageStyleDTO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.plan.CreativeRandomTypeEnum;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativePlanConfigDTO", description = "创作计划配置信息！")
public class CreativePlanConfigDTO implements java.io.Serializable {

    private static final long serialVersionUID = 1211787128072072394L;

    /**
     * 上传图片地址列表
     */
    @Schema(description = "上传图片地址列表")
    @NotEmpty(message = "请上传您的图片素材！")
    private List<String> imageUrlList;

    /**
     * 应用UID列表
     */
    @Schema(description = "应用列表")
    @NotEmpty(message = "请选择文案列表！")
    private List<String> copyWritingList;

    /**
     * 应用变量列表
     */
    @Schema(description = "应用变量列表")
    private List<VariableItemDTO> variableList;

    /**
     * 图片风格列表
     */
    @Schema(description = "图片风格列表")
    @Valid
    @NotEmpty(message = "请选择图片风格！")
    private List<XhsImageStyleDTO> imageStyleList;

    /**
     * 执行随机方式
     */
    @Schema(description = "执行随机方式")
    @NotBlank(message = "执行随机方式不能为空！")
    @InEnum(value = CreativeRandomTypeEnum.class, field = InEnum.EnumField.NAME, message = "执行随机方式不支持({value})！")
    private String randomType;

    /**
     * 生成数量
     */
    @Schema(description = "生成数量")
    @NotNull(message = "生成数量不能为空！")
    @Min(value = 1, message = "生成数量最小值为 1")
    @Max(value = 500, message = "生成数量最大值为 500")
    private Integer total;

    /**
     * 校验配置信息
     */
    public void validate() {
        if (CollectionUtil.isEmpty(imageUrlList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_UPLOAD_IMAGE_EMPTY);
        }
        if (CollectionUtil.isEmpty(copyWritingList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_COPY_WRITING_EMPTY);
        }
        if (CollectionUtil.isEmpty(imageStyleList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_IMAGE_STYLE_EMPTY);
        }
        if (StringUtil.isBlank(randomType)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_RANDOM_TYPE_EMPTY);
        }
        if (!CreativeRandomTypeEnum.contains(randomType)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_RANDOM_TYPE_NOT_SUPPORT, randomType);
        }
        if (Objects.isNull(total)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_TOTAL_EMPTY);
        }
        if (total < 1 || total > 500) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_TOTAL_OUT_OF_RANGE, total);
        }
    }
}
