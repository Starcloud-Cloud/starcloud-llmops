package com.starcloud.ops.business.app.service.materiallibrary.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.enums.materiallibrary.ColumnTypeEnum;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.MaterialLibrary.OperateImportUtil;
import com.starcloud.ops.business.app.util.MaterialLibrary.dto.ExcelDataImportConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DatePattern.PURE_DATETIME_MS_PATTERN;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.EXCEL_NOT_EXIST;
import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.*;
import static com.starcloud.ops.business.app.service.materiallibrary.config.MaterialLibraryDataUploadJobConfiguration.LIBRARY_DATA_UPLOAD_THREAD_POOL_TASK_EXECUTOR;

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

    @Resource(name = LIBRARY_DATA_UPLOAD_THREAD_POOL_TASK_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

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
            throw new RuntimeException(StrUtil.format("压缩包中未找到名为:{} 的文件", TEMPLATE_FILE_NAME));
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
                columnData = OperateImportUtil.readOtherExcel(excel, TEMPLATE_FILE_TABLE_HEAD_CELL + 1, imageOrDocumentColumn);
                if (!columnData.isEmpty()) {
                    executeAsyncUpload(columnData, childrenDirs, unzipDir.getAbsolutePath(), importReqVO.getLibraryId());
                }

            }


        }
        log.info("表头读取完成，验证成功 开始异步存储数据");

        ExcelDataImportConfigDTO importConfigDTO = new ExcelDataImportConfigDTO();
        importConfigDTO.setUserId(getLoginUserId());
        importConfigDTO.setLibraryId(importReqVO.getLibraryId());


        importConfigDTO.setColumnConfig(BeanUtils.toBean(saveReqVOS, MaterialLibraryTableColumnRespVO.class));

        // 异步存储数据
        OperateImportUtil.readExcel(excel, childrenDirs, importConfigDTO, materialLibrarySliceService, 2, unzipDir.getAbsolutePath());

        if (columnData.isEmpty()) {

            try {
                for (int i = 0; i < 10; i++) {
                    long size = materialLibrarySliceService.getMaterialLibrarySliceByLibraryId(importReqVO.getLibraryId()).size();
                    if (size > 0) {
                        log.info("Material library slice found after {} attempts.", i + 1);
                        return;
                    }
                    TimeUnit.SECONDS.sleep(2L);
                }
                log.warn("Material library slice not found after 10 attempts.");
                return;
            } catch (InterruptedException e) {
                // 改进异常处理
                Thread.currentThread().interrupt(); // 恢复中断状态
                log.error("Interrupted while waiting for material library slice.", e);
                return;
            }

        }
        List<String> flatValues = columnData.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        validateUploadIsSuccess(buildRedisKey(flatValues, importReqVO.getLibraryId()));
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

    public void executeAsyncUpload(Map<Integer, List<String>> columnData, File[] childrenDirs, String unzipDirPath, Long libraryId) {
        log.info("=========> 图片和文档数据 异步处理");
        String prefix = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PURE_DATETIME_MS_PATTERN) + RandomUtil.randomInt(1000, 9999);

        List<String> images = CollUtil.distinct(columnData.get(ColumnTypeEnum.IMAGE.getCode()));

        if (CollUtil.isNotEmpty(images)) {
            log.info("=========> 图片数量为{}", images.size());
            images.forEach(imageName -> {
                List<File> filesInImagesFolder = findFilesInTargetFolder(childrenDirs, "images", imageName);
                if (CollUtil.isEmpty(filesInImagesFolder)) {
                    redisTemplate.boundValueOps(getRedisKey(imageName, libraryId)).set(JSONUtil.toJsonStr(MATERIAL_LIBRARY_FILE_NO_FOUND), 3, TimeUnit.DAYS);
                    return;
                }
                threadPoolTaskExecutor.execute(() -> {
                    File image = FileUtil.file(filesInImagesFolder.get(0));

                    try {
                        String url = ImageUploadUtils.uploadImage(StrUtil.format("{}_{}", prefix, imageName), ImageUploadUtils.UPLOAD_PATH, IoUtil.readBytes(Files.newInputStream(image.toPath()))).getUrl();
                        redisTemplate.boundValueOps(getRedisKey(imageName, libraryId)).set(JSONUtil.toJsonStr(url), 3, TimeUnit.DAYS);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            });
        }


        List<String> documentNames = CollUtil.distinct(columnData.get(ColumnTypeEnum.DOCUMENT.getCode()));
        if (CollUtil.isNotEmpty(documentNames)) {
            log.info("=========> 文档数量为{}", images.size());
            documentNames.forEach(documentName -> {
                threadPoolTaskExecutor.execute(() -> {
                    List<String> urls = documentScreenshot(IdUtil.fastSimpleUUID(), documentName, unzipDirPath);
                    redisTemplate.boundValueOps(getRedisKey(documentName, libraryId)).set(String.join(",", urls), 3, TimeUnit.DAYS);

                });

            });

        }
        log.info("=========> 图片和文档数据 异步处理中");

    }


    private static boolean isTemplateFile(File file) {
        // 确保是文件而不是目录
        if (!FileUtil.isFile(file)) {
            return false;
        }

        // 检查文件名是否匹配
        return TEMPLATE_FILE_NAME.equalsIgnoreCase(FileNameUtil.getName(file));
    }


    private List<String> documentScreenshot(String parseUid, String documentPath, String unzipDir) {
        HashMap<String, Object> paramMap = new HashMap<>();
        File document = Paths.get(unzipDir, documentPath).toFile();
        if (!document.exists()) {
            return Collections.emptyList();
        }
        paramMap.put("file", document);
        paramMap.put("parseUid", parseUid);
        String result = HttpUtil.post(getUrl(), paramMap, 1_0000);
        List<String> documentScreenshot = JSONUtil.parseArray(result).toList(String.class);
        if (documentScreenshot == null) {
            documentScreenshot = Collections.emptyList();
        }
        return documentScreenshot;
    }

    private String getUrl() {
        DictDataService bean = SpringUtil.getBean(DictDataService.class);
        DictDataDO dictData = bean.parseDictData("playwright", "material_parse");
        return dictData.getValue();
    }

    /**
     * 查找特定文件夹下的特定文件
     *
     * @param directoriesToSearch 文件列表
     * @param targetFolderName    目标文件夹名称
     * @param targetedFileName    目标文件名称
     * @return 查询到的文件
     */
    public List<File> findFilesInTargetFolder(File[] directoriesToSearch, String targetFolderName, String targetedFileName) {
        List<File> foundFiles = new ArrayList<>();
        for (File directory : directoriesToSearch) {
            if (directory.isDirectory()) {
                // 遍历目录下的所有文件和子目录
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory() && file.getName().equals(targetFolderName)) {
                            // 进入目标文件夹后，再次遍历查找指定文件
                            File[] targetFiles = file.listFiles(pathname -> pathname.getName().equals(targetedFileName));
                            if (targetFiles != null) {
                                for (File targetFile : targetFiles) {
                                    if (targetFile.isFile()) {
                                        foundFiles.add(targetFile);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return foundFiles;
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

}
