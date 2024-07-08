package com.starcloud.ops.business.app.service.materiallibrary.handle;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import com.starcloud.ops.business.app.util.MaterialLibrary.OperateImportUtil;
import com.starcloud.ops.business.app.util.MaterialLibrary.dto.ExcelDataImportConfigDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_EXPORT_FAIL_EXCEL_NO_SUPPRT;
import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.TEMPLATE_FILE_TABLE_HEAD_CELL;

/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.app.service.materiallibrary.handle
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2024/06/27  11:18
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2024/06/27   AlanCusack    1.0         1.0 Version
 */
@Slf4j
@Component
public class ExcelMaterialImportStrategy implements MaterialImportStrategy{


    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;
    @Resource
    private MaterialLibrarySliceService materialLibrarySliceService;
    /**
     * @param importReqVO
     */
    @SneakyThrows
    @Override
    public void importMaterial(MaterialLibraryImportReqVO importReqVO) {

        MultipartFile excel = importReqVO.getFile()[0];
        Set<String> newHeads = OperateImportUtil.readExcelHead(excel.getInputStream(), 1, TEMPLATE_FILE_TABLE_HEAD_CELL + 1);
        // 获取素材库表头信息
        List<MaterialLibraryTableColumnDO> materialConfigList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(importReqVO.getLibraryId());

        List<MaterialLibraryTableColumnSaveReqVO> saveReqVOS;
        if (CollUtil.isEmpty(materialConfigList)) {
            // 新增表头数据
            saveReqVOS = addMaterialTableColumn(newHeads, importReqVO.getLibraryId());
        } else {
            // 获取初始表头数据
            Set<String> heads = materialConfigList.stream().map(MaterialLibraryTableColumnDO::getColumnName).collect(Collectors.toSet());
            // 表头验证
            validateMaterialTableColumn(heads, newHeads);
            saveReqVOS = BeanUtils.toBean(materialConfigList, MaterialLibraryTableColumnSaveReqVO.class);
        }
        log.info("表头读取完成，验证成功 开始异步存储数据");

        ExcelDataImportConfigDTO importConfigDTO = new ExcelDataImportConfigDTO();
        importConfigDTO.setUserId(getLoginUserId());
        importConfigDTO.setLibraryId(importReqVO.getLibraryId());


        importConfigDTO.setColumnConfig(BeanUtils.toBean(saveReqVOS, MaterialLibraryTableColumnRespVO.class));

        // 异步存储数据
        OperateImportUtil.readExcel(excel.getInputStream(), null, importConfigDTO, materialLibrarySliceService, 2);


        throw exception(MATERIAL_LIBRARY_EXPORT_FAIL_EXCEL_NO_SUPPRT);
    }
}
