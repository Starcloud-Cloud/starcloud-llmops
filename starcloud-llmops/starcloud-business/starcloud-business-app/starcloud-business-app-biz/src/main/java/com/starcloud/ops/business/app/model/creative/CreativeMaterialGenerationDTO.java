package com.starcloud.ops.business.app.model.creative;

import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
    private List<Map<String, Object>> materialList;

    /**
     * 所有字段定义列表
     */
    @Schema(description = "素材库UID")
    private String libraryUid;

    /**
     * 选中的字段定义列表
     */
    @Schema(description = "选中的字段定义列表")
    private List<String> checkedFieldList;

    /**
     * 素材要求
     */
    @Schema(description = "素材要求")
    private String requirement;

    /**
     * 生成数量
     */
    @Schema(description = "生成数量")
    private Integer generateCount;

}
