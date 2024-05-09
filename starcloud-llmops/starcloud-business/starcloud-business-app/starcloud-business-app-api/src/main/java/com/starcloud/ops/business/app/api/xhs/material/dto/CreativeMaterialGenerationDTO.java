package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@Schema(description = "创意素材生成DTO")
public class CreativeMaterialGenerationDTO implements Serializable {

    private static final long serialVersionUID = 6005771042251763584L;

    /**
     * 素材列表
     */
    @Schema(description = "素材列表")
    private List<AbstractCreativeMaterialDTO> materialList;

    /**
     * 所有字段定义列表
     */
    @Schema(description = "所有字段定义列表")
    private List<MaterialFieldConfigDTO> fieldList;

    /**
     * 选中的字段定义列表
     */
    @Schema(description = "选中的字段定义列表")
    private List<MaterialFieldConfigDTO> selectedFieldList;

    /**
     * 素材要求
     */
    @Schema(description = "素材要求")
    private String materialRequirement;

}
