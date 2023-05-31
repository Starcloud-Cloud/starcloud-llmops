package com.starcloud.ops.business.app.domain.context;

import com.starcloud.ops.business.app.domain.entity.AppEntity;
import lombok.Data;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class AppContext {

    private AppEntity app;
}
