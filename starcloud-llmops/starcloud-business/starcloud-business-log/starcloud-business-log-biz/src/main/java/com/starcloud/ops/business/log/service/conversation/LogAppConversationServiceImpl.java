package com.starcloud.ops.business.log.service.conversation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationExportReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationUpdateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.dal.mysql.LogAppConversationMapper;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.log.enums.ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS;

/**
 * 应用执行日志会话 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class LogAppConversationServiceImpl implements LogAppConversationService {

    @Resource
    private LogAppConversationMapper appConversationMapper;

    @Resource
    private LogAppMessageMapper logAppMessageMapper;

    @Override
    public Long createAppConversation(LogAppConversationCreateReqVO createReqVO) {
        // 插入
        LogAppConversationDO appConversation = LogAppConversationConvert.INSTANCE.convert(createReqVO);
        appConversationMapper.insert(appConversation);
        // 返回
        return appConversation.getId();
    }

    @Override
    public void updateAppConversation(LogAppConversationUpdateReqVO updateReqVO) {
        // 校验存在
        // validateAppConversationExists(updateReqVO.getId());
        // 更新
        LogAppConversationDO updateObj = LogAppConversationConvert.INSTANCE.convert(updateReqVO);

        appConversationMapper.update(updateObj, Wrappers.lambdaQuery(LogAppConversationDO.class).eq(LogAppConversationDO::getUid, updateReqVO.getUid()));

    }

    /**
     * 更新应用执行日志会话状态
     *
     * @param uid    编号
     * @param status 状态
     */
    @Override
    public void updateAppConversationStatus(String uid, String status) {
        appConversationMapper.update(null, Wrappers.lambdaUpdate(LogAppConversationDO.class).eq(LogAppConversationDO::getUid, uid).set(LogAppConversationDO::getStatus, status));
    }

    @Override
    public void deleteAppConversation(Long id) {
        // 校验存在
        validateAppConversationExists(id);
        // 删除
        appConversationMapper.deleteById(id);
    }

    private void validateAppConversationExists(Long id) {
        if (appConversationMapper.selectById(id) == null) {
            throw exception(APP_CONVERSATION_NOT_EXISTS);
        }
    }

    @Override
    public LogAppConversationDO getAppConversation(Long id) {
        return appConversationMapper.selectById(id);
    }

    @Override
    public LogAppConversationDO getAppConversation(String uid) {

        return appConversationMapper.selectOne(LogAppConversationDO::getUid, uid);
    }

    @Override
    public List<LogAppConversationDO> getAppConversationList(Collection<Long> ids) {
        return appConversationMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<LogAppConversationDO> getAppConversationPage(LogAppConversationPageReqVO pageReqVO) {
        return appConversationMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LogAppConversationDO> getAppConversationList(LogAppConversationExportReqVO exportReqVO) {
        return appConversationMapper.selectList(exportReqVO);
    }


    /**
     * 查询应用执行日志信息接口
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<LogAppConversationInfoPO> getAppConversationInfoPage(LogAppConversationInfoPageReqVO pageReqVO) {

        //appConversationMapper.selectPage(pageReqVO);
        Page<LogAppConversationDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        IPage<LogAppConversationInfoPO> infoPOIPage = appConversationMapper.selectSqlPage(page, pageReqVO);

        return new PageResult<>(infoPOIPage.getRecords(), infoPOIPage.getTotal());
    }


    @Override
    public List<LogAppMessageStatisticsListPO> getAppMessageStatisticsList(LogAppMessageStatisticsListReqVO statisticsListReqVO) {

        String timeType = statisticsListReqVO.getTimeType();
        statisticsListReqVO.setStartTime(LogTimeTypeEnum.getStartTimeByType(timeType));
        statisticsListReqVO.setEndTime(LogTimeTypeEnum.getEndTimeByType(timeType));
        List<LogAppMessageStatisticsListPO> statisticsList = logAppMessageMapper.getAppMessageStatisticsList(statisticsListReqVO);
        LogAppMessageStatisticsListPO logAppMessageStatisticsListPO = statisticsList.get(statisticsList.size() - 1);
        String nowDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // 如果最后一条数据不是今天的数据，就添加一条今天的数据
        if (!nowDate.equals(logAppMessageStatisticsListPO.getCreateDate())) {
            LogAppMessageStatisticsListPO nowStatistics = new LogAppMessageStatisticsListPO();
            nowStatistics.setAppUid(logAppMessageStatisticsListPO.getAppUid());
            nowStatistics.setAppMode(logAppMessageStatisticsListPO.getAppMode());
            nowStatistics.setAppName(logAppMessageStatisticsListPO.getAppName());
            nowStatistics.setFromScene(logAppMessageStatisticsListPO.getFromScene());
            nowStatistics.setMessageCount(0);
            nowStatistics.setSuccessCount(0);
            nowStatistics.setErrorCount(0);
            nowStatistics.setUserCount(0);
            nowStatistics.setElapsedTotal(new BigDecimal("0"));
            nowStatistics.setElapsedAvg(new BigDecimal("0"));
            nowStatistics.setMessageTokens(0);
            nowStatistics.setAnswerTokens(0);
            nowStatistics.setTokens(0);
            nowStatistics.setCreateDate(nowDate);
            statisticsList.add(nowStatistics);
        }

        return statisticsList;

    }
}