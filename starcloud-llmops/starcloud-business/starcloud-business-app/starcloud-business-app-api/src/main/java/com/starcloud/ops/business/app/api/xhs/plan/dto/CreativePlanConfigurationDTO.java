package com.starcloud.ops.business.app.api.xhs.plan.dto;

import cn.hutool.core.lang.Assert;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
     * 上传图片地址列表
     */
    @Schema(description = "上传图片地址列表")
    private List<String> imageUrlList;

    /**
     * 变量列表
     */
    @Schema(description = "变量列表")
    private List<VariableItemRespVO> variableList;

    /**
     * 校验配置信息
     */
    public void validate() {
        Assert.notBlank(this.schemeUid, "创作方案不能为空！");
    }

}
