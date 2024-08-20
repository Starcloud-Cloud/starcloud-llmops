package com.starcloud.ops.business.app.service.materiallibrary.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.enums.materiallibrary.ColumnTypeEnum;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.util.MaterialLibrary.dto.ExcelDataImportConfigDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class ExcelDataEventListener extends AnalysisEventListener<Map<Integer, String>> {

    private static final int BATCH_COUNT = 100;
    private List<MaterialLibrarySliceSaveReqVO> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    private List<String> otherFileKeys = new ArrayList<>();

    private MaterialLibrarySliceService service;

    private File[] files;

    private long start;
    private long end;


    /**
     * 解压后的绝对路径
     */
    private String unzipDirPath;


    public ExcelDataEventListener(MaterialLibrarySliceService service,
                                  File[] files, String unzipDirPath) {
        this.service = service;
        this.files = files;
        this.unzipDirPath = unzipDirPath;
        start = System.currentTimeMillis();

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

        long totalLength = data.values().stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .mapToInt(String::length)
                .sum();

        materialLibrarySliceSaveReqVO.setCharCount(totalLength);

        List<MaterialLibrarySliceSaveReqVO.TableContent> contents = new ArrayList<>();

        List<MaterialLibraryTableColumnRespVO> columnConfig = importConfigDTO.getColumnConfig();
        for (int i = 0; i < columnConfig.size(); i++) {
            MaterialLibraryTableColumnRespVO tableColumnRespVO = columnConfig.get(i);

            MaterialLibrarySliceSaveReqVO.TableContent tableContent = new MaterialLibrarySliceSaveReqVO.TableContent();
            tableContent.setColumnId(tableColumnRespVO.getId());
            tableContent.setColumnCode(tableColumnRespVO.getColumnCode());
            tableContent.setColumnName(tableColumnRespVO.getColumnName());
            tableContent.setValue(data.get(i));

            if (ColumnTypeEnum.IMAGE.getCode().equals(tableColumnRespVO.getColumnType()) || ColumnTypeEnum.DOCUMENT.getCode().equals(tableColumnRespVO.getColumnType())) {
                otherFileKeys.add(data.get(i));
            }

            contents.add(tableContent);

        }

        materialLibrarySliceSaveReqVO.setContent(contents);

        cachedDataList.add(materialLibrarySliceSaveReqVO);


        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
            otherFileKeys = ListUtils.newArrayList();
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
        end = System.currentTimeMillis();
        log.info("所有数据解析完成！耗时{}", end - start);

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
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        // threadPoolTaskExecutor.execute(() -> {
        service.batchSaveDataAndExecuteOtherFile(cachedDataList, otherFileKeys);
        // });
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


        // // 指定文件夹路径
        // String folderPath = "/path/to/your/folder";
        // // 获取文件夹内所有.txt文件
        // List<File> txtFiles = FileUtil.loopFiles(new File(folderPath), file -> file.getName().toLowerCase().endsWith(".txt"));
        //
        // // 输出文件路径
        // for (File txtFile : txtFiles) {
        //     System.out.println(txtFile.getAbsolutePath());
        // }


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


}