package com.starcloud.ops.business.app.model.plan;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class PlanExecuteRequest implements Serializable {

    private static final long serialVersionUID = -8952228169156691678L;

    /**
     * 计划UID
     */
    private String planUid;


    /**
     * 是否异步
     */
    private Boolean async;

    /**
     * 素材列表
     */
    private List<Map<String, Object>> materialList;


    /**
     * 素材列表 JSON 格式
     */
    private String materialListJson;
}
