package com.starcloud.ops.business.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.dto.TemplateConfigDTO;
import lombok.Data;

import java.util.List;

/**
 * App 实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppEntity {

    /**
     * App 唯一标识
     */
    private String uid;

    /**
     * App 名称
     */
    private String name;

    /**
     * App 版本
     */
    private String version;

    /**
     * App 类型
     */
    private String type;

    /**
     * App 标识
     */
    private String logotype;

    /**
     * App 来源类型
     */
    private String sourceType;

    /**
     * 模版标签
     */
    private List<String> tags;

    /**
     * 模版类别
     */
    private List<String> categories;

    /**
     * 模版场景
     */
    private List<String> scenes;

    /**
     * 模版详细配置信息, 步骤，变量，场景等
     */
    private AppConfigEntity config;


    public String getUniqueName() {
        return this.name + this.version + this.uid;
    }


}
