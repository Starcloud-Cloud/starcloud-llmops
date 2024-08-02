package com.starcloud.ops.business.app.recommend;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.api.app.handler.ImageOcr.HandlerResponse;
import com.starcloud.ops.business.app.api.app.vo.params.JsonDataVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.ActionResponseRespVO;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;

/**
 * 推荐应用Variable Item 工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendResponseFactory {

    /**
     * 默认文本响应
     *
     * @return ActionResponseRespVO
     */
    public static ActionResponseRespVO defTextResponse() {
        return defTextResponse(Boolean.TRUE);
    }

    /**
     * 默认文本响应
     *
     * @return ActionResponseRespVO
     */
    public static ActionResponseRespVO defTextResponse(Boolean isShow) {
        ActionResponseRespVO response = new ActionResponseRespVO();
        response.setIsShow(isShow);
        response.setReadOnly(Boolean.FALSE);
        response.setType(AppStepResponseTypeEnum.TEXT.name());
        response.setStyle(AppStepResponseStyleEnum.TEXTAREA.name());
        return response;
    }

    /**
     * 默认文本响应
     *
     * @return ActionResponseRespVO
     */
    public static ActionResponseRespVO defInputResponse() {
        ActionResponseRespVO response = new ActionResponseRespVO();
        response.setIsShow(Boolean.TRUE);
        response.setReadOnly(Boolean.FALSE);
        response.setType(AppStepResponseTypeEnum.TEXT.name());
        response.setStyle(AppStepResponseStyleEnum.INPUT.name());
        return response;
    }

    /**
     * 只读JSON响应
     *
     * @return ActionResponseRespVO
     */
    public static ActionResponseRespVO defReadOnlyResponse(String jsonSchema) {
        return defReadOnlyResponse(Boolean.TRUE, jsonSchema);
    }

    /**
     * 只读JSON响应
     *
     * @return ActionResponseRespVO
     */
    public static ActionResponseRespVO defReadOnlyResponse(Boolean isShow, String jsonSchema) {
        return defJsonResponse(isShow, Boolean.TRUE, jsonSchema);
    }

    /**
     * 只读JSON响应
     *
     * @return ActionResponseRespVO
     */
    public static ActionResponseRespVO defJsonResponse(Boolean isShow, Boolean readOnly, String jsonSchema) {
        JsonDataVO jsonData = new JsonDataVO();
        jsonData.setJsonSchema(jsonSchema);

        ActionResponseRespVO response = new ActionResponseRespVO();
        response.setIsShow(isShow);
        response.setReadOnly(readOnly);
        response.setType(AppStepResponseTypeEnum.JSON.name());
        response.setStyle(AppStepResponseStyleEnum.JSON.name());
        response.setOutput(jsonData);
        return response;
    }


    /**
     * 只读JSON响应
     *
     * @return ActionResponseRespVO
     */
    public static ActionResponseRespVO defJsonResponse(Boolean isShow, Boolean readOnly, JsonDataVO jsonDataVO) {

        ActionResponseRespVO response = new ActionResponseRespVO();
        response.setIsShow(isShow);
        response.setReadOnly(readOnly);
        response.setType(AppStepResponseTypeEnum.JSON.name());
        response.setStyle(AppStepResponseStyleEnum.JSON.name());
        response.setOutput(jsonDataVO);
        return response;
    }
}
