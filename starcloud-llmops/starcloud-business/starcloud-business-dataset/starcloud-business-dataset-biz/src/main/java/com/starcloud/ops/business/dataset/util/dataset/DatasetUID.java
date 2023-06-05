package com.starcloud.ops.business.dataset.util.dataset;

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
    /**
     * 数据集UID
     *
     */
    public static String getDatasetUID() {
        //TODO 业务ID
      return IdUtil.getSnowflakeNextIdStr();
    }



}
