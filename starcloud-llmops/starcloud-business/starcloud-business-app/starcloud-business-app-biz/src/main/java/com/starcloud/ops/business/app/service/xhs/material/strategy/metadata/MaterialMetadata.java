package com.starcloud.ops.business.app.service.xhs.material.strategy.metadata;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

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
    private String materialStepName;

}