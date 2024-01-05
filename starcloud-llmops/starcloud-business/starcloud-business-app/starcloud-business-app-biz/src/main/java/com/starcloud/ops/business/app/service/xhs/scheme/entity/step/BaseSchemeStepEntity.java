package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public abstract class BaseSchemeStepEntity implements java.io.Serializable {

    private static final long serialVersionUID = 5401242096922842719L;

    /**
     * 唯一
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 转换到应用参数
     */
    public abstract void convertAppStepWrapper(WorkflowStepWrapperRespVO stepWrapper);

    /**
     * 转换到创作方案参数
     */
    public abstract void convertCreativeSchemeStep();

}
