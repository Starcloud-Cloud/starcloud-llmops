package com.starcloud.ops.business.app.service.materiallibrary.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ExcelHeadEventListener extends AnalysisEventListener<Map<Integer, String>> {


    // 定义变量，分别表示限制列数和行数 以及限定的表头
    private Integer limitColSize;
    private Set<String> oldHead;


    // 构造函数
    ExcelHeadEventListener() {

    }

    // 带参构造函数，直接赋值限制行列
    public ExcelHeadEventListener(Integer limitColSize, Set<String> oldHead) {
        this.limitColSize = limitColSize;
        this.oldHead = oldHead;
    }

    /**
     * When analysis one row trigger invoke function.
     *
     * @param data    one row value. It is same as {@link AnalysisContext#readRowHolder()}
     * @param context analysis context
     */
    @Override
    public void invoke(Map<Integer, String> integerStringMap, AnalysisContext analysisContext) {
    }

    /**
     * 这里会一行行的返回头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        Set<String> newHead = new HashSet<>();
        if (context.getCurrentRowNum() == limitColSize) {
            newHead.addAll(headMap.values());
            log.info("解析表头数据:{}", JSON.toJSONString(headMap));
        }

        //
        //
        // Integer size = headMap.keySet().size();
        //
        //
        // for (int i = 0; i < (size >= this.limitColSize ? this.limitColSize : size); i++) {
        //
        // }
        // log.info("解析表头数据:{}", JSON.toJSONString(headMap));
        // 验证表头
        checkHead(this.oldHead, newHead);
    }


    /**
     * if have something to do after all analysis
     *
     * @param context
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