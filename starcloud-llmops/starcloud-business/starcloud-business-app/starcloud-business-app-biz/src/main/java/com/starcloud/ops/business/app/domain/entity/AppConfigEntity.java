package com.starcloud.ops.business.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppConfigEntity implements Serializable {

    private static final long serialVersionUID = 6506975829346659943L;

    /**
     * 模版步骤
     */
    private List<AppStepWrapper> steps;

    /**
     * 模版变量
     */
    private List<AppVariableEntity> variables;



    /**
     * 获取 多 action 下的图标
     * @return
     */
    public List<String> getActionsIcon() {
        return null;
    }



    /**
     * 模版步骤
     */
    public AppStepWrapper getFirstStep() {
        return Optional.ofNullable(steps).orElse(new ArrayList<>()).stream().findFirst().orElse(null);
    }

    /**
     * 模版步骤
     */
    public AppStepWrapper getStep(String stepId) {
        return Optional.ofNullable(steps).orElse(new ArrayList<>()).stream().filter((stepWrapper) -> stepWrapper.getField().equals(stepId)).findFirst().orElse(null);
    }

}
