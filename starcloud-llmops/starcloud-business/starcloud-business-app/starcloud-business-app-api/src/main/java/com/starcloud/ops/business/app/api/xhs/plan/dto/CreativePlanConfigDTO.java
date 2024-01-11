package com.starcloud.ops.business.app.api.xhs.plan.dto;

import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeListOptionRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
@Schema(name = "CreativePlanConfigDTO", description = "创作计划配置信息！")
public class CreativePlanConfigDTO implements java.io.Serializable {

    private static final long serialVersionUID = 1211787128072072394L;

    /**
     * 上传图片地址列表
     */
    @Schema(description = "上传图片地址列表")
//    @NotEmpty(message = "请上传您的图片素材！")
    private List<String> imageUrlList;

    /**
     * 创作方案列表
     */
    @Schema(description = "方案列表")
    @NotEmpty(message = "请选择创作方案！")
    private List<CreativeSchemeListOptionRespVO> schemeList;

    /**
     * 校验配置信息
     */
    public void validate() {

    }
}
