package com.starcloud.ops.business.dataset.util.dataset;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;

/**
 * @className    : DatasetUID
 * @description  : [数据集UID生成]
 * @author       : [wuruiqiang]
 * @version      : [v1.0]
 * @createTime   : [2023/5/31 18:16]
 * @updateUser   : [wuruiqiang]
 * @updateTime   : [2023/5/31 18:16]
 * @updateRemark : [暂无修改]
 */
@SuppressWarnings("unchecked")
public class DatasetUID {

    // 定义ID 前缀 前缀为 base 机密结果

    /**
     * 数据集
     */
    private final static String DATASET_PREFIX = "数据集";

    /**
     * 源数据
     */
    private final static String SOURCE_DATA_PREFIX = "源数据";

    /**
     * 存储集
     */
    private final static String STORAGE_PREFIX = "存储集";
    
    
    /**
     * 数据集UID
     *
     */
    public static String createDatasetUID() {
        String encode = Base64.encode(DATASET_PREFIX);
        return encode+IdUtil.fastSimpleUUID();
    }

    /**
     * 数据集UID
     *
     */
    public static String createSourceDataUID() {
        String encode = Base64.encode(SOURCE_DATA_PREFIX);
        return encode+IdUtil.fastSimpleUUID();
    }

    /**
     * 数据集UID
     *
     */
    public static String createStorageUID() {
        String encode = Base64.encode(STORAGE_PREFIX);
        return encode+IdUtil.fastSimpleUUID();
    }



}
