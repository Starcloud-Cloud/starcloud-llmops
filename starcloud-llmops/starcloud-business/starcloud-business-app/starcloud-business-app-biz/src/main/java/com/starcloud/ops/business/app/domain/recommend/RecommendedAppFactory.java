package com.starcloud.ops.business.app.domain.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.util.MessageUtil;

import java.util.Arrays;
import java.util.Collections;

/**
 * 推荐应用工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendedAppFactory {

    /**
     * 生成文本应用
     *
     * @return AppRespVO
     */
    public static AppRespVO defGenerateTextApp() {
        AppRespVO app = new AppRespVO();
        app.setName(MessageUtil.getMessage("WORKFLOW_STEP_GENERATE_TEXT_NAME"));
        app.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_GENERATE_TEXT_DESCRIPTION"));
        app.setModel(AppModelEnum.COMPLETION.name());
        app.setType(AppTypeEnum.MYSELF.name());
        app.setSource(AppSourceEnum.WEB.name());
        app.setTags(Collections.singletonList("Generate Text"));
        app.setCategories(Collections.singletonList("Writing"));
        app.setScenes(Arrays.asList(AppSceneEnum.WEB_ADMIN.name(), AppSceneEnum.WEB_MARKET.name()));
        app.setImages(null);
        app.setIcon("post");
        app.setWorkflowConfig(RecommendedConfigFactory.defGenerateTextConfig());
        return app;
    }
}
