package com.starcloud.ops.business.app.api.xhs.plan.dto;

import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

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
    @Schema(description = "创作方案")
    @NotBlank(message = "创作方案不能为空！")
    private String schemeUid;

    /**
     * 资料列表
     */
    @Schema(description = "素材列表")
    private List<AbstractBaseCreativeMaterialDTO> creativeMaterialList;

    /**
     * 变量列表
     */
    @Schema(description = "变量列表")
    private List<VariableItemRespVO> variableList;

    /**
     * 校验配置信息
     */
    public void validate() {
        AppValidate.notBlank(this.schemeUid, "缺少必填项：创作方案为选择！");
        // 校验素材
        if (CollectionUtils.isEmpty(creativeMaterialList)) {
            creativeMaterialList.forEach(AbstractBaseCreativeMaterialDTO::valid);
        }
    }

}
