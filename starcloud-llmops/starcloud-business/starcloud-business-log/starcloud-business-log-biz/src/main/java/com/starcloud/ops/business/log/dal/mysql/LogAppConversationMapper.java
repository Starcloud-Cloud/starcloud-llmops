package com.starcloud.ops.business.log.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageUidReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationListReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListUidReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用执行日志会话 Mapper
 *
 * @author admin
 * @version 1.0.0
 * @since 2023-07-30
 */
@Mapper
public interface LogAppConversationMapper extends BaseMapperX<LogAppConversationDO> {

    /**
     * 查询应用执行日志会话列表
     *
     * @param query 查询条件
     * @return 应用执行日志会话列表
     */
    default List<LogAppConversationDO> selectList(LogAppConversationListReqVO query) {
        return selectList(new LambdaQueryWrapperX<LogAppConversationDO>()
                .eqIfPresent(LogAppConversationDO::getUid, query.getUid())
                .eqIfPresent(LogAppConversationDO::getAppUid, query.getAppUid())
                .eqIfPresent(LogAppConversationDO::getAppName, query.getAppName())
                .eqIfPresent(LogAppConversationDO::getAppMode, query.getAppMode())
                .eqIfPresent(LogAppConversationDO::getStatus, query.getStatus())
                .eqIfPresent(LogAppConversationDO::getFromScene, query.getFromScene())
                .inIfPresent(LogAppConversationDO::getFromScene, query.getFromSceneList())
                .eqIfPresent(LogAppConversationDO::getAiModel, query.getAiModel())
                .eqIfPresent(LogAppConversationDO::getCreator, query.getCreator())
                .orderByDesc(LogAppConversationDO::getId));
    }

    /**
     * 查询应用执行日志会话分页
     *
     * @param query 查询条件
     * @return 应用执行日志会话分页
     */
    default PageResult<LogAppConversationDO> selectPage(LogAppConversationPageReqVO query) {
        return selectPage(query, new LambdaQueryWrapperX<LogAppConversationDO>()
                .eqIfPresent(LogAppConversationDO::getUid, query.getUid())
                .eqIfPresent(LogAppConversationDO::getAppUid, query.getAppUid())
                .eqIfPresent(LogAppConversationDO::getAppName, query.getAppName())
                .eqIfPresent(LogAppConversationDO::getAppMode, query.getAppMode())
                .eqIfPresent(LogAppConversationDO::getFromScene, query.getFromScene())
                .inIfPresent(LogAppConversationDO::getFromScene, query.getScenes())
                .eqIfPresent(LogAppConversationDO::getAiModel, query.getAiModel())
                .eqIfPresent(LogAppConversationDO::getStatus, query.getStatus())
                .eqIfPresent(LogAppConversationDO::getCreator, query.getCreator())
                .orderByDesc(LogAppConversationDO::getId));
    }

    /**
     * 分页查询会话统计列表
     *
     * @param page  分页参数
     * @param query 查询参数
     * @return 会话统计列表
     */
    IPage<LogAppConversationInfoPO> pageLogAppConversation(IPage<LogAppConversationDO> page, @Param("query") AppLogConversationInfoPageReqVO query);

    /**
     * 根据应用 UID 分页查询应用执行日志会话数据 <br>
     *
     * @param page  分页参数
     * @param query 查询参数
     * @return 应用执行日志会话数据
     */
    IPage<LogAppConversationInfoPO> pageLogAppConversationByAppUid(Page<LogAppConversationDO> page, @Param("query") AppLogConversationInfoPageUidReqVO query);

    List<LogAppMessageStatisticsListPO> listLogAppConversationStatistics(@Param("query") AppLogMessageStatisticsListReqVO query);

    List<LogAppMessageStatisticsListPO> listLogAppConversationStatisticsByAppUid(@Param("query") AppLogMessageStatisticsListUidReqVO query);

    List<LogAppMessageStatisticsListPO> listRightsStatistics(@Param("query") AppLogMessageStatisticsListReqVO query);

    List<LogAppMessageStatisticsListPO> listRightsStatisticsByAppUid(@Param("query") AppLogMessageStatisticsListUidReqVO query);
}