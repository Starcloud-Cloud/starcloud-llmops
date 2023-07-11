package com.starcloud.ops.business.dataset.enums;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataSetSourceDataStatusEnum implements IntArrayValuable {

    UPLOAD(0,"数据上传中"),

    SYNCHRONIZATION(-1,"数据同步中"),

    CLEANING_IN(1,"数据清洗中"),
    CLEANING_ERROR(2,"数据清洗失败"),
    CLEANING_COMPLETED(3,"数据完成"),

    SPLIT_IN(4,"数据分割中"),
    SPLIT_ERROR(5,"数据分割失败"),
    SPLIT_COMPLETED(6,"数据分割完成"),

    INDEX_IN(7,"正在创建索引"),
    INDEX_ERROR(8,"创建索引失败"),
    INDEX_COMPLETED(9,"创建索引完成");

    private final Integer status;
    private final String name;



    /**
     * @return int 数组
     */
    @Override
    public int[] array() {
        return new int[0];
    }
}
