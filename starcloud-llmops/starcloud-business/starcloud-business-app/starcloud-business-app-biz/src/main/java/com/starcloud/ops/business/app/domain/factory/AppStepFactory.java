package com.starcloud.ops.business.app.domain.factory;

import com.starcloud.ops.business.app.api.dto.StepDTO;
import com.starcloud.ops.business.app.domain.entity.BaseStepEntity;
import com.starcloud.ops.business.app.enums.StepTypeEnum;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public class AppStepFactory {


    public BaseStepEntity factory(StepDTO step) {
        Class<? extends BaseStepEntity> entity = StepTypeEnum.getEntityByName(step.getType());


        return null;
    }
}
