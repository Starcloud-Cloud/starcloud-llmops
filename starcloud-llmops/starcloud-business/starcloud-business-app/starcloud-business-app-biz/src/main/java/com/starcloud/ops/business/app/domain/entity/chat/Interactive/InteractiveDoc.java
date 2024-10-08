package com.starcloud.ops.business.app.domain.entity.chat.Interactive;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class InteractiveDoc implements Serializable {

    /**
     * 数据ID
     */
    private Long id;

    /**
     * 数据集id
     */
    @Deprecated
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

    /**
     *
     */
    private Object ext;


    private String updateTime;

}

