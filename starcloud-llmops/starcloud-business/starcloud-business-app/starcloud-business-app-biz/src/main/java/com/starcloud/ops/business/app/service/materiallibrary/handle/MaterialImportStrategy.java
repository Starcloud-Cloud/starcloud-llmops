package com.starcloud.ops.business.app.service.materiallibrary.handle;

import cn.hutool.core.collection.CollUtil;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.enums.materiallibrary.ColumnTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 素材导入策略
 */
public interface MaterialImportStrategy {

    void importMaterial(MaterialLibraryImportReqVO importReqVO);


    /**
     * @param materialTableColumn 素材库存在的表头
     * @param importTableColumn   导入数据的表头
     */
    default void validateMaterialTableColumn(Set<String> materialTableColumn, Set<String> importTableColumn) {
        if (CollUtil.isEmpty(materialTableColumn)) {
            return;
        }
        if (!CollUtil.isEqualList(materialTableColumn, importTableColumn)) {
            throw new RuntimeException("表头与当前表结构不一致,列名称及顺序需保持一致\n" +
                    "\n" +
                    "表头需要与现有表格结构保持一致。");
            // throw exception(EXCEL_HEADER_REQUIRED_FILED, subtract);
        }
    }

    /**
     * @param importTableColumn 导入数据的表头
     */
    default List<MaterialLibraryTableColumnSaveReqVO> buildMaterialTableColumn(Set<String> importTableColumn, Long libraryId) {
        int sequence = 1;


        List<MaterialLibraryTableColumnSaveReqVO> tableColumnDOS = new ArrayList<>();

        for (String headName : importTableColumn) {

            MaterialLibraryTableColumnSaveReqVO saveReqVO = new MaterialLibraryTableColumnSaveReqVO();

            saveReqVO.setLibraryId(libraryId);
            saveReqVO.setColumnName(headName);
            saveReqVO.setColumnCode(null);
            saveReqVO.setColumnType(ColumnTypeEnum.STRING.getCode());
            saveReqVO.setDescription(null);
            saveReqVO.setIsRequired(false);
            saveReqVO.setSequence((long) sequence++);
            tableColumnDOS.add(saveReqVO);
        }
        return tableColumnDOS;
    }
}
