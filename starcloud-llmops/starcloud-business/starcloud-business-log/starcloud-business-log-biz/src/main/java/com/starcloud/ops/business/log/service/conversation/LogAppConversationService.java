package com.starcloud.ops.business.log.service.conversation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageQuery;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationInfoPageAppUidReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationExportReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationStatusReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationUpdateReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessageStatisticsListAppUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;

/**
 * 应用执行日志会话 Service 接口
 *
 * @author 芋道源码
 */
public interface LogAppConversationService {

    /**
     * 创建应用执行日志会话
     *
     * @param request 创建信息
     * @return 编号
     */
    Long createAppConversation(@Valid LogAppConversationCreateReqVO request);

    /**
     * 更新应用执行日志会话
     *
     * @param request 更新信息
     */
    void updateAppConversation(@Valid LogAppConversationUpdateReqVO request);

    /**
     * 更新应用执行日志会话状态
     *
     * @param uid    编号
     * @param status 状态
     */
    void updateAppConversationStatus(@NotEmpty(message = "会话 Uid 不能为空") String uid, @NotEmpty(message = "会话状态不能为空") String status);

    /**
     * 更新应用执行日志会话状态
     *
     * @param request 更新信息
     */
    void updateAppConversationStatus(@Valid LogAppConversationStatusReqVO request);

    /**
     * 删除应用执行日志会话
     *
     * @param id 编号
     */
    void deleteAppConversation(Long id);

    /**
     * 获得应用执行日志会话
     *
     * @param id 编号
     * @return 应用执行日志会话
     */
    LogAppConversationDO getAppConversation(Long id);

    /**
     * 获得应用执行日志会话
     *
     * @param uid 编号
     * @return 应用执行日志会话
     */
    LogAppConversationDO getAppConversation(String uid);

    /**
     * 获得应用执行日志会话列表
     *
     * @param ids 编号
     * @return 应用执行日志会话列表
     */
    List<LogAppConversationDO> getAppConversationList(Collection<Long> ids);

    /**
     * 获得应用执行日志会话分页
     *
     * @param pageReqVO 分页查询
     * @return 应用执行日志会话分页
     */
    PageResult<LogAppConversationDO> getAppConversationPage(LogAppConversationPageReqVO pageReqVO);

    /**
     * 获得应用执行日志会话列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 应用执行日志会话列表
     */
    List<LogAppConversationDO> getAppConversationList(LogAppConversationExportReqVO exportReqVO);

    /**
     * 获取用户最新会话
     *
     * @param appUid  应用uid
     * @param creator 用户uid
     * @return 应用执行日志会话
     */
    LogAppConversationDO getUserRecentlyConversation(String appUid, String creator, String scene);

    /**
     * 根据应用 UID 获取应用执行日志消息统计数据列表 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    List<LogAppMessageStatisticsListPO> listLogMessageStatisticsByAppUid(LogAppMessageStatisticsListAppUidReqVO query);

    /**
     * app message 统计列表数据
     *
     * @param query 查询条件
     * @return 应用执行日志会话列表
     */
    List<LogAppMessageStatisticsListPO> listLogMessageStatistics(LogAppMessageStatisticsListReqVO query);

    /**
     * 根据 应用 UID 分页查询应用执行日志会话数据 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 应用执行日志会话数据
     */
    PageResult<LogAppConversationInfoPO> pageLogConversationByAppUid(LogAppConversationInfoPageAppUidReqVO query);

    /**
     * 获取 应用执行分页信息
     *
     * @param query 分页查询
     * @return 应用执行日志会话分页
     */
    PageResult<LogAppConversationInfoPO> pageLogConversation(AppLogConversationInfoPageQuery query);

    /**
     * 获取最新的会话
     *
     * @param appUid 应用编号
     * @return 应用执行日志会话
     */
    LogAppConversationDO getRecentlyConversation(String appUid);

}