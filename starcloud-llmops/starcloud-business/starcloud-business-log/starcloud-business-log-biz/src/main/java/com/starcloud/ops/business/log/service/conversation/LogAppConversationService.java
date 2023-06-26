package com.starcloud.ops.business.log.service.conversation;

import java.util.*;
import javax.validation.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.conversation.vo.*;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;

/**
 * 应用执行日志会话 Service 接口
 *
 * @author 芋道源码
 */
public interface LogAppConversationService {

    /**
     * 创建应用执行日志会话
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAppConversation(@Valid LogAppConversationCreateReqVO createReqVO);

    /**
     * 更新应用执行日志会话
     *
     * @param updateReqVO 更新信息
     */
    void updateAppConversation(@Valid LogAppConversationUpdateReqVO updateReqVO);

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
     * app message 统计列表数据
     *
     * @param statisticsListReqVO
     * @return
     */
    List<LogAppMessageStatisticsListPO> getAppMessageStatisticsList(LogAppMessageStatisticsListReqVO statisticsListReqVO);


    /**
     * 获取 应用执行分页信息
     *
     * @param pageReqVO
     * @return
     */
    PageResult<LogAppConversationInfoPO> getAppConversationInfoPage(LogAppConversationInfoPageReqVO pageReqVO);


}