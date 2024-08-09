package com.starcloud.ops.business.app.service.materiallibrary.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.starcloud.ops.business.app.enums.materiallibrary.ColumnTypeEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ExcelImageEventListener extends AnalysisEventListener<Map<Integer, String>> {


    // 定义变量，分别表示限制列数和行数 以及限定的表头
    private Map<Integer, List<Long>> columnIndices;
    /**
     * -- GETTER --
     * 获取excel的所有数据，数据量太大会出现内存溢出
     */
    @Getter
    private Map<Integer, List<String>> columnData = new ConcurrentHashMap<>();
    private List<String> imageNames = new ArrayList<>();
    private List<String> documentNames = new ArrayList<>();


    // 带参构造函数，直接赋值限制行列
    public ExcelImageEventListener(Map<Integer, List<Long>> columnIndices) {
        this.columnIndices = columnIndices;
    }

    /**
     * When analysis one row trigger invoke function.
     *
     * @param rowData one row value. It is same as {@link AnalysisContext#readRowHolder()}
     * @param analysisContext  analysis context
     */
    @Override
    public void invoke(Map<Integer, String> rowData, AnalysisContext analysisContext) {


        for (Map.Entry<Integer, List<Long>> entry : columnIndices.entrySet()) {
            Integer type = entry.getKey();
            List<Long> indices = entry.getValue();

            // 对于每个 type 的索引列表，从 rowData 中获取对应的值
            for (Long index : indices) {
                String value = rowData.get(index.intValue());
                if (StrUtil.isNotBlank(value))
                    if (ColumnTypeEnum.IMAGE.getCode().equals(type))
                        imageNames.add(value);
                    else if (ColumnTypeEnum.DOCUMENT.getCode().equals(type))
                        documentNames.add(value);
            }

        }
    }


    /**
     * if have something to do after all analysis
     *
     * @param context 内容
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 将收集到的数据放入 columnData
        columnData.put(ColumnTypeEnum.IMAGE.getCode(), CollUtil.distinct(imageNames));
        columnData.put(ColumnTypeEnum.DOCUMENT.getCode(), CollUtil.distinct(documentNames));
        log.info("表格中图像/文档数据获取完成！");
    }


}