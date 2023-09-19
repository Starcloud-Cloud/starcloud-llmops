package com.starcloud.ops.business.log.service.conversation;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageQuery;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationInfoPageAppUidReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationExportReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationStatusReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationUpdateReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessageStatisticsListAppUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.dal.mysql.LogAppConversationMapper;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
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

    @Resource
    private LogAppMessageMapper logAppMessageMapper;

    /**
     * 创建应用执行日志会话
     *
     * @param request 创建信息
     * @return 编号
     */
    @Override
    public Long createAppConversation(LogAppConversationCreateReqVO request) {
        LogAppConversationDO appConversation = LogAppConversationConvert.INSTANCE.convert(request);
        //手动设置，不走用户态
        appConversation.setCreator(request.getCreator());
        appConversation.setUpdater(request.getUpdater());
        appConversation.setTenantId(request.getTenantId());
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
    public void updateAppConversation(LogAppConversationUpdateReqVO request) {
        // 校验存在
        // validateAppConversationExists(updateReqVO.getId());
        // 更新
        LogAppConversationDO updateObj = LogAppConversationConvert.INSTANCE.convert(request);
        appConversationMapper.update(updateObj, Wrappers.lambdaQuery(LogAppConversationDO.class).eq(LogAppConversationDO::getUid, request.getUid()));

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

    /**
     * 更新应用执行日志会话状态
     *
     * @param request 更新信息
     */
    @Override
    public void updateAppConversationStatus(LogAppConversationStatusReqVO request) {
        appConversationMapper.update(null, Wrappers.lambdaUpdate(LogAppConversationDO.class)
                .eq(LogAppConversationDO::getUid, request.getUid())
                .set(LogAppConversationDO::getStatus, request.getStatus())
                .set(LogAppConversationDO::getErrorCode, request.getErrorCode())
                .set(LogAppConversationDO::getErrorMsg, request.getErrorMsg()));
    }

    /**
     * 删除应用执行日志会话
     *
     * @param id 编号
     */
    @Override
    public void deleteAppConversation(Long id) {
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
    public LogAppConversationDO getAppConversation(Long id) {
        return appConversationMapper.selectById(id);
    }

    /**
     * 获得应用执行日志会话
     *
     * @param uid 编号
     * @return 应用执行日志会话
     */
    @Override
    public LogAppConversationDO getAppConversation(String uid) {
        return appConversationMapper.selectOne(LogAppConversationDO::getUid, uid);
    }

    /**
     * 获得应用执行日志会话列表
     *
     * @param ids 编号
     * @return 应用执行日志会话列表
     */
    @Override
    public List<LogAppConversationDO> getAppConversationList(Collection<Long> ids) {
        return appConversationMapper.selectBatchIds(ids);
    }

    /**
     * 获得应用执行日志会话分页
     *
     * @param pageReqVO 分页查询
     * @return 应用执行日志会话分页
     */
    @Override
    public PageResult<LogAppConversationDO> getAppConversationPage(LogAppConversationPageReqVO pageReqVO) {
        return appConversationMapper.selectPage(pageReqVO);
    }

    /**
     * 获得应用执行日志会话列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 应用执行日志会话列表
     */
    @Override
    public List<LogAppConversationDO> getAppConversationList(LogAppConversationExportReqVO exportReqVO) {
        return appConversationMapper.selectList(exportReqVO);
    }

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
     * 根据应用 UID 获取应用执行日志消息统计数据列表 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    @Override
    public List<LogAppMessageStatisticsListPO> listLogMessageStatisticsByAppUid(LogAppMessageStatisticsListAppUidReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置日期单位
        query.setUnit(logTimeTypeEnum.getGroupUnit().name());
        // 设置开始时间和结束时间
        query.setStartTime(logTimeTypeEnum.getStartTime());
        query.setEndTime(logTimeTypeEnum.getEndTime());
        // 查询数据
        List<LogAppMessageStatisticsListPO> statisticsList = logAppMessageMapper.listLogMessageStatisticsByAppUid(query);
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
    public List<LogAppMessageStatisticsListPO> listLogMessageStatistics(LogAppMessageStatisticsListReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置日期单位
        query.setUnit(logTimeTypeEnum.getGroupUnit().name());
        // 设置开始时间和结束时间
        query.setStartTime(logTimeTypeEnum.getStartTime());
        query.setEndTime(logTimeTypeEnum.getEndTime());
        // 查询数据
        List<LogAppMessageStatisticsListPO> statisticsList = logAppMessageMapper.listLogMessageStatistics(query);
        // 填充数据
        return listLogAppMessageStatistics(statisticsList, logTimeTypeEnum);
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
    public PageResult<LogAppConversationInfoPO> pageLogConversationByAppUid(LogAppConversationInfoPageAppUidReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置开始时间和结束时间
        query.setStartTime(logTimeTypeEnum.getStartTime());
        query.setEndTime(logTimeTypeEnum.getEndTime());

        Page<LogAppConversationDO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<LogAppConversationInfoPO> infoPage = appConversationMapper.pageLogConversationByAppUid(page, query);
        return new PageResult<>(infoPage.getRecords(), infoPage.getTotal());
    }

    /**
     * 获取 应用执行分页信息
     *
     * @param query 分页查询
     * @return 应用执行日志会话分页
     */
    @Override
    public PageResult<LogAppConversationInfoPO> pageLogConversation(AppLogConversationInfoPageQuery query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置开始时间和结束时间
        query.setStartTime(logTimeTypeEnum.getStartTime());
        query.setEndTime(logTimeTypeEnum.getEndTime());

        Page<LogAppConversationDO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<LogAppConversationInfoPO> infoPage = appConversationMapper.pageLogConversation(page, query);
        return new PageResult<>(infoPage.getRecords(), infoPage.getTotal());
    }

    /**
     * 获取最新的会话
     *
     * @param appUid 应用编号
     */
    @Override
    public LogAppConversationDO getRecentlyConversation(String appUid) {
        LambdaQueryWrapper<LogAppConversationDO> wrapper = Wrappers.lambdaQuery(LogAppConversationDO.class)
                .eq(LogAppConversationDO::getAppUid, appUid)
                .orderByDesc(LogAppConversationDO::getCreateTime)
                .last("limit 1");
        return appConversationMapper.selectOne(wrapper);
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
     * 根据日志时间类型，填充数据
     *
     * @param statisticsList  数据
     * @param logTimeTypeEnum 日志时间类型
     * @return 填充后的数据
     */
    @NotNull
    private static List<LogAppMessageStatisticsListPO> listLogAppMessageStatistics(List<LogAppMessageStatisticsListPO> statisticsList, LogTimeTypeEnum logTimeTypeEnum) {

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
                fillStatisticsList.add(getFillLogAppMessageStatistics(formatDate));
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
    private static LogAppMessageStatisticsListPO getFillLogAppMessageStatistics(String date) {
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