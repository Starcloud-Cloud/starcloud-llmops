package com.starcloud.ops.business.app.domain.context;

import com.starcloud.ops.business.app.domain.entity.AppEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * App 上下文
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppContext {


    private String conversationId;

    private String stepId;

    /**
     * App 实体， 由 TemplateDTO 转换而来
     */
    private AppEntity app;

    private HttpServletResponse httpServletResponse;

    public AppContext(AppEntity app) {
        this.app = app;
        this.stepId = app.getConfig().getFirstStep().getField();
    }


    public Map<String, Object> getContextVariables() {

        this.app.getConfig().getSteps().forEach(appStepWrapper -> {

            appStepWrapper.getContextVariables();

        });

        return new HashMap();

    }


}
