package com.starcloud.ops.business.app.api.xhs.plan.dto;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.utils.MaterialDefineUtil;
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
    public void validate() {
        AppValidate.notNull(appInformation, "应用信息不能为空！");
        // 校验海报
        if (CollectionUtil.isNotEmpty(imageStyleList)) {
            imageStyleList.forEach(PosterStyleDTO::validate);
        }
    }

}
