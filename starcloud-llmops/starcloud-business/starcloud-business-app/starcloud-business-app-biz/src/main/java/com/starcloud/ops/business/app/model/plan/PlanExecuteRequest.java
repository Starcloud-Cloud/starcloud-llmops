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


    private String appUid;

    /**
     * 计划UID
     */
    private String planUid;

    /**
     * 是否异步
     */
    private Boolean async;

    /**
     * 选择执行海报风格ID
     */
    private String posterStyleId;

    /**
     * 素材列表 JSON 格式
     */
    private String materialListJson;


    private List<Map<String, Object>> materialList;

    /**
     * 生成海报数量
     */
    private Integer totalCount;
}
