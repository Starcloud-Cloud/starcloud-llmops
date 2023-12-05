package com.starcloud.ops.business.app.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 创作中心异常码常量
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public interface CreativeErrorCodeConstants {

    /**
     * 创作计划不存在
     */
    ErrorCode PLAN_NOT_EXIST = new ErrorCode(350100110, "创作计划不存在（{}）！请稍后重试或者联系管理员！");

    /**
     * 创作计划配置不能为空
     */
    ErrorCode PLAN_CONFIG_NOT_NULL = new ErrorCode(350100111, "创作计划配置不能为空（{}）！请稍后重试或者联系管理员！");

    /**
     * 创作计划配置不能为空
     */
    ErrorCode PLAN_TYPE_REQUIRED = new ErrorCode(350100112, "创作计划类型是必须的！请稍后重试或者联系管理员！");

    /**
     * 创作计划类型不支持
     */
    ErrorCode PLAN_TYPE_NOT_SUPPORTED = new ErrorCode(350100113, "创作计划类型不支持（{}）！请稍后重试或者联系管理员！");

    /**
     * 创作计划UID不能为空
     */
    ErrorCode PLAN_UID_REQUIRED = new ErrorCode(350100114, "创作计划UID是必须的！请稍后重试或者联系管理员！");

    /**
     * 只有待执行的创作计划才允许修改
     */
    ErrorCode PLAN_STATUS_NOT_SUPPORT_MODIFY = new ErrorCode(350100115, "当前计划不支持修改，只有待执行的创作计划才允许修改！");

    /**
     * 图片素材不能为空
     */
    ErrorCode PLAN_UPLOAD_IMAGE_EMPTY = new ErrorCode(350100116, "图片素材不能为空！请上传图片素材后重试！");

    /**
     * 应用不存在
     */
    ErrorCode PLAN_APP_NOT_EXIST = new ErrorCode(350100118, "文案执行应用不存在！请联系管理员！");

    /**
     * 不支持顺序模式
     */
    ErrorCode PLAN_RANDOM_TYPE_NOT_SUPPORTED = new ErrorCode(350100123, "不支持顺序模式！请选择全部随机后重试！");

    /**
     * 创作计划状态不能为空
     */
    ErrorCode PLAN_STATUS_REQUIRED = new ErrorCode(350100125, "创作计划状态是必须的！请稍后重试或者联系管理员！");

    /**
     * 创作计划状态不支持
     */
    ErrorCode PLAN_STATUS_NOT_SUPPORTED = new ErrorCode(350100126, "创作计划状态不支持（{}）！请稍后重试或者联系管理员！");

    /**
     * 创作计划名称已存在
     */
    ErrorCode PLAN_NAME_EXIST = new ErrorCode(350100127, "创作计划名称已存在（{}），请重新输入！");

    /**
     * 创作计划名称已存在
     */
    ErrorCode PLAN_SCHEME_NOT_EMPTY = new ErrorCode(350100128, "创作计划的创作方案不能为空，请检查您的创作计划是否选择创作方案！");

    /**
     * 创作方案不存在
     */
    ErrorCode PLAN_SCHEME_NOT_EXIST = new ErrorCode(350100129, "创作方案不存在，请重新选择创作方案后重试！");

    /**
     * 创作方案不存在
     */
    ErrorCode SCHEME_NOT_EXIST = new ErrorCode(350200110, "创作方案不存在，请重新输入！");

    /**
     * 创作方案名称不能为空
     */
    ErrorCode SCHEME_NAME_REQUIRED = new ErrorCode(350200110, "创作方案名称不能为空，请重新输入！");

    /**
     * 创作计划名称已存在
     */
    ErrorCode SCHEME_NAME_EXIST = new ErrorCode(350100127, "创作方案名称已存在，请重新输入！");

    /**
     * 创作方案类目不能为空
     */
    ErrorCode SCHEME_CATEGORY_REQUIRED = new ErrorCode(350200110, "创作方案类目不能为空，请重新输入！");

    /**
     * 创作方案的参考内容不能为空
     */
    ErrorCode SCHEME_REFERS_NOT_EMPTY = new ErrorCode(350200115, "创作方案的参考内容不能为空，请检该创作方案后重试（创作方案名称: {}）！");

    /**
     * 创作方案配置不能为空
     */
    ErrorCode SCHEME_CONFIGURATION_NOT_NULL = new ErrorCode(350200111, "创作方案模板配置不能为空！创作方案名称: {}）！");

    /**
     * 创作方案文案模板不能为空
     */
    ErrorCode SCHEME_COPY_WRITING_TEMPLATE_NOT_NULL = new ErrorCode(350200112, "创作方案文案模板不能为空！创作方案名称: {}）！");

    /**
     * 创作方案文案模板总结信息不能为空
     */
    ErrorCode SCHEME_COPY_WRITING_TEMPLATE_SUMMARY_REQUIRED = new ErrorCode(350200112, "创作方案文案模板总结信息不能为空！创作方案名称: {}）！");

    /**
     * 创作方案文案模板要求不能为空
     */
    ErrorCode SCHEME_COPY_WRITING_TEMPLATE_DEMAND_REQUIRED = new ErrorCode(350200112, "创作方案文案模板要求不能为空！创作方案名称: {}）！");

    /**
     * 创作方案图片模板不能为空
     */
    ErrorCode SCHEME_IMAGE_TEMPLATE_NOT_NULL = new ErrorCode(350200113, "创作方案图片模板不能为空！创作方案名称: {}）!");

    /**
     * 创作方案图片模板风格不能为空
     */
    ErrorCode SCHEME_IMAGE_TEMPLATE_STYLE_LIST_NOT_EMPTY = new ErrorCode(350200114, "创作方案图片模板风格不能为空！创作方案名称: {}）!");

    /**
     * 创作方案图片模板风格的模板列表不能为空
     */
    ErrorCode SCHEME_IMAGE_TEMPLATE_STYLE_TEMPLATE_LIST_NOT_EMPTY = new ErrorCode(350200116, "创作方案图片模板风格的模板列表不能为空！风格名称: {}）!");

    /**
     * 生成文案示例失败
     */
    ErrorCode SCHEME_EXAMPLE_FAILURE = new ErrorCode(350200116, "生成文案示例失败, 请稍候重试！{}");

    /**
     * 图片模板ID不能为空
     */
    ErrorCode POSTER_ID_REQUIRED = new ErrorCode(750100111, "图片模板ID不能为空！");

    /**
     * 图片模板参数不能为空
     */
    ErrorCode POSTER_PARAMS_REQUIRED = new ErrorCode(750100112, "图片模板参数不能为空！");

    /**
     * 不支持的图片模板或者图片模板不存在
     */
    ErrorCode POSTER_NOT_SUPPORTED = new ErrorCode(750100113, "不支持的图片模板或者图片模板不存在({})！");

    /**
     * 该风格下的图片模板不能为空
     */
    ErrorCode STYLE_IMAGE_TEMPLATE_NOT_EMPTY = new ErrorCode(750100211, "该风格下的图片模板不能为空！({})！");

    /**
     * 图片生成响应不能为空
     */
    ErrorCode CREATIVE_IMAGE_RESPONSE_NOT_NULL = new ErrorCode(750100311, "创作中心：图片生成响应不能为空！");

    /**
     * 应用执行结果不存在
     */
    ErrorCode CREATIVE_APP_EXECUTE_RESULT_NOT_EXIST = new ErrorCode(750100312, "应用执行结果内容不存在，请稍后重试或者联系管理员（{}）！");

    /**
     * 应用执行结果不存在
     */
    ErrorCode CREATIVE_APP_EXECUTE_RESULT_FORMAT_ERROR = new ErrorCode(750100313, "执行结果格式不正确！，请稍后重试或者联系管理员（{}）！");
}
