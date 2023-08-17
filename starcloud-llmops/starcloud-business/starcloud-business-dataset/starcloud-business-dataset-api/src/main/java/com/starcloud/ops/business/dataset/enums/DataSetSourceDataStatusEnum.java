package com.starcloud.ops.business.dataset.enums;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataSetSourceDataStatusEnum implements IntArrayValuable {


    /**
     * 1-10 数据解析
     */
    ANALYSIS_ERROR(0,"数据解析失败"),

    /**
     * 11-20  上传
     */

    UPLOAD(11,"数据上传中"),
    UPLOAD_ERROR(15,"数据上传失败"),
    UPLOAD_COMPLETED(20,"数据上传成功"),

    /**
     * 21 - 30 数据同步
     */
    SYNCHRONIZATION(21,"数据同步中"),
    SYNCHRONIZATION_ERROR(25,"数据同步失败"),
    SYNCHRONIZATION__COMPLETED(30,"数据同步完成"),

    /**
     * 31 - 40  数据清洗
     */
    CLEANING_IN(31,"数据清洗中"),
    CLEANING_ERROR(35,"数据清洗失败"),
    CLEANING_COMPLETED(40,"数据清洗完成"),

    /**
     * 41 - 50  数据清洗
     */
    SPLIT_IN(41,"数据分割中"),
    SPLIT_ERROR(45,"数据分割失败"),
    SPLIT_COMPLETED(50,"数据分割完成"),

    /**
     * 51 -60  创建索引
     */
    INDEX_IN(51,"正在创建索引"),
    INDEX_ERROR(55,"创建索引失败"),
    INDEX_COMPLETED(60,"创建索引完成"),


    /**
     * 99 总结创建完成
     */
    SUMMERY_ERROR(98,"总结创建失败"),
    /**
     * 99 完成
     */
    COMPLETED(99," 完成"),

    ;

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
