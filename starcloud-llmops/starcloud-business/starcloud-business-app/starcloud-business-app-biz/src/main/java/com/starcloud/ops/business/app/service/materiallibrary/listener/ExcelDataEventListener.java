package com.starcloud.ops.business.app.service.materiallibrary.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.enums.materiallibrary.ColumnTypeEnum;
import com.starcloud.ops.business.app.service.materiallibrary.CommonExcelReadService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.MaterialLibrary.dto.ExcelDataImportConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DatePattern.PURE_DATETIME_MS_PATTERN;

@Slf4j
public class ExcelDataEventListener extends AnalysisEventListener<Map<Integer, String>> {

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    private List<MaterialLibrarySliceSaveReqVO> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private CommonExcelReadService service;

    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private File[] files;

    /**
     * 解压后的绝对路径
     */
    private String unzipDirPath;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param service 公共 service
     */
    public ExcelDataEventListener(MaterialLibrarySliceService service,
                                  File[] files, String unzipDirPath) {
        this.service = service;
        this.files = files;
        this.unzipDirPath = unzipDirPath;

    }


    /**
     * When analysis one row trigger invoke function.
     *
     * @param data    one row value. It is same as {@link AnalysisContext#readRowHolder()}
     * @param context analysis context
     */
    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));

        if (data.isEmpty()) {
            return;
        }

        ExcelDataImportConfigDTO importConfigDTO = BeanUtil.toBean(context.getCustom(), ExcelDataImportConfigDTO.class);


        MaterialLibrarySliceSaveReqVO materialLibrarySliceSaveReqVO = new MaterialLibrarySliceSaveReqVO();
        materialLibrarySliceSaveReqVO.setLibraryId(importConfigDTO.getLibraryId());
        materialLibrarySliceSaveReqVO.setStatus(true);
        materialLibrarySliceSaveReqVO.setIsShare(false);

        // 用并行流处理大数据集
        long totalLength = data.values().parallelStream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .mapToInt(String::length)
                .sum();

        materialLibrarySliceSaveReqVO.setCharCount(totalLength);

        List<MaterialLibrarySliceSaveReqVO.TableContent> contents = new ArrayList<>();

        List<MaterialLibraryTableColumnRespVO> columnConfig = importConfigDTO.getColumnConfig();
        List<String> screenShot = new ArrayList<>();
        for (int i = 0; i < columnConfig.size(); i++) {
            MaterialLibraryTableColumnRespVO tableColumnRespVO = columnConfig.get(i);

            MaterialLibrarySliceSaveReqVO.TableContent tableContent = new MaterialLibrarySliceSaveReqVO.TableContent();
            tableContent.setColumnId(tableColumnRespVO.getId());
            tableContent.setColumnCode(tableColumnRespVO.getColumnCode());
            tableContent.setColumnName(tableColumnRespVO.getColumnName());
            tableContent.setValue(data.get(i));

            if (ColumnTypeEnum.IMAGE.getCode().equals(tableColumnRespVO.getColumnType())) {
                String imgUrl = StrUtil.NULL;
                try {
                    List<File> filesInImagesFolder = findFilesInTargetFolder(files, "images", tableContent.getValue());
                    if (!CollUtil.isEmpty(filesInImagesFolder)) {
                        File images = FileUtil.file(filesInImagesFolder.get(0));

                        imgUrl = ImageUploadUtils.uploadImage(StrUtil.format("{}_{}", LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PURE_DATETIME_MS_PATTERN), images.getName()), ImageUploadUtils.UPLOAD_PATH, IoUtil.readBytes(Files.newInputStream(images.toPath()))).getUrl();
                    }
                } catch (IOException e) {
                    log.error("图片解析异常");
                    // throw new RuntimeException("图片解析异常");
                }
                tableContent.setValue(imgUrl);

            } else if (ColumnTypeEnum.DOCUMENT.getCode().equals(tableColumnRespVO.getColumnType())
                    && StringUtils.isNotBlank(unzipDirPath)) {
                // 文档相对路径  文字文稿.docx
                String documentPath = tableContent.getValue();
                List<String> urls = documentScreenshot(IdUtil.fastSimpleUUID(), documentPath, unzipDirPath);
                screenShot.addAll(urls);
            }
            contents.add(tableContent);
        }

        Map<String, Integer> codeType = importConfigDTO.getColumnConfig().stream().collect(Collectors.toMap(MaterialLibraryTableColumnRespVO::getColumnCode, MaterialLibraryTableColumnRespVO::getColumnType));
        Iterator<String> iterator = screenShot.iterator();
        for (MaterialLibrarySliceSaveReqVO.TableContent content : contents) {
            Integer type = codeType.get(content.getColumnCode());
            String value = content.getValue();
            if (ColumnTypeEnum.IMAGE.getCode().equals(type)
                    && (StringUtils.isBlank(value) || StrUtil.NULL.equalsIgnoreCase(value))
                    && iterator.hasNext()) {
                content.setValue(iterator.next());
            }
        }

        materialLibrarySliceSaveReqVO.setContent(contents);

        cachedDataList.add(materialLibrarySliceSaveReqVO);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }


    /**
     * if have something to do after all analysis
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("所有数据解析完成！");

    }


    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     *
     * @param exception
     * @param context
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析失败，但是继续解析下一行:{}", exception.getMessage());
        // 如果是某一个单元格的转换异常 能获取到具体行号
        // 如果要获取头的信息 配合invokeHeadMap使用
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            log.error("第{}行，第{}列解析异常，数据为:{}", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
        }
    }


    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        service.saveBatchData(cachedDataList);
        log.info("存储数据库成功！");
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
}