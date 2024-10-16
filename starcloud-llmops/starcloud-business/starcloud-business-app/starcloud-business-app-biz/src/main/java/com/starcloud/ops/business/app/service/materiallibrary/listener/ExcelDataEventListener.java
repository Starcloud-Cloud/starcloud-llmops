package com.starcloud.ops.business.app.service.materiallibrary.listener;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.util.MaterialLibrary.dto.ExcelDataImportConfigDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 核心逻辑是 每次读取一百条 读取完成后构建对象以及异步上传图片 然后将值 放入队列中 全部读取完毕后 返回队列数据
 */
@Slf4j
public class ExcelDataEventListener extends AnalysisEventListener<Map<Integer, String>> {

    @Getter
    private List<MaterialLibrarySliceSaveReqVO> result = new CopyOnWriteArrayList<>();

    private final long start;


    public ExcelDataEventListener() {
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

        MaterialLibrarySliceSaveReqVO materialLibrarySliceSaveReqVO = createMaterialLibrarySliceSaveReqVO(importConfigDTO, data);
        result.add(materialLibrarySliceSaveReqVO);
    }


    /**
     * if have something to do after all analysis
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        long end = System.currentTimeMillis();
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


    private MaterialLibrarySliceSaveReqVO createMaterialLibrarySliceSaveReqVO(ExcelDataImportConfigDTO importConfigDTO, Map<Integer, String> data) {
        MaterialLibrarySliceSaveReqVO materialLibrarySliceSaveReqVO = new MaterialLibrarySliceSaveReqVO();
        materialLibrarySliceSaveReqVO.setLibraryId(importConfigDTO.getLibraryId());
        materialLibrarySliceSaveReqVO.setStatus(true);
        materialLibrarySliceSaveReqVO.setIsShare(false);
        materialLibrarySliceSaveReqVO.setUsedCount(0L);
        materialLibrarySliceSaveReqVO.setSequence(0L);

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

            contents.add(tableContent);

        }

        materialLibrarySliceSaveReqVO.setContent(contents);
        return materialLibrarySliceSaveReqVO;
    }
}