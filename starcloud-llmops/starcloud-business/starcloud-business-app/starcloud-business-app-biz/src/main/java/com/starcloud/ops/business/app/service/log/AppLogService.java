package com.starcloud.ops.business.app.service.log;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.controller.admin.log.vo.request.AppLogMessageQuery;
import com.starcloud.ops.business.app.controller.admin.log.vo.response.AppExecutedPromptRespVO;
import com.starcloud.ops.business.app.controller.admin.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.controller.admin.log.vo.response.ImageLogMessageRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageUidReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.response.AppLogConversationInfoRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.response.LogAppMessageStatisticsListVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageRespVO;
import com.starcloud.ops.framework.common.api.dto.Option;

import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-09
 */
public interface AppLogService {

    /**
     * 日志元数据
     *
     * @return 日志元数据
     */
    Map<String, List<Option>> logMetaData(String type);

    /**
     * 根据条件查询分页查询日志消息数量
     *
     * @param query 查询条件
     * @return 日志消息数量
     */
    Page<LogAppMessageRespVO> pageAppLogMessage(AppLogMessageQuery query);

    /**
     * 获取应用执行日志消息
     *
     * @param appMessageUid 应用执行日志uid
     * @return 应用执行日志列表
     */
    LogAppMessageInfoRespVO getAppMessageResult(String appMessageUid);

    /**
     * 获取应用执行日志消息统计数据列表 <br>
     * 1. 生成记录 <br>
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    List<LogAppMessageStatisticsListVO> listLogMessageStatistics(AppLogMessageStatisticsListReqVO query);

    /**
     * 根据应用 UID 获取应用执行日志消息统计数据列表 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    List<LogAppMessageStatisticsListVO> listLogMessageStatisticsByAppUid(AppLogMessageStatisticsListUidReqVO query);

    /**
     * 分页查询应用执行日志会话数据
     * 1. 生成记录 <br>
     *
     * @param query 查询条件
     * @return 应用执行日志会话数据
     */
    PageResult<AppLogConversationInfoRespVO> pageLogConversation(AppLogConversationInfoPageReqVO query);

    /**
     * 根据应用市场 UID 分页查询应用执行日志会话数据 <br>
     *
     * @param query 查询条件
     * @return 应用市场执行日志会话数据
     */
    PageResult<AppLogMessageRespVO> pageLogConversationByMarketUid(AppLogConversationInfoPageUidReqVO query);

    /**
     * 根据 应用 UID 分页查询应用执行日志会话数据 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 应用执行日志会话数据
     */
    PageResult<AppLogConversationInfoRespVO> pageLogConversationByAppUid(AppLogConversationInfoPageUidReqVO query);

    /**
     * 分页查询应用执行日志消息数据
     *
     * @param query 查询条件
     * @return 应用执行日志消息数据
     */
    PageResult<ImageLogMessageRespVO> pageImageRecord(LogAppConversationPageReqVO query);

    /**
     * 获取文本生成消息详情
     *
     * @param query 查询条件
     * @return AppLogMessageRespVO
     */
    AppLogMessageRespVO getLogAppMessageDetail(LogAppMessagePageReqVO query);

    /**
     * 获取聊天详情
     *
     * @param query 查询条件
     * @return AppLogMessageRespVO
     */
    PageResult<AppLogMessageRespVO> getChatMessageDetail(LogAppMessagePageReqVO query);

    /**
     * 获取图片生成消息详情
     *
     * @param query 查询条件
     * @return ImageRespVO
     */
    ImageLogMessageRespVO getLogImageMessageDetail(LogAppMessagePageReqVO query);

    AppExecutedPromptRespVO getAppExecutedPrompt(LogAppMessagePageReqVO query);

}
