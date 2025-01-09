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
    ErrorCode PLAN_NOT_EXIST = new ErrorCode(710100110, "创作计划不存在（{}）！请稍后重试或者联系管理员！");

    /**
     * 创作计划配置不能为空
     */
    ErrorCode PLAN_CONFIG_NOT_NULL = new ErrorCode(710100111, "创作计划配置不能为空（{}）！请稍后重试或者联系管理员！");

    /**
     * 计划执行失败
     */
    ErrorCode PLAN_EXECUTE_FAILURE = new ErrorCode(710100111, "计划执行失败，请稍候重试！");
    /**
     * 创作计划UID不能为空
     */
    ErrorCode PLAN_UID_REQUIRED = new ErrorCode(710100114, "创作计划UID是必须的！请稍后重试或者联系管理员！");

    /**
     * 只有待执行的创作计划才允许修改
     */
    ErrorCode PLAN_STATUS_NOT_SUPPORT_MODIFY = new ErrorCode(710100115, "计划正在执行中，请稍后重试...");

    /**
     * 只有待执行的创作计划才允许修改
     */
    ErrorCode PLAN_STATUS_NOT_ALLOW_UPDATE = new ErrorCode(710100115, "计划状态不允许修改（{}）！");

    /**
     * 创作计划状态不能为空
     */
    ErrorCode PLAN_STATUS_REQUIRED = new ErrorCode(710100119, "创作计划状态是必须的！请稍后重试或者联系管理员！");

    /**
     * 创作计划状态不支持
     */
    ErrorCode PLAN_STATUS_NOT_SUPPORTED = new ErrorCode(710100120, "创作计划状态不支持（{}）！请稍后重试或者联系管理员！");

    /**
     * 创作计划名称已存在
     */
    ErrorCode PLAN_NAME_EXIST = new ErrorCode(710100121, "创作计划名称已存在（{}），请重新输入！");

    /**
     * 创作计划名称已存在
     */
    ErrorCode PLAN_SCHEME_NOT_EMPTY = new ErrorCode(710100122, "创作计划的创作方案不能为空，请检查您的创作计划是否选择创作方案！");

    /**
     * 创作方案不存在
     */
    ErrorCode PLAN_SCHEME_NOT_EXIST = new ErrorCode(710100123, "创作方案不存在，请重新选择创作方案后重试！");

    /**
     * 未发现首图
     */
    ErrorCode PLAN_IMAGE_MAIN_NOT_EXIST = new ErrorCode(710100124, "未发现首图！风格名称: {}）!");

    /**
     * 更新创作计划状态失败
     */
    ErrorCode PLAN_UPDATE_STATUS_FAILED = new ErrorCode(710100125, "更新创作计划状态失败，请稍后重试({})！{}!");

    /**
     * 创作计划正在执行中
     */
    ErrorCode PLAN_IS_EXECUTING = new ErrorCode(710100126, "创作计划正在执行中，请稍后重试 {}!");

    /**
     * 计划执行批次不存在
     */
    ErrorCode PLAN_BATCH_NOT_EXIST = new ErrorCode(710100127, "计划执行批次不存在 planUid = {} , batch = {}");


    /**
     * 创作方案不存在
     */
    ErrorCode SCHEME_NOT_EXIST = new ErrorCode(720100110, "创作方案不存在，请重新输入！");

    /**
     * 创作方案名称不能为空
     */
    ErrorCode SCHEME_NAME_REQUIRED = new ErrorCode(720100111, "创作方案名称不能为空，请重新输入！");

    /**
     * 创作计划名称已存在
     */
    ErrorCode SCHEME_NAME_EXIST = new ErrorCode(720100112, "创作方案名称已存在，请重新输入！");

    /**
     * 创作方案类目不能为空
     */
    ErrorCode SCHEME_CATEGORY_REQUIRED = new ErrorCode(720100113, "创作方案类目不能为空，请重新输入！");

    /**
     * 创作方案的参考内容不能为空
     */
    ErrorCode SCHEME_REFERS_NOT_EMPTY = new ErrorCode(720100114, "创作方案的参考内容不能为空，请检该创作方案后重试（创作方案名称: {}）！");

    /**
     * 创作方案配置不能为空
     */
    ErrorCode SCHEME_CONFIGURATION_NOT_NULL = new ErrorCode(720100115, "创作方案模板配置不能为空！创作方案名称: {}）！");

    /**
     * 创作方案应用UID不能为空
     */
    ErrorCode SCHEME_APP_UID_REQUIRED = new ErrorCode(720100115, "创作方案应用UID不能为空！创作方案名称: {}）！");

    /**
     * 创作方案文案模板不能为空
     */
    ErrorCode SCHEME_COPY_WRITING_TEMPLATE_NOT_NULL = new ErrorCode(720100116, "创作方案文案模板不能为空！创作方案名称: {}）！");

    /**
     * 创作方案文案模板总结信息不能为空
     */
    ErrorCode SCHEME_COPY_WRITING_TEMPLATE_SUMMARY_REQUIRED = new ErrorCode(720100117, "创作方案文案模板总结信息不能为空！创作方案名称: {}）！");

    /**
     * 创作方案文案模板要求不能为空
     */
    ErrorCode SCHEME_COPY_WRITING_TEMPLATE_DEMAND_REQUIRED = new ErrorCode(720100118, "创作方案文案模板要求不能为空！创作方案名称: {}）！");

    /**
     * 创作方案图片模板不能为空
     */
    ErrorCode SCHEME_IMAGE_TEMPLATE_NOT_NULL = new ErrorCode(720100119, "创作方案图片模板不能为空！创作方案名称: {}）!");

    /**
     * 创作方案图片模板风格不能为空
     */
    ErrorCode SCHEME_IMAGE_TEMPLATE_STYLE_LIST_NOT_EMPTY = new ErrorCode(720100120, "创作方案图片模板风格不能为空！创作方案名称: {}）!");

    /**
     * 创作方案图片模板风格的模板列表不能为空
     */
    ErrorCode SCHEME_IMAGE_TEMPLATE_STYLE_TEMPLATE_LIST_NOT_EMPTY = new ErrorCode(720100121, "创作方案图片模板风格的模板列表不能为空！风格名称: {}）!");

    /**
     * 生成文案示例失败
     */
    ErrorCode SCHEME_EXAMPLE_FAILURE = new ErrorCode(720100123, "生成文案示例失败, 请稍候重试！{}");

    //    ======= 素材库异常 =======
    ErrorCode MATERIAL_TYPE_NOT_EXIST = new ErrorCode(730000001, "素材类型不存在 [{}]");

    ErrorCode TYPE_UNMATCH_FIELD = new ErrorCode(730000002, "素材类型 {} 与上传的数据结构不匹配");

    ErrorCode MATERIAL_NOT_EXIST = new ErrorCode(730000003, "素材不存在 {}");

    ErrorCode MATERIAL_FIELD_NOT_VALID = new ErrorCode(730000004, "素材内容校验不通过: {}");

    ErrorCode MATERIAL_PARSE_ERROR = new ErrorCode(730000005, "素材解析异常: {}");

    ErrorCode UPLOAD_QUEUE_FULL = new ErrorCode(730000006, "素材上传队列已满，请稍后重试");

    ErrorCode NOT_ZIP_PACKAGE = new ErrorCode(730000007, "只支持上传zip、rar压缩包");

    ErrorCode DOWNLOAD_TEMPLATE_ERROR = new ErrorCode(730000008, "导出模板异常 {}");

    ErrorCode TEMP_IS_NOT_EXIST = new ErrorCode(730000009, "素材模版解析异常，请检测下载的模版文件名是否正确（不要修改目录名或留多余的空格），或重新下载模版修改后再次上传");

    ErrorCode EXCEL_REQUIRED_FILED = new ErrorCode(730000010, "excel中缺少必填字段[{}]");

    ErrorCode MATERIAL_STEP_NOT_EXIST = new ErrorCode(730000011, "上传素材步骤不存在");

    ErrorCode NO_MATERIAL_DEFINE = new ErrorCode(730000012, "上传素材步骤素材定义不存在");

    ErrorCode EXCEL_HEADER_REQUIRED_FILED = new ErrorCode(730000013, "上传素材excel表头缺少必填字段:{}");

    ErrorCode DUPLICATE_FILED_NAME = new ErrorCode(730000014, "上传素材步骤素材定义中存在重复的字段code:{}");

    ErrorCode DUPLICATE_FILED_DESC = new ErrorCode(730000015, "上传素材步骤素材定义中存在重复的字段名称:{}");

    ErrorCode FILED_TYPE_MISTAKE = new ErrorCode(730000016, "上传素材步骤素材定义中存在错误的字段类型:{}");

    ErrorCode NO_REQUIRED_FILED = new ErrorCode(730000017, "上传素材中缺少必填字段:{}");

    ErrorCode FILED_NAME_ERROR = new ErrorCode(730000018, "素材定义字段code必须是英文字母和数字组成:{}");

    ErrorCode FILED_DESC_IS_BLANK = new ErrorCode(730000019, "素材定义字段中文名不能为空");

    ErrorCode FILED_DESC_LENGTH = new ErrorCode(730000020, "素材定义字段中文名10个字符以内 {}");

    ErrorCode EXCEL_NOT_EXIST = new ErrorCode(730000021, "压缩包中未找到名为:导入模板.xlsx 的文件");

    ErrorCode IMAGES_NOT_EXIST = new ErrorCode(730000022, "压缩包中未找到images的文件夹");

    ErrorCode TOO_MANY_DOCUMENT = new ErrorCode(730000023, "只能包含一个文档类型的字段");


    /**
     * 图片模板ID不能为空
     */
    ErrorCode POSTER_ID_REQUIRED = new ErrorCode(750100111, "图片模板ID不能为空！");

    /**
     * 创作方案模式不能为空
     */
    ErrorCode SCHEME_MODE_REQUIRED = new ErrorCode(720100124, "创作方案模式不能为空！{}");

    /**
     * 创作方案模式不支持
     */
    ErrorCode SCHEME_MODE_NOT_SUPPORTED = new ErrorCode(720100125, "创作方案模式不支持（{}）！{}");

    /**
     * 图片模板参数不能为空
     */
    ErrorCode POSTER_PARAMS_REQUIRED = new ErrorCode(750100112, "图片模板参数不能为空！");

    /**
     * 不支持的图片模板或者图片模板不存在
     */
    ErrorCode POSTER_NOT_SUPPORTED = new ErrorCode(750100113, "包含段落步骤情况下，必须包含生成段落的图片模板！");

    /**
     * 图片模板不存在
     */
    ErrorCode POSTER_IMAGE_TEMPLATE_NOT_EXIST = new ErrorCode(750100113, "图片模板不存在(模板名称：{}！");

    /**
     * 海报错误：返回结果不存在！
     */
    ErrorCode POSTER_RESPONSE_IS_NULL = new ErrorCode(350700110, "{}：返回结果不存在！");

    /**
     * 海报错误
     */
    ErrorCode POSTER_RESPONSE_IS_NOT_SUCCESS = new ErrorCode(350700111, "{}：{}");

    /**
     * 海报错误：生成结果不存在！
     */
    ErrorCode POSTER_RESPONSE_DATA_IS_NULL = new ErrorCode(350700112, "{}：返回结果不存在！");

    /**
     * 海报错误：海报URL为空！请联系管理员！
     */
    ErrorCode POSTER_URL_IS_BLANK = new ErrorCode(350700114, "海报生成URL不存在！请联系管理员！");

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

    /**
     * 生成段落数和要求段落数不想等
     */
    ErrorCode PARAGRAPH_SIZE_NOT_EQUAL = new ErrorCode(750100314, "生成段落数和要求段落数不想等！");

    /**
     * 生成的段落标题不能为空
     */
    ErrorCode PARAGRAPH_TITLE_IS_NOT_BLANK = new ErrorCode(750100315, "生成的段落标题不能为空！");

    /**
     * 生成的段落内容不能为空
     */
    ErrorCode PARAGRAPH_CONTENT_IS_NOT_BLANK = new ErrorCode(750100316, "生成的段落内容不能为空！");

    /**
     * 段落数量使必填的
     */
    ErrorCode PARAGRAPH_COUNT_IS_REQUIRED = new ErrorCode(750100317, "段落数量使必填的！");


    /**
     * url地址不是图片
     */
    ErrorCode URL_IS_NOT_IMAGES = new ErrorCode(760100001, "url地址不是图片！{}");

    /**
     * 插件不存在
     */
    ErrorCode PLUGIN_NOT_EXIST = new ErrorCode(760100002, "插件不存在！{}");

    /**
     * 插件执行失败
     */
    ErrorCode PLUGIN_EXECUTE_ERROR = new ErrorCode(760100003, "插件执行失败！{}");

    /**
     * 插件配置错误
     */
    ErrorCode PLUGIN_CONFIG_ERROR = new ErrorCode(760100004, "插件配置错误！{}");


    ErrorCode NO_PERMISSIONS = new ErrorCode(760100005, "没有操作权限");


    ErrorCode NAME_DUPLICATE = new ErrorCode(760100006, "插件名称重复，{}");


    ErrorCode PLUGIN_CONFIG_NOT_EXIST = new ErrorCode(760100007, "插件配置不存在，libraryUid={},pluginUid={}");


    ErrorCode SYSTEM_PLUGIN = new ErrorCode(760100008, "系统插件不可删除");


    ErrorCode COZE_SERVICE_ERROR = new ErrorCode(760100009, "调用coze执行失败，请检查coze本身流程是否可执行");

    ErrorCode TOKEN_ERROR = new ErrorCode(760100010, "token不存在，id = {}");


    ErrorCode INPUT_JSON_ERROR = new ErrorCode(760100011, "输入参数应该是json对象，{}");

    ErrorCode OUTPUT_JSON_ERROR = new ErrorCode(760100012, "coze工作流输出参数应该是json对象或者集合对象，result={}");

    ErrorCode INPUT_OUTPUT_ERROR = new ErrorCode(760100013, "coze执行结果获取异常, {}");

    ErrorCode PLATFORM_NOT_SUPPORT = new ErrorCode(760100014, "不支持的插件类型, {}");

    ErrorCode EXECUTE_POOL_FULL = new ErrorCode(760100015, "线程池已满，请稍后重试");


    ErrorCode COZE_ERROR = new ErrorCode(760100016, "调用coze执行失败，{}");


    ErrorCode VIDEO_ERROR = new ErrorCode(760200001, "生成视频失败，{}");

    ErrorCode PARAM_ERROR = new ErrorCode(760200002, "生成视频拼装参数错误，{}");


}
