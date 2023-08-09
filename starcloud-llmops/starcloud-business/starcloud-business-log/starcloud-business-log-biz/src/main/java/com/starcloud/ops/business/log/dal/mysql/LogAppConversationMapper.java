package com.starcloud.ops.business.log.dal.mysql;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.core.mybatis.query.MPJLambdaWrapperX;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationExportReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageFeedbacksDO;
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
     * @param reqVO 查询条件
     * @return 应用执行日志会话列表
     */
    default List<LogAppConversationDO> selectList(LogAppConversationExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<LogAppConversationDO>()
                .eqIfPresent(LogAppConversationDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppConversationDO::getAppUid, reqVO.getAppUid())
                .eqIfPresent(LogAppConversationDO::getAppName, reqVO.getAppName())
                .eqIfPresent(LogAppConversationDO::getAppMode, reqVO.getAppMode())
                .eqIfPresent(LogAppConversationDO::getAppConfig, reqVO.getAppConfig())
                .eqIfPresent(LogAppConversationDO::getStatus, reqVO.getStatus())
                .eqIfPresent(LogAppConversationDO::getFromScene, reqVO.getFromScene())
                .eqIfPresent(LogAppConversationDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppConversationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppConversationDO::getId));
    }

    /**
     * 查询应用执行日志会话分页
     *
     * @param reqVO 查询条件
     * @return 应用执行日志会话分页
     */
    default PageResult<LogAppConversationDO> selectPage(LogAppConversationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LogAppConversationDO>()
                .eqIfPresent(LogAppConversationDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppConversationDO::getAppUid, reqVO.getAppUid())
                .eqIfPresent(LogAppConversationDO::getAppName, reqVO.getAppName())
                .eqIfPresent(LogAppConversationDO::getAppMode, reqVO.getAppMode())
                .eqIfPresent(LogAppConversationDO::getAppConfig, reqVO.getAppConfig())
                .eqIfPresent(LogAppConversationDO::getStatus, reqVO.getStatus())
                .eqIfPresent(LogAppConversationDO::getFromScene, reqVO.getFromScene())
                .eqIfPresent(LogAppConversationDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppConversationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppConversationDO::getId));
    }

    /**
     * 分页查询应用执行日志会话
     *
     * @param reqVO 查询条件
     * @return 应用执行日志会话分页
     */
    default PageResult<LogAppConversationInfoPO> selectPage(LogAppConversationInfoPageReqVO reqVO) {

        MPJLambdaWrapperX<LogAppConversationDO> lambdaWrapperX = (MPJLambdaWrapperX<LogAppConversationDO>) new MPJLambdaWrapperX<LogAppConversationDO>()

                .select(LogAppConversationDO::getUid, LogAppConversationDO::getAppUid, LogAppConversationDO::getAppName, LogAppConversationDO::getAppMode, LogAppConversationDO::getFromScene,
                        LogAppConversationDO::getStatus, LogAppConversationDO::getCreator, LogAppConversationDO::getEndUser, LogAppConversationDO::getCreateTime)

                .selectCount(LogAppMessageDO::getId, LogAppConversationInfoPO::getMessageCount)
                .selectCount(LogAppMessageFeedbacksDO::getId, LogAppConversationInfoPO::getFeedbacksCount)
                .selectSum(LogAppMessageDO::getElapsed, LogAppConversationInfoPO::getTotalElapsed)
                .selectSum(LogAppMessageDO::getTotalPrice, LogAppConversationInfoPO::getTotalPrice)

                .selectSum(LogAppMessageDO::getMessageTokens, LogAppConversationInfoPO::getTotalMessageTokens)
                .selectSum(LogAppMessageDO::getAnswerTokens, LogAppConversationInfoPO::getTotalAnswerTokens)

                .leftJoin(LogAppMessageDO.class, LogAppMessageDO::getAppConversationUid, LogAppConversationDO::getUid)
                .leftJoin(LogAppMessageFeedbacksDO.class, LogAppMessageFeedbacksDO::getAppMessageUid, LogAppMessageDO::getUid)

                .eq(ObjectUtil.isNotEmpty(reqVO.getAppUid()), LogAppConversationDO::getAppUid, reqVO.getAppUid())
                .eq(ObjectUtil.isNotEmpty(reqVO.getAppName()), LogAppConversationDO::getAppName, reqVO.getAppName())

                .eq(ObjectUtil.isNotEmpty(reqVO.getStatus()), LogAppConversationDO::getStatus, reqVO.getStatus())
                .eq(ObjectUtil.isNotEmpty(reqVO.getFromScene()), LogAppConversationDO::getFromScene, reqVO.getFromScene())
                .eq(ObjectUtil.isNotEmpty(reqVO.getUser()), LogAppConversationDO::getCreator, reqVO.getUser())
                .eq(ObjectUtil.isNotEmpty(reqVO.getEndUser()), LogAppConversationDO::getEndUser, reqVO.getEndUser());

        lambdaWrapperX.betweenIfPresent(LogAppConversationDO::getCreateTime, reqVO.getStartTime(), reqVO.getEndTime())
                .orderByDesc(LogAppConversationDO::getId)
                .groupBy(LogAppConversationDO::getId);

        IPage<LogAppConversationInfoPO> mpPage = this.selectJoinPage(new Page<>(reqVO.getPageNo(), reqVO.getPageSize()), LogAppConversationInfoPO.class, lambdaWrapperX);

        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());

    }

    /**
     * 分页查询会话统计列表
     *
     * @param page  分页参数
     * @param reqVO 查询参数
     * @return 会话统计列表
     */
    IPage<LogAppConversationInfoPO> selectSqlPage(IPage<LogAppConversationDO> page, @Param("req") LogAppConversationInfoPageReqVO reqVO);

}