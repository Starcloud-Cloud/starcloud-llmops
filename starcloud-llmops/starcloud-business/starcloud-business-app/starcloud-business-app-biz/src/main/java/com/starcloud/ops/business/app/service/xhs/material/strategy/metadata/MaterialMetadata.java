package com.starcloud.ops.business.app.service.xhs.material.strategy.metadata;

import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialUsageModel;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Accessors(chain = true)
public class MaterialMetadata implements java.io.Serializable {

    private static final long serialVersionUID = 6640844962671452679L;

    /**
     * 应用UID
     */
    private String appUid;

    /**
     * 计划UID
     */
    private String planUid;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 计划来源
     */
    private CreativePlanSourceEnum planSource;

    /**
     * 素材库UID
     */
    private String materialLibraryUid;

    /**
     * 素材类型
     */
    private String materialType;

    /**
     * 素材使用模型
     */
    private MaterialUsageModel materialUsageModel;

    /**
     * 素材步骤名称
     */
    private String materialStepId;

    /**
     * 海报步骤ID
     */
    private String posterStepId;

    /**
     * 素材字段配置
     */
    private List<MaterialFieldConfigDTO> materialFieldList;

}
