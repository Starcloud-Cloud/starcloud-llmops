package com.starcloud.ops.business.app.domain.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.action.ActionResponseRespVO;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;

/**
 * 推荐应用Variable Item 工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendedResponseFactory {

    /**
     * 默认文本响应
     *
     * @return ActionResponseRespVO
     */
    public static ActionResponseRespVO defTextResponse() {
        ActionResponseRespVO response = new ActionResponseRespVO();
        response.setType(AppStepResponseTypeEnum.TEXT.name());
        response.setStyle(AppStepResponseStyleEnum.TEXTAREA.name());
        response.setIsShow(Boolean.TRUE);
        return response;
    }
}
