package com.starcloud.ops.business.log.service.message;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageListReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageUpdateReqVO;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
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
        appMessage.setDeptId(request.getDeptId());
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
        updateObj.setUpdateTime(LocalDateTime.now());
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
     * @param query 查询条件
     * @return 应用执行日志结果列表
     */
    @Override
    public List<LogAppMessageDO> listAppLogMessage(LogAppMessageListReqVO query) {
        return logAppMessageMapper.selectList(query);
    }

    /**
     * 获得应用执行日志结果列表, 根据 appConversationUid
     *
     * @param appConversationUidList appConversationUid 列表
     * @return 应用执行日志结果列表
     */
    @Override
    public List<LogAppMessageDO> listAppLogMessageByAppConversationUidList(Collection<String> appConversationUidList) {
        return logAppMessageMapper.listAppLogMessageByAppConversationUidList(appConversationUidList);
    }

    /**
     * 获得应用执行日志结果分页
     *
     * @param query 分页查询
     * @return 应用执行日志结果分页
     */
    @Override
    public PageResult<LogAppMessageDO> pageAppLogMessage(LogAppMessagePageReqVO query) {
        return logAppMessageMapper.selectPage(query);
    }

    /**
     * 排除系统总结场景
     *
     * @param query 分页查询
     * @return 应用执行日志结果分页
     */
    @Override
    public PageResult<LogAppMessageDO> userMessagePage(LogAppMessagePageReqVO query) {
        return logAppMessageMapper.pageUserMessage(query);
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
                fillStatisticsList.add(handlerAppMessageStatistics(logMessageStatisticsOptional.get()));
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

    private static LogAppMessageStatisticsListPO handlerAppMessageStatistics(LogAppMessageStatisticsListPO statistics) {
        statistics.setMessageCount(Optional.ofNullable(statistics.getMessageCount()).orElse(0));
        statistics.setSuccessCount(Optional.ofNullable(statistics.getSuccessCount()).orElse(0));
        statistics.setCompletionSuccessCount(Optional.ofNullable(statistics.getCompletionSuccessCount()).orElse(0));
        statistics.setImageSuccessCount(Optional.ofNullable(statistics.getImageSuccessCount()).orElse(0));
        statistics.setErrorCount(Optional.ofNullable(statistics.getErrorCount()).orElse(0));
        statistics.setCompletionErrorCount(Optional.ofNullable(statistics.getCompletionErrorCount()).orElse(0));
        statistics.setImageErrorCount(Optional.ofNullable(statistics.getImageErrorCount()).orElse(0));
        statistics.setFeedbackLikeCount(Optional.ofNullable(statistics.getFeedbackLikeCount()).orElse(0));
        statistics.setCompletionAvgElapsed(Optional.ofNullable(statistics.getCompletionAvgElapsed()).orElse(new BigDecimal("0")));
        statistics.setImageAvgElapsed(Optional.ofNullable(statistics.getImageAvgElapsed()).orElse(new BigDecimal("0")));
        statistics.setCompletionCostPoints(Optional.ofNullable(statistics.getCompletionCostPoints()).orElse(0));
        statistics.setImageCostPoints(Optional.ofNullable(statistics.getImageCostPoints()).orElse(0));
        statistics.setCompletionTokens(Optional.ofNullable(statistics.getCompletionTokens()).orElse(0));
        statistics.setChatTokens(Optional.ofNullable(statistics.getChatTokens()).orElse(0));
        statistics.setTokens(Optional.ofNullable(statistics.getTokens()).orElse(0));
        return statistics;
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
        fillStatistics.setCompletionSuccessCount(0);
        fillStatistics.setImageSuccessCount(0);
        fillStatistics.setErrorCount(0);
        fillStatistics.setCompletionErrorCount(0);
        fillStatistics.setImageErrorCount(0);
        fillStatistics.setFeedbackLikeCount(0);
        fillStatistics.setCompletionAvgElapsed(new BigDecimal("0"));
        fillStatistics.setImageAvgElapsed(new BigDecimal("0"));
        fillStatistics.setCompletionCostPoints(0);
        fillStatistics.setImageCostPoints(0);
        fillStatistics.setCompletionTokens(0);
        fillStatistics.setChatTokens(0);
        fillStatistics.setTokens(0);
        fillStatistics.setCreateDate(date);
        return fillStatistics;
    }

}