package com.starcloud.ops.business.app.service.materiallibrary.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_DATA_UPLOAD_BIG;

@Slf4j
public class ExcelHeadEventListener extends AnalysisEventListener<Map<Integer, String>> {


    // 定义变量，分别表示限制列数和行数 以及限定的表头
    private Integer limitColSize;
    /**
     * -- GETTER --
     * 获取excel的所有数据，数据量太大会出现内存溢出
     */
    @Getter
    private List<String> heads = new CopyOnWriteArrayList<>();


    // 带参构造函数，直接赋值限制行列
    public ExcelHeadEventListener(Integer limitColSize) {
        this.limitColSize = limitColSize;
    }

    /**
     * When analysis one row trigger invoke function.
     *
     * @param integerStringMap one row value. It is same as {@link AnalysisContext#readRowHolder()}
     * @param analysisContext  analysis context
     */
    @Override
    public void invoke(Map<Integer, String> integerStringMap, AnalysisContext analysisContext) {
    }

    /**
     * 这里会一行行的返回头
     *
     * @param headMap 头数据
     * @param context 内容
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        int max_size = 1000;
        Integer totalRowNumber = context.readSheetHolder().getApproximateTotalRowNumber();
        log.info("总行数为{}", totalRowNumber);
        if (max_size < totalRowNumber) {
            // throw new RuntimeException("导入数据量过大，请分批导入");
            throw exception(MATERIAL_LIBRARY_DATA_UPLOAD_BIG, max_size);
        }

        if (Objects.equals(context.getCurrentRowNum(), limitColSize)) {
            heads.addAll(headMap.values());
            log.info("解析表头数据:{}", JSON.toJSONString(headMap));
        }
    }


    /**
     * if have something to do after all analysis
     *
     * @param context 内容
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("表头数据解析完成！");
    }


    /**
     * 工具方法，根据传入的列限制来保留数据，也就是只取前几个Key，如果有需求，可以自己添加KeySet排序之后再取，我这里没这个需求
     *
     * @param oldMap 未限制列前的Map
     * @return 限制列后的Map
     */
    private Map<Integer, String> getLimitColMap(Map<Integer, String> oldMap) {
        Integer size = oldMap.keySet().size();

        Map<Integer, String> newMap = new HashMap<>();
        for (int i = 0; i < (size >= this.limitColSize ? this.limitColSize : size); i++) {
            newMap.put(i, oldMap.get(i));
        }
        return newMap;
    }


    /**
     * @param materialTableColumn 素材库存在的表头
     * @param importTableColumn   导入数据的表头
     */
    private void checkHead(Set<String> materialTableColumn, Set<String> importTableColumn) {
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
}