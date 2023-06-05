package com.starcloud.ops.business.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.dto.StepWrapperDTO;
import com.starcloud.ops.business.app.api.dto.VariableDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * App 配置实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppConfigEntity {

    /**
     * 模版名称
     */
    private String name;

    /**
     * 模版类型, 0：系统推荐模版，1：我的模版，2：下载模版
     */
    private String type;

    /**
     * 模版标识, 区分自定义模版和每一种具体的系统模版，所有的模版的具体类型都基于此标识，不同的标识，模版的具体配置（步骤，变量，场景等）会有所不同。
     */
    private String logotype;

    /**
     * 模版来源类型，表示模版的是从那个平台创建，或者下载的。比如 WrdPress，Chrome插件等
     */
    private String sourceType;

    /**
     * 模版版本，默认版本 1.0.0
     */
    private String version;

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
     * 模版步骤
     */
    private List<AppStepWrapper> steps;

    /**
     * 模版变量
     */
    private List<AppVariableEntity> variables;


    /**
     * 模版步骤
     */
    public AppStepWrapper getFirstStep() {
        return Optional.ofNullable(steps).orElse(new ArrayList<>()).stream().findFirst().orElse(null);
    }

}
