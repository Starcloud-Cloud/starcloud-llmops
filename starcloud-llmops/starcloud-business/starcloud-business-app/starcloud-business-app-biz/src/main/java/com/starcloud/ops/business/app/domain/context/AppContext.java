package com.starcloud.ops.business.app.domain.context;

import com.starcloud.ops.business.app.domain.entity.AppEntity;
import lombok.Data;

/**
 * App 上下文
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class AppContext {

    /**
     * App 实体， 由 TemplateDTO 转换而来
     */
    private AppEntity app;
}
