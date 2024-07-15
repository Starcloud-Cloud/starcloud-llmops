package com.starcloud.ops.business.app.model.plan;

import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

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
@Schema(name = "CreativePlanConfigurationDTO", description = "创作计划配置信息！")
public class CreativePlanConfigurationDTO implements java.io.Serializable {

    private static final long serialVersionUID = 1211787128072072394L;

    /**
     * 创作方案
     */
    @Schema(description = "应用信息")
    @NotNull(message = "应用信息！")
    private AppMarketRespVO appInformation;

    /**
     * 资料列表
     */
    @Schema(description = "素材列表")
    private List<Map<String, Object>> materialList;

    /**
     * 图片风格列表
     */
    @Schema(description = "图片风格模板")
    private List<PosterStyleDTO> imageStyleList;

    /**
     * 校验配置信息
     */
    public void validate(ValidateTypeEnum validateType) {
        // 非配置校验的话，会校验 素材列表和海报列表
        if (!ValidateTypeEnum.CONFIG.equals(validateType)) {
            AppValidate.notEmpty(materialList, "创作计划素材列表不能为空！");
            AppValidate.notEmpty(imageStyleList, "创作计划海报风格列表不能为空！");
        }
        // 应用信息始终校验
        AppValidate.notNull(appInformation, "应用配置信息不能为空！");
        // 将 appInformation 转换为 AppMarketEntity 对象
        AppMarketEntity appMarketEntity = AppMarketConvert.INSTANCE.convertEntity(appInformation);
        // 进行校验应用
        appMarketEntity.validate(null, validateType);

        appInformation = AppMarketConvert.INSTANCE.convertResponse(appMarketEntity);
    }

}
