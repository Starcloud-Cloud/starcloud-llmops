package com.starcloud.ops.business.app.service.materiallibrary.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.enums.materiallibrary.ColumnTypeEnum;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import com.starcloud.ops.business.app.util.MaterialLibrary.OperateImportUtil;
import com.starcloud.ops.business.app.util.MaterialLibrary.dto.ExcelDataImportConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.EXCEL_NOT_EXIST;
import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.*;

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

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * @param importReqVO 素材导入VO
     */
    @Override
    public void importMaterial(MaterialLibraryImportReqVO importReqVO) {
        // 压缩包只读取一个
        // -解压 并且返回压缩包内的数据

        Map<Integer, List<String>> columnData = Collections.emptyMap();
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
            throw exception(EXCEL_NOT_EXIST);
        }


        List<String> newHeads = OperateImportUtil.readExcelHead(excel, 1, TEMPLATE_FILE_TABLE_HEAD_CELL + 1);
        // 获取素材库表头信息
        List<MaterialLibraryTableColumnDO> materialConfigList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(importReqVO.getLibraryId());

        List<MaterialLibraryTableColumnSaveReqVO> saveReqVOS;
        if (CollUtil.isEmpty(materialConfigList)) {
            // 新增表头数据
            saveReqVOS = buildMaterialTableColumn(newHeads, importReqVO.getLibraryId());
            materialLibraryTableColumnService.saveBatchData(saveReqVOS);
            List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(importReqVO.getLibraryId());
            saveReqVOS = BeanUtils.toBean(tableColumnDOList, MaterialLibraryTableColumnSaveReqVO.class);
        } else {
            // 获取初始表头数据
            // 排序返回数据 如果顺序相等 则按照 ID 排序 正序
            List<String> heads = materialConfigList.stream()
                    .sorted(Comparator.comparing(MaterialLibraryTableColumnDO::getSequence).thenComparing(MaterialLibraryTableColumnDO::getId))
                    .map(MaterialLibraryTableColumnDO::getColumnName)
                    .collect(Collectors.toList());
            // 表头验证
            validateMaterialTableColumn(heads, newHeads);
            saveReqVOS = BeanUtils.toBean(materialConfigList, MaterialLibraryTableColumnSaveReqVO.class);


            // 获取图像和文件的表头位置
            Map<Integer, List<Long>> imageOrDocumentColumn = getImageOrDocumentColumn(materialConfigList);

            if (!imageOrDocumentColumn.isEmpty()) {
                columnData = OperateImportUtil.readOtherExcel(excel, 2, imageOrDocumentColumn);
                if (!columnData.isEmpty()) {
                    materialLibrarySliceService.executeAsyncUpload(columnData, childrenDirs, unzipDir.getAbsolutePath(), importReqVO.getLibraryId());
                }

            }


        }
        log.info("表头读取完成，验证成功 开始异步存储数据");

        ExcelDataImportConfigDTO importConfigDTO = new ExcelDataImportConfigDTO();
        importConfigDTO.setUserId(getLoginUserId());
        importConfigDTO.setLibraryId(importReqVO.getLibraryId());

        importConfigDTO.setColumnConfig(BeanUtils.toBean(saveReqVOS, MaterialLibraryTableColumnRespVO.class));

        // 解析Excel数据
        List<MaterialLibrarySliceSaveReqVO> saveReqVOList = OperateImportUtil.readExcel(excel, importConfigDTO, materialLibrarySliceService, 2);
        materialLibrarySliceService.batchSaveDataDesc(saveReqVOList);

    }


    private static Map<Integer, List<Long>> getImageOrDocumentColumn(List<MaterialLibraryTableColumnDO> materialConfigList) {
        return materialConfigList.stream()
                .filter(column -> Objects.equals(column.getColumnType(), ColumnTypeEnum.IMAGE.getCode())
                        || Objects.equals(column.getColumnType(), ColumnTypeEnum.DOCUMENT.getCode()))
                .sorted(Comparator.comparing(MaterialLibraryTableColumnDO::getSequence)
                        .thenComparing(MaterialLibraryTableColumnDO::getId))
                .collect(Collectors.groupingBy(
                        MaterialLibraryTableColumnDO::getColumnType, // 分组依据
                        Collectors.mapping(MaterialLibraryTableColumnDO::getSequence, Collectors.toList())));


    }


    private static boolean isTemplateFile(File file) {
        // 确保是文件而不是目录
        if (!FileUtil.isFile(file)) {
            return false;
        }

        // 检查文件名是否匹配
        return TEMPLATE_FILE_NAME.equalsIgnoreCase(FileNameUtil.getName(file));
    }


    private String getRedisKey(String data, Long libraryId) {
        return String.format(MATERIAL_IMAGE_REDIS_PREFIX, libraryId, data);
    }

    private List<String> buildRedisKey(List<String> keys, Long libraryId) {
        return CollUtil.distinct(keys).stream()
                .map(key -> getRedisKey(key, libraryId))
                .collect(Collectors.toList());
    }


    // 循环获取值 获取 5 次 每次间隔 2 秒
    private void validateUploadIsSuccess(List<String> otherFileKeys) {

        ArrayList<String> distinct = CollUtil.distinct(otherFileKeys);
        final int MAX_RETRIES = 30;
        final long RETRY_DELAY_SECONDS = 3L;

        // 循环尝试获取文件状态，最多尝试 MAX_RETRIES 次
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                // 计算其他文件键在 Redis 中的实际存在数量
                Long existingCount = redisTemplate.countExistingKeys(distinct);

                // 如果所有文件都已存在，则认为上传成功，直接返回
                if (existingCount != null && existingCount.equals((long) distinct.size())) {
                    System.out.println("All files have been successfully uploaded.");
                    return;
                }

                // 等待 RETRY_DELAY_SECONDS 秒后再次尝试
                TimeUnit.SECONDS.sleep(RETRY_DELAY_SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                System.err.println("Thread was interrupted, Failed to complete operation");
                return;
            } catch (Exception e) {
                // 处理其他可能的异常
                System.err.println("An error occurred while checking file status: " + e.getMessage());
                return;
            }
        }

        // 如果经过多次尝试仍然没有所有文件，则认为上传失败
        System.err.println("Not all files were successfully uploaded after multiple retries.");


    }


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private ZipMaterialImportStrategy getSelf() {
        return SpringUtil.getBean(getClass());
    }
}
