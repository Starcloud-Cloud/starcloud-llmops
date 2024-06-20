package com.starcloud.ops.business.app.service.xhs.material.strategy.metadata;

import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
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
     * 素材类型
     */
    private String materialType;

    /**
     * 素材步骤名称
     */
    private String materialStepId;

    /**
     * 素材字段配置
     */
    private List<MaterialFieldConfigDTO> materialFieldList;

}
