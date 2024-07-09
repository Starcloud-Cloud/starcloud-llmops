package com.starcloud.ops.business.app.service.materiallibrary.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import com.starcloud.ops.business.app.util.MaterialLibrary.OperateImportUtil;
import com.starcloud.ops.business.app.util.MaterialLibrary.dto.ExcelDataImportConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.EXCEL_NOT_EXIST;
import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.TEMPLATE_FILE_NAME;
import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.TEMPLATE_FILE_TABLE_HEAD_CELL;

/**
 * 压缩包素材导入
 */
@Slf4j
@Component
public class ZipMaterialImportStrategy implements MaterialImportStrategy {

    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;
    @Resource
    private MaterialLibrarySliceService materialLibrarySliceService;

    /**
     * @param importReqVO 素材导入VO
     */
    @Override
    public void importMaterial(MaterialLibraryImportReqVO importReqVO) {
        // 压缩包只读取一个
        // -解压 并且返回压缩包内的数据
        File[] childrenDirs = OperateImportUtil.unpackReturnFile(importReqVO.getFile()[0]);

        if (childrenDirs == null) {
            throw exception(EXCEL_NOT_EXIST);
        }

        File excel = null;
        File unzipDir = null;

        // 获取模板文件
        for (File childrenDir : childrenDirs) {
            if (childrenDir != null && childrenDir.isDirectory()) { // 避免空指针异常
                File[] excelFiles = childrenDir.listFiles(ZipMaterialImportStrategy::isTemplateFile);
                if (excelFiles != null && excelFiles.length > 0) {
                    excel = excelFiles[0];
                    unzipDir = childrenDir;
                    break;
                }
            }
        }


        if (Objects.isNull(excel)) {
            throw new RuntimeException(StrUtil.format("压缩包中未找到名为:{} 的文件", TEMPLATE_FILE_NAME));
        }


        Set<String> newHeads = OperateImportUtil.readExcelHead(excel, 1, TEMPLATE_FILE_TABLE_HEAD_CELL + 1);
        // 获取素材库表头信息
        List<MaterialLibraryTableColumnDO> materialConfigList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(importReqVO.getLibraryId());

        List<MaterialLibraryTableColumnSaveReqVO> saveReqVOS;
        if (CollUtil.isEmpty(materialConfigList)) {
            // 新增表头数据
            saveReqVOS = buildMaterialTableColumn(newHeads, importReqVO.getLibraryId());
            materialLibraryTableColumnService.saveBatchData(saveReqVOS);
            List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(importReqVO.getLibraryId());
            saveReqVOS= BeanUtils.toBean(tableColumnDOList,MaterialLibraryTableColumnSaveReqVO.class);
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
        OperateImportUtil.readExcel(excel, childrenDirs, importConfigDTO, materialLibrarySliceService, 2);
    }


    private static boolean isTemplateFile(File file) {
        // 确保是文件而不是目录
        if (!FileUtil.isFile(file)) {
            return false;
        }

        // 检查文件名是否匹配
        return TEMPLATE_FILE_NAME.equalsIgnoreCase(FileNameUtil.getName(file));
    }

}
