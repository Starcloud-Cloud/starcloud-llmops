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
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.TEMPLATE_FILE_TABLE_HEAD_CELL;

/**
 * Excel 文件导入策略
 */
@Slf4j
@Component
public class ExcelMaterialImportStrategy implements MaterialImportStrategy {

    @Resource
    private MaterialLibrarySliceService materialLibrarySliceService;

    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    /**
     * @param importReqVO 导入素材数据 VO
     */
    @SneakyThrows
    @Override
    public void importMaterial(MaterialLibraryImportReqVO importReqVO) {

        MultipartFile excel = importReqVO.getFile()[0];
        List<String> newHeads = OperateImportUtil.readExcelHead(excel.getInputStream(), 1, TEMPLATE_FILE_TABLE_HEAD_CELL + 1);
        // 获取素材库表头信息
        List<MaterialLibraryTableColumnDO> materialConfigList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(importReqVO.getLibraryId());

        List<MaterialLibraryTableColumnSaveReqVO> saveReqVOS;
        if (CollUtil.isEmpty(materialConfigList)) {
            // 新增表头数据
            saveReqVOS = buildMaterialTableColumn(newHeads, importReqVO.getLibraryId());
        } else {
            // 获取初始表头数据
            List<String> heads = materialConfigList.stream().map(MaterialLibraryTableColumnDO::getColumnName).collect(Collectors.toList());
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

    }
}
