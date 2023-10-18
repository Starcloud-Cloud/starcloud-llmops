package com.starcloud.ops.business.log.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageListReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.enums.LogMessageTypeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 应用执行日志结果 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppMessageMapper extends BaseMapperX<LogAppMessageDO> {

    /**
     * 获得应用执行日志结果分页
     *
     * @param query 查询条件
     * @return 应用执行日志结果分页
     */
    default PageResult<LogAppMessageDO> selectPage(LogAppMessagePageReqVO query) {
        return selectPage(query, new LambdaQueryWrapperX<LogAppMessageDO>()
                .eqIfPresent(LogAppMessageDO::getUid, query.getUid())
                .eqIfPresent(LogAppMessageDO::getAppConversationUid, query.getAppConversationUid())
                .eqIfPresent(LogAppMessageDO::getAppUid, query.getAppUid())
                .eqIfPresent(LogAppMessageDO::getAppMode, query.getAppMode())
                .eqIfPresent(LogAppMessageDO::getFromScene, query.getFromScene())
                .eqIfPresent(LogAppMessageDO::getAiModel, query.getAiModel())
                .eqIfPresent(LogAppMessageDO::getCreator, query.getCreator())
                .eqIfPresent(LogAppMessageDO::getStatus, query.getStatus())
                .orderByDesc(LogAppMessageDO::getId));
    }

    /**
     * 根据会话编号，获得应用执行日志结果列表
     *
     * @param appConversationUidList 会话编号列表
     * @return 应用执行日志结果列表
     */
    default List<LogAppMessageDO> listAppLogMessageByAppConversationUidList(Collection<String> appConversationUidList) {
        return selectList(new LambdaQueryWrapperX<LogAppMessageDO>()
                .in(LogAppMessageDO::getAppConversationUid, appConversationUidList)
                .orderByDesc(LogAppMessageDO::getId));
    }

    /**
     * 排除系统总结场景
     *
     * @param query 分页查询
     * @return 应用执行日志结果分页
     */
    default PageResult<LogAppMessageDO> pageUserMessage(LogAppMessagePageReqVO query) {
        return selectPage(query, new LambdaQueryWrapperX<LogAppMessageDO>()
                .eqIfPresent(LogAppMessageDO::getUid, query.getUid())
                .eqIfPresent(LogAppMessageDO::getAppConversationUid, query.getAppConversationUid())
                .eqIfPresent(LogAppMessageDO::getAppUid, query.getAppUid())
                .eqIfPresent(LogAppMessageDO::getAppMode, query.getAppMode())
                .eqIfPresent(LogAppMessageDO::getFromScene, query.getFromScene())
                .eqIfPresent(LogAppMessageDO::getAiModel, query.getAiModel())
                .eqIfPresent(LogAppMessageDO::getCreator, query.getCreator())
                .eqIfPresent(LogAppMessageDO::getStatus, query.getStatus())
                .ne(LogAppMessageDO::getFromScene, "SYSTEM_SUMMARY")
                .ne(LogAppMessageDO::getMsgType, LogMessageTypeEnum.SUMMARY.name())
                .orderByDesc(LogAppMessageDO::getId));
    }

    /**
     * 获得应用执行日志结果列表
     *
     * @param query 查询条件
     * @return 应用执行日志结果列表
     */
    default List<LogAppMessageDO> selectList(LogAppMessageListReqVO query) {
        return selectList(new LambdaQueryWrapperX<LogAppMessageDO>()
                .eqIfPresent(LogAppMessageDO::getUid, query.getUid())
                .eqIfPresent(LogAppMessageDO::getAppConversationUid, query.getAppConversationUid())
                .eqIfPresent(LogAppMessageDO::getAppUid, query.getAppUid())
                .eqIfPresent(LogAppMessageDO::getAppMode, query.getAppMode())
                .eqIfPresent(LogAppMessageDO::getFromScene, query.getFromScene())
                .eqIfPresent(LogAppMessageDO::getAiModel, query.getAiModel())
                .eqIfPresent(LogAppMessageDO::getCreator, query.getCreator())
                .eqIfPresent(LogAppMessageDO::getStatus, query.getStatus())
                .orderByDesc(LogAppMessageDO::getId));
    }

    /**
     * 获得应用执行日志消息统计列表
     *
     * @param query 查询条件
     * @return 应用执行日志消息统计列表
     */
    List<LogAppMessageStatisticsListPO> listLogAppMessageStatistics(@Param("query") AppLogMessageStatisticsListReqVO query);

    /**
     * 根据应用 UID 获取应用执行日志消息统计数据列表
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    List<LogAppMessageStatisticsListPO> listLogAppMessageStatisticsByAppUid(@Param("query") AppLogMessageStatisticsListUidReqVO query);

}