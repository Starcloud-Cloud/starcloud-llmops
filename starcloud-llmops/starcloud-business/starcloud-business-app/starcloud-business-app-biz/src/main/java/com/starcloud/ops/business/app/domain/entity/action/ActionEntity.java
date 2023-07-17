package com.starcloud.ops.business.app.domain.entity.action;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

/**
 * 基础 action 实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
public class ActionEntity extends BaseActionEntity {


    public Object getValue() {
        return ObjectUtil.isNotEmpty(this.getResponse()) ? this.getResponse().getAnswer() : null;
    }

}
