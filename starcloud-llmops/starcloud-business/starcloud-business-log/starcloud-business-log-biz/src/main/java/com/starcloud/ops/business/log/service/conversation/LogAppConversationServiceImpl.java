package com.starcloud.ops.business.log.service.conversation;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageUidReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationListReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationStatusReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationUpdateReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListUidReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.dal.mysql.LogAppConversationMapper;
import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import org.apache.commons.lang3.StringUtils;
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
 * 应用执行日志会话 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class LogAppConversationServiceImpl implements LogAppConversationService {

    @Resource
    private LogAppConversationMapper appConversationMapper;

    /**
     * 创建应用执行日志会话
     *
     * @param request 创建信息
     * @return 编号
     */
    @Override
    public Long createAppLogConversation(LogAppConversationCreateReqVO request) {
        LogAppConversationDO appConversation = LogAppConversationConvert.INSTANCE.convert(request);
        //手动设置，不走用户态
        appConversation.setCreator(request.getCreator());
        appConversation.setUpdater(request.getUpdater());
        appConversation.setDeptId(request.getDeptId());
        appConversation.setTenantId(request.getTenantId());
        appConversation.setCreateTime(LocalDateTime.now());
        appConversation.setUpdateTime(LocalDateTime.now());
        appConversationMapper.insert(appConversation);
        // 返回
        return appConversation.getId();
    }

    /**
     * 更新应用执行日志会话
     *
     * @param request 更新信息
     */
    @Override
    public void updateAppLogConversation(LogAppConversationUpdateReqVO request) {
        LogAppConversationDO updateObj = LogAppConversationConvert.INSTANCE.convert(request);
        updateObj.setUpdateTime(LocalDateTime.now());
        appConversationMapper.update(updateObj, Wrappers.lambdaQuery(LogAppConversationDO.class).eq(LogAppConversationDO::getUid, request.getUid()));
    }

    /**
     * 更新应用执行日志会话状态
     *
     * @param request 更新信息
     */
    @Override
    public void updateAppLogConversationStatus(LogAppConversationStatusReqVO request) {
        appConversationMapper.update(null, Wrappers.lambdaUpdate(LogAppConversationDO.class)
                .eq(LogAppConversationDO::getUid, request.getUid())
                .set(StringUtils.isNotBlank(request.getAiModel()), LogAppConversationDO::getAiModel, request.getAiModel())
                .set(LogAppConversationDO::getStatus, request.getStatus())
                .set(LogAppConversationDO::getErrorCode, request.getErrorCode())
                .set(LogAppConversationDO::getErrorMsg, request.getErrorMsg())
                .set(LogAppConversationDO::getUpdateTime, LocalDateTime.now()));
    }

    /**
     * 删除应用执行日志会话
     *
     * @param id 编号
     */
    @Override
    public void deleteAppLogConversation(Long id) {
        // 校验存在
        validateAppConversationExists(id);
        // 删除
        appConversationMapper.deleteById(id);
    }

    /**
     * 获得应用执行日志会话
     *
     * @param id 编号
     * @return 应用执行日志会话
     */
    @Override
    public LogAppConversationDO getAppLogConversation(Long id) {
        return appConversationMapper.selectById(id);
    }

    /**
     * 获得应用执行日志会话
     *
     * @param uid 编号
     * @return 应用执行日志会话
     */
    @Override
    public LogAppConversationDO getAppLogConversation(String uid) {
        return appConversationMapper.selectOne(LogAppConversationDO::getUid, uid);
    }

    /**
     * 获取用户最新会话
     *
     * @param appUid  应用uid
     * @param creator 用户uid
     * @return 应用执行日志会话
     */
    @Override
    public LogAppConversationDO getUserRecentlyConversation(String appUid, String creator, String scene) {
        LambdaQueryWrapper<LogAppConversationDO> wrapper = Wrappers.lambdaQuery(LogAppConversationDO.class)
                .eq(LogAppConversationDO::getAppUid, appUid)
                .eq(LogAppConversationDO::getCreator, creator)
                .eq(LogAppConversationDO::getFromScene, scene)
                .orderByDesc(LogAppConversationDO::getCreateTime)
                .last("limit 1");
        return appConversationMapper.selectOne(wrapper);
    }

    /**
     * 获得应用执行日志会话列表
     *
     * @param ids 编号
     * @return 应用执行日志会话列表
     */
    @Override
    public List<LogAppConversationDO> listAppLogConversation(Collection<Long> ids) {
        return appConversationMapper.selectBatchIds(ids);
    }

    /**
     * 获得应用执行日志会话列表
     *
     * @param query 查询条件
     * @return 应用执行日志会话列表
     */
    @Override
    public List<LogAppConversationDO> listAppLogConversation(LogAppConversationListReqVO query) {
        return appConversationMapper.selectList(query);
    }

    /**
     * 获得应用执行日志会话分页
     *
     * @param query 分页查询
     * @return 应用执行日志会话分页
     */
    @Override
    public PageResult<LogAppConversationDO> pageAppLogConversation(LogAppConversationPageReqVO query) {
        return appConversationMapper.selectPage(query);
    }

    /**
     * 根据 应用 UID 分页查询应用执行日志会话数据 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 应用执行日志会话数据
     */
    @Override
    public PageResult<LogAppConversationInfoPO> pageLogAppConversation(AppLogConversationInfoPageUidReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置开始时间和结束时间
        query.setStartTime(logTimeTypeEnum.getStartTime());
        query.setEndTime(logTimeTypeEnum.getEndTime());

        Page<LogAppConversationDO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<LogAppConversationInfoPO> infoPage = appConversationMapper.pageLogAppConversationByAppUid(page, query);
        return new PageResult<>(infoPage.getRecords(), infoPage.getTotal());
    }

    /**
     * 获取 应用执行分页信息
     *
     * @param query 分页查询
     * @return 应用执行日志会话分页
     */
    @Override
    public PageResult<LogAppConversationInfoPO> pageLogAppConversation(AppLogConversationInfoPageReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置开始时间和结束时间
        query.setStartTime(logTimeTypeEnum.getStartTime());
        query.setEndTime(logTimeTypeEnum.getEndTime());

        Page<LogAppConversationDO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<LogAppConversationInfoPO> infoPage = appConversationMapper.pageLogAppConversation(page, query);
        return new PageResult<>(infoPage.getRecords(), infoPage.getTotal());
    }

    /**
     * 校验应用执行日志会话是否存在
     *
     * @param id 应用执行日志会话编号
     */
    private void validateAppConversationExists(Long id) {
        if (appConversationMapper.selectById(id) == null) {
            throw exception(APP_CONVERSATION_NOT_EXISTS);
        }
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
    public List<LogAppMessageStatisticsListPO> listLogAppConversationStatistics(AppLogMessageStatisticsListUidReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(org.apache.commons.lang3.StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置日期单位
        query.setUnit(logTimeTypeEnum.getGroupUnit().name());
        // 设置开始时间和结束时间
        query.setStartTime(DateTimeFormatter.ofPattern(logTimeTypeEnum.getFormatByGroupUnit()).format(logTimeTypeEnum.getStartTime()));
        query.setEndTime(DateTimeFormatter.ofPattern(logTimeTypeEnum.getFormatByGroupUnit()).format(logTimeTypeEnum.getEndTime()));
        // 查询数据
        List<LogAppMessageStatisticsListPO> statisticsList = appConversationMapper.listLogAppConversationStatisticsByAppUid(query);
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
    public List<LogAppMessageStatisticsListPO> listLogAppConversationStatistics(AppLogMessageStatisticsListReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(org.apache.commons.lang3.StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置日期单位
        query.setUnit(logTimeTypeEnum.getGroupUnit().name());
        // 设置开始时间和结束时间
        query.setStartTime(DateTimeFormatter.ofPattern(logTimeTypeEnum.getFormatByGroupUnit()).format(logTimeTypeEnum.getStartTime()));
        query.setEndTime(DateTimeFormatter.ofPattern(logTimeTypeEnum.getFormatByGroupUnit()).format(logTimeTypeEnum.getEndTime()));
        // 查询数据
        List<LogAppMessageStatisticsListPO> statisticsList = appConversationMapper.listLogAppConversationStatistics(query);
        // 填充数据
        return listLogAppMessageStatistics(statisticsList, logTimeTypeEnum);
    }

    @Override
    public List<LogAppMessageStatisticsListPO> listRightsStatistics(AppLogMessageStatisticsListReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(org.apache.commons.lang3.StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置日期单位
        query.setUnit(logTimeTypeEnum.getGroupUnit().name());
        // 设置开始时间和结束时间
        query.setStartTime(DateTimeFormatter.ofPattern(logTimeTypeEnum.getFormatByGroupUnit()).format(logTimeTypeEnum.getStartTime()));
        query.setEndTime(DateTimeFormatter.ofPattern(logTimeTypeEnum.getFormatByGroupUnit()).format(logTimeTypeEnum.getEndTime()));
        // 查询数据
        List<LogAppMessageStatisticsListPO> statisticsList = appConversationMapper.listRightsStatistics(query);
        // 填充数据
        return listLogAppMessageStatistics(statisticsList, logTimeTypeEnum);
    }

    @Override
    public List<LogAppMessageStatisticsListPO> listRightsStatistics(AppLogMessageStatisticsListUidReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(org.apache.commons.lang3.StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置日期单位
        query.setUnit(logTimeTypeEnum.getGroupUnit().name());
        // 设置开始时间和结束时间
        query.setStartTime(DateTimeFormatter.ofPattern(logTimeTypeEnum.getFormatByGroupUnit()).format(logTimeTypeEnum.getStartTime()));
        query.setEndTime(DateTimeFormatter.ofPattern(logTimeTypeEnum.getFormatByGroupUnit()).format(logTimeTypeEnum.getEndTime()));
        // 查询数据
        List<LogAppMessageStatisticsListPO> statisticsList = appConversationMapper.listRightsStatisticsByAppUid(query);
        // 填充数据
        return listLogAppMessageStatistics(statisticsList, logTimeTypeEnum);
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
                    .filter(statistics -> formatDate.equals(statistics.getUpdateDate())).findFirst();
            // 存在就添加，不存在就创建
            if (logMessageStatisticsOptional.isPresent()) {
                fillStatisticsList.add(handlerAppMessageStatistics(logMessageStatisticsOptional.get()));
            } else {
                fillStatisticsList.add(fillLogAppMessageStatistics(formatDate));
            }
        }

        // 处理并且返回数据
        return getStatisticsListStream(fillStatisticsList, logTimeTypeEnum)
                .sorted(Comparator.comparing(LogAppMessageStatisticsListPO::getUpdateDate))
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
                String updateDate = item.getUpdateDate();
                LocalDateTime localDateTime = LocalDateTime.parse(updateDate, DateTimeFormatter.ofPattern(LogTimeTypeEnum.TODAY.getFormatByGroupUnit()));
                item.setUpdateDate(localDateTime.format(DateTimeFormatter.ofPattern("HH")));
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
        statistics.setMatrixCostPoints(Optional.ofNullable(statistics.getMatrixCostPoints()).orElse(0));
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
        fillStatistics.setMatrixCostPoints(0);
        fillStatistics.setCompletionTokens(0);
        fillStatistics.setChatTokens(0);
        fillStatistics.setTokens(0);
        fillStatistics.setUpdateDate(date);
        return fillStatistics;
    }
}