package com.starcloud.ops.business.app.service.log;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.log.vo.request.AppLogMessageQuery;
import com.starcloud.ops.business.app.api.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.ImageLogMessageRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageAppUidReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppMessageStatisticsListVO;
import com.starcloud.ops.business.log.api.message.vo.AppLogMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListAppUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
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
     * 根据应用 UID 获取应用执行日志消息统计数据列表 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    List<LogAppMessageStatisticsListVO> listLogMessageStatisticsByAppUid(LogAppMessageStatisticsListAppUidReqVO query);

    /**
     * 获取应用执行日志消息统计数据列表 <br>
     * 1. 生成记录 <br>
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    List<LogAppMessageStatisticsListVO> listLogMessageStatistics(LogAppMessageStatisticsListReqVO query);

    /**
     * 根据应用市场 UID 分页查询应用执行日志会话数据 <br>
     *
     * @param query 查询条件
     * @return 应用市场执行日志会话数据
     */
    PageResult<AppLogMessageRespVO> pageLogConversationByMarketUid(LogAppConversationInfoPageAppUidReqVO query);

    /**
     * 根据 应用 UID 分页查询应用执行日志会话数据 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 应用执行日志会话数据
     */
    PageResult<LogAppConversationInfoRespVO> pageLogConversationByAppUid(LogAppConversationInfoPageAppUidReqVO query);

    /**
     * 分页查询应用执行日志会话数据
     * 1. 生成记录 <br>
     *
     * @param query 查询条件
     * @return 应用执行日志会话数据
     */
    PageResult<LogAppConversationInfoRespVO> pageLogConversation(LogAppConversationInfoPageReqVO query);

    /**
     * 获取文本生成消息详情
     *
     * @param query 查询条件
     * @return AppLogMessageRespVO
     */
    AppLogMessageRespVO getLogAppMessageDetail(AppLogMessagePageReqVO query);

    /**
     * 获取聊天详情
     *
     * @param query 查询条件
     * @return AppLogMessageRespVO
     */
    PageResult<AppLogMessageRespVO> getChatMessageDetail(AppLogMessagePageReqVO query);

    /**
     * 获取图片生成消息详情
     *
     * @param query 查询条件
     * @return ImageRespVO
     */
    ImageLogMessageRespVO getLogImageMessageDetail(AppLogMessagePageReqVO query);


}
