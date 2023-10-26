package com.starcloud.ops.business.log.service.message;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageListReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageUpdateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应用执行日志结果 Service 接口
 *
 * @author 芋道源码
 */
public interface LogAppMessageService {

    /**
     * 创建应用执行日志结果
     *
     * @param request 创建信息
     * @return 编号
     */
    Long createAppLogMessage(@Valid LogAppMessageCreateReqVO request);

    /**
     * 更新应用执行日志结果
     *
     * @param request 更新信息
     */
    void updateAppLogMessage(@Valid LogAppMessageUpdateReqVO request);

    /**
     * 删除应用执行日志结果
     *
     * @param id 编号
     */
    void deleteAppLogMessage(Long id);

    /**
     * 获得应用执行日志结果
     *
     * @param id 主键
     * @return 应用执行日志结果
     */
    LogAppMessageDO getAppLogMessage(Long id);

    /**
     * 获得应用执行日志结果
     *
     * @param uid 编号
     * @return 应用执行日志结果
     */
    LogAppMessageDO getAppLogMessage(String uid);

    /**
     * 获得应用执行日志结果列表
     *
     * @param ids 编号
     * @return 应用执行日志结果列表
     */
    List<LogAppMessageDO> listAppLogMessage(Collection<Long> ids);

    /**
     * 获得应用执行日志结果列表, 用于 Excel 导出
     *
     * @param query 查询条件
     * @return 应用执行日志结果列表
     */
    List<LogAppMessageDO> listAppLogMessage(LogAppMessageListReqVO query);

    /**
     * 获得应用执行日志结果列表, 根据 appConversationUid
     *
     * @param appConversationUidList appConversationUid 列表
     * @return 应用执行日志结果列表
     */
    List<LogAppMessageDO> listAppLogMessageByAppConversationUidList(Collection<String> appConversationUidList);

    /**
     * 获得应用执行日志结果 Map, 按照 appConversationUid 分组
     *
     * @param appConversationUidList appConversationUid 列表
     * @return 应用执行日志结果 Map
     */
    default Map<String, List<LogAppMessageDO>> mapAppLogMessageAppConversationUid(Collection<String> appConversationUidList) {
        List<LogAppMessageDO> list = listAppLogMessageByAppConversationUidList(appConversationUidList);
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.groupingBy(LogAppMessageDO::getAppConversationUid));
    }

    /**
     * 获得应用执行日志结果分页
     *
     * @param query 分页查询
     * @return 应用执行日志结果分页
     */
    PageResult<LogAppMessageDO> pageAppLogMessage(LogAppMessagePageReqVO query);

    /**
     * 排除系统总结场景
     *
     * @param pageReqVO 分页查询
     * @return 应用执行日志结果分页
     */
    PageResult<LogAppMessageDO> userMessagePage(LogAppMessagePageReqVO pageReqVO);

    /**
     * 根据应用 UID 获取应用执行日志消息统计数据列表 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    List<LogAppMessageStatisticsListPO> listLogAppMessageStatistics(AppLogMessageStatisticsListUidReqVO query);

    /**
     * app message 统计列表数据
     *
     * @param query 查询条件
     * @return 应用执行日志会话列表
     */
    List<LogAppMessageStatisticsListPO> listLogAppMessageStatistics(AppLogMessageStatisticsListReqVO query);

}