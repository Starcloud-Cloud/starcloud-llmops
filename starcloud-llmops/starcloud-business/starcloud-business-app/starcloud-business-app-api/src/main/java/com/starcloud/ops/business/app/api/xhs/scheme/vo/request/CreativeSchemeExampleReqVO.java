package com.starcloud.ops.business.app.api.xhs.scheme.vo.request;

import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class CreativeSchemeExampleReqVO implements Serializable {

    private static final long serialVersionUID = 2795742509475840779L;

    /**
     * 创作方案UID
     */
    @Schema(description = "创作方案UID")
    @NotBlank(message = "创作方案UID不能为空！")
    private String schemeUid;

    /**
     * 素材列表
     */
    @Schema(description = "素材列表")
    private List<AbstractBaseCreativeMaterialDTO> creativeMaterialList;


}
