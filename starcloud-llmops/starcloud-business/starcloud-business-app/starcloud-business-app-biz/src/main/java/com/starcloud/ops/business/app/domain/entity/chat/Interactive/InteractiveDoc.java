package com.starcloud.ops.business.app.domain.entity.chat.Interactive;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class InteractiveDoc implements Serializable {

    /**
     * 数据ID
     */
    private Long id;

    /**
     * 相似度
     */
    private Double score;

    /**
     * 分段序号
     */
    private Integer position;

    /**
     * 数据集id
     */
    private String datasetId;


    /**
     * 显示名称
     */
    private String name;

    /**
     * 文档类型
     */
    private String type;


    /**
     * 文档URL
     */
    private String url;

    /**
     * 描述
     */
    private String desc;


    private LocalDateTime updateTime;

}

