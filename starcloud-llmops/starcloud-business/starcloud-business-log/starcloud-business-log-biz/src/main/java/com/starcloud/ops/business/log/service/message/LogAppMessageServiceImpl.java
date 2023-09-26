package com.starcloud.ops.business.log.service.message;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageExportReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageUpdateReqVO;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import com.starcloud.ops.business.log.enums.LogMessageTypeEnum;
import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.log.enums.ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS;

/**
 * 应用执行日志结果 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class LogAppMessageServiceImpl implements LogAppMessageService {

    @Resource
    private LogAppMessageMapper logAppMessageMapper;

    /**
     * 创建应用执行日志结果
     *
     * @param request 创建信息
     * @return 编号
     */
    @Override
    public Long createAppLogMessage(LogAppMessageCreateReqVO request) {
        LogAppMessageDO appMessage = LogAppMessageConvert.INSTANCE.convert(request);
        appMessage.setCreator(request.getCreator());
        appMessage.setUpdater(request.getCreator());
        appMessage.setCreateTime(request.getCreateTime());
        appMessage.setUpdateTime(request.getUpdateTime());
        appMessage.setTenantId(request.getTenantId());
        logAppMessageMapper.insert(appMessage);
        return appMessage.getId();
    }

    /**
     * 更新应用执行日志结果
     *
     * @param request 更新信息
     */
    @Override
    public void updateAppLogMessage(LogAppMessageUpdateReqVO request) {
        // 校验存在
        validateAppMessageExists(request.getId());
        // 更新
        LogAppMessageDO updateObj = LogAppMessageConvert.INSTANCE.convert(request);
        logAppMessageMapper.updateById(updateObj);
    }

    /**
     * 删除应用执行日志结果
     *
     * @param id 编号
     */
    @Override
    public void deleteAppLogMessage(Long id) {
        // 校验存在
        validateAppMessageExists(id);
        // 删除
        logAppMessageMapper.deleteById(id);
    }

    /**
     * 获得应用执行日志结果
     *
     * @param id 编号
     * @return 应用执行日志结果
     */
    @Override
    public LogAppMessageDO getAppLogMessage(Long id) {
        return logAppMessageMapper.selectById(id);
    }

    /**
     * 获得应用执行日志结果
     *
     * @param uid 编号
     * @return 应用执行日志结果
     */
    @Override
    public LogAppMessageDO getAppLogMessage(String uid) {
        return logAppMessageMapper.selectOne(LogAppMessageDO::getUid, uid);
    }

    /**
     * 获得应用执行日志结果列表
     *
     * @param ids 编号
     * @return 应用执行日志结果列表
     */
    @Override
    public List<LogAppMessageDO> listAppLogMessage(Collection<Long> ids) {
        return logAppMessageMapper.selectBatchIds(ids);
    }

    /**
     * 获得应用执行日志结果列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 应用执行日志结果列表
     */
    @Override
    public List<LogAppMessageDO> listAppLogMessage(LogAppMessageExportReqVO exportReqVO) {
        return logAppMessageMapper.selectList(exportReqVO);
    }

    /**
     * 获得应用执行日志结果分页
     *
     * @param pageReqVO 分页查询
     * @return 应用执行日志结果分页
     */
    @Override
    public PageResult<LogAppMessageDO> pageAppLogMessage(LogAppMessagePageReqVO pageReqVO) {
        return logAppMessageMapper.selectPage(pageReqVO);
    }

    /**
     * 根据会话uid获取消息列表
     *
     * @param query 查询条件
     * @return 消息列表
     */
    @Override
    public Page<LogAppMessageDO> pageAppLogMessage(AppLogMessagePageReqVO query) {
        LambdaQueryWrapper<LogAppMessageDO> wrapper = Wrappers.lambdaQuery(LogAppMessageDO.class);
        wrapper.eq(LogAppMessageDO::getAppConversationUid, query.getConversationUid());
        wrapper.eq(StringUtils.isNotBlank(query.getAppMode()), LogAppMessageDO::getAppMode, query.getAppMode());
        wrapper.eq(StringUtils.isNotBlank(query.getFromScene()), LogAppMessageDO::getFromScene, query.getFromScene());
        wrapper.eq(StringUtils.isNotBlank(query.getStatus()), LogAppMessageDO::getStatus, query.getStatus());
        wrapper.orderByDesc(LogAppMessageDO::getCreateTime);
        Page<LogAppMessageDO> page = new Page<>(query.getPageNo(), query.getPageSize());
        return logAppMessageMapper.selectPage(page, wrapper);
    }

    /**
     * 排除系统总结场景
     *
     * @param reqVO 分页查询
     * @return 应用执行日志结果分页
     */
    @Override
    public PageResult<LogAppMessageDO> userMessagePage(LogAppMessagePageReqVO reqVO) {
        return logAppMessageMapper.selectPage(reqVO, new LambdaQueryWrapperX<LogAppMessageDO>()
                .eqIfPresent(LogAppMessageDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppMessageDO::getAppConversationUid, reqVO.getAppConversationUid())
                .eqIfPresent(LogAppMessageDO::getAppUid, reqVO.getAppUid())
                .eqIfPresent(LogAppMessageDO::getAppMode, reqVO.getAppMode())
                .eqIfPresent(LogAppMessageDO::getAppConfig, reqVO.getAppConfig())
                .eqIfPresent(LogAppMessageDO::getAppStep, reqVO.getAppStep())
                .eqIfPresent(LogAppMessageDO::getStatus, reqVO.getStatus())
                .eqIfPresent(LogAppMessageDO::getErrorCode, reqVO.getErrorCode())
                .eqIfPresent(LogAppMessageDO::getErrorMsg, reqVO.getErrorMsg())
                .eqIfPresent(LogAppMessageDO::getVariables, reqVO.getVariables())
                .eqIfPresent(LogAppMessageDO::getMessage, reqVO.getMessage())
                .eqIfPresent(LogAppMessageDO::getMessageTokens, reqVO.getMessageTokens())
                .eqIfPresent(LogAppMessageDO::getMessageUnitPrice, reqVO.getMessageUnitPrice())
                .eqIfPresent(LogAppMessageDO::getAnswer, reqVO.getAnswer())
                .eqIfPresent(LogAppMessageDO::getAnswerTokens, reqVO.getAnswerTokens())
                .eqIfPresent(LogAppMessageDO::getAnswerUnitPrice, reqVO.getAnswerUnitPrice())
                .eqIfPresent(LogAppMessageDO::getElapsed, reqVO.getElapsed())
                .eqIfPresent(LogAppMessageDO::getTotalPrice, reqVO.getTotalPrice())
                .eqIfPresent(LogAppMessageDO::getCurrency, reqVO.getCurrency())
                .eqIfPresent(LogAppMessageDO::getFromScene, reqVO.getFromScene())
                .eqIfPresent(LogAppMessageDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppMessageDO::getCreateTime, reqVO.getCreateTime())
                .ne(LogAppMessageDO::getFromScene, "SYSTEM_SUMMARY")
                .ne(LogAppMessageDO::getMsgType, LogMessageTypeEnum.SUMMARY.name())
                .orderByDesc(LogAppMessageDO::getId));
    }

    /**
     * 根据应用 UID 获取应用执行日志消息统计数据列表 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    @Override
    public List<LogAppMessageStatisticsListPO> listLogAppMessageStatistics(AppLogMessageStatisticsListUidReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(org.apache.commons.lang3.StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置日期单位
        query.setUnit(logTimeTypeEnum.getGroupUnit().name());
        // 设置开始时间和结束时间
        query.setStartTime(logTimeTypeEnum.getStartTime());
        query.setEndTime(logTimeTypeEnum.getEndTime());
        // 查询数据
        List<LogAppMessageStatisticsListPO> statisticsList = logAppMessageMapper.listLogAppMessageStatisticsByAppUid(query);
        // 填充数据
        return listLogAppMessageStatistics(statisticsList, logTimeTypeEnum);
    }

    /**
     * app message 统计列表数据
     *
     * @param query 查询条件
     * @return 应用执行日志会话列表
     */
    @Override
    public List<LogAppMessageStatisticsListPO> listLogAppMessageStatistics(AppLogMessageStatisticsListReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(org.apache.commons.lang3.StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置日期单位
        query.setUnit(logTimeTypeEnum.getGroupUnit().name());
        // 设置开始时间和结束时间
        query.setStartTime(logTimeTypeEnum.getStartTime());
        query.setEndTime(logTimeTypeEnum.getEndTime());
        // 查询数据
        List<LogAppMessageStatisticsListPO> statisticsList = logAppMessageMapper.listLogAppMessageStatistics(query);
        // 填充数据
        return listLogAppMessageStatistics(statisticsList, logTimeTypeEnum);
    }

    /**
     * 校验应用执行日志结果存在
     *
     * @param id 日志消息ID
     */
    private void validateAppMessageExists(Long id) {
        if (logAppMessageMapper.selectById(id) == null) {
            throw exception(APP_CONVERSATION_NOT_EXISTS);
        }
    }

    /**
     * 根据日志时间类型，填充数据
     *
     * @param statisticsList  数据
     * @param logTimeTypeEnum 日志时间类型
     * @return 填充后的数据
     */
    @NotNull
    private static List<LogAppMessageStatisticsListPO> listLogAppMessageStatistics(List<LogAppMessageStatisticsListPO> statisticsList,
                                                                                   LogTimeTypeEnum logTimeTypeEnum) {

        if (CollectionUtils.isEmpty(statisticsList)) {
            return Collections.emptyList();
        }
        // 生成获取时间范围。
        List<LocalDateTime> dateRange = LogTimeTypeEnum.dateTimeRange(logTimeTypeEnum);
        // 填充数据
        List<LogAppMessageStatisticsListPO> fillStatisticsList = new ArrayList<>();
        for (LocalDateTime localDateTime : dateRange) {
            // 格式化时间
            String formatDate = localDateTime.format(DateTimeFormatter.ofPattern(logTimeTypeEnum.getFormatByGroupUnit()));
            // 匹配是否存在
            Optional<LogAppMessageStatisticsListPO> logMessageStatisticsOptional = statisticsList.stream()
                    .filter(statistics -> formatDate.equals(statistics.getCreateDate())).findFirst();
            // 存在就添加，不存在就创建
            if (logMessageStatisticsOptional.isPresent()) {
                fillStatisticsList.add(logMessageStatisticsOptional.get());
            } else {
                fillStatisticsList.add(fillLogAppMessageStatistics(formatDate));
            }
        }

        // 处理并且返回数据
        return getStatisticsListStream(fillStatisticsList, logTimeTypeEnum)
                .sorted(Comparator.comparing(LogAppMessageStatisticsListPO::getCreateDate))
                .collect(Collectors.toList());
    }

    /**
     * 处理当天的数据
     *
     * @param fillStatisticsList 填充的数据
     * @param logTimeTypeEnum    日志时间类型
     * @return 处理后的数据
     */
    private static Stream<LogAppMessageStatisticsListPO> getStatisticsListStream(List<LogAppMessageStatisticsListPO> fillStatisticsList, LogTimeTypeEnum logTimeTypeEnum) {
        Stream<LogAppMessageStatisticsListPO> statisticsListStream = CollectionUtil.emptyIfNull(fillStatisticsList).stream();
        if (Objects.equals(logTimeTypeEnum, LogTimeTypeEnum.TODAY)) {
            statisticsListStream = statisticsListStream.peek(item -> {
                String createDate = item.getCreateDate();
                LocalDateTime localDateTime = LocalDateTime.parse(createDate, DateTimeFormatter.ofPattern(LogTimeTypeEnum.TODAY.getFormatByGroupUnit()));
                item.setCreateDate(localDateTime.format(DateTimeFormatter.ofPattern("HH")));
            });
        }
        return statisticsListStream;
    }

    /**
     * 填充一条数据
     *
     * @param date 日期
     * @return 填充的数据
     */
    @NotNull
    private static LogAppMessageStatisticsListPO fillLogAppMessageStatistics(String date) {
        LogAppMessageStatisticsListPO fillStatistics = new LogAppMessageStatisticsListPO();
        fillStatistics.setMessageCount(0);
        fillStatistics.setSuccessCount(0);
        fillStatistics.setErrorCount(0);
        fillStatistics.setUserCount(0);
        fillStatistics.setElapsedTotal(new BigDecimal("0"));
        fillStatistics.setElapsedAvg(new BigDecimal("0"));
        fillStatistics.setMessageTokens(0);
        fillStatistics.setAnswerTokens(0);
        fillStatistics.setTokens(0);
        fillStatistics.setCreateDate(date);
        return fillStatistics;
    }

}