package com.starcloud.ops.business.log.service.conversation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageUidReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationExportReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationStatusReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationUpdateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;

/**
 * 应用执行日志会话 Service 接口
 *
 * @author 芋道源码
 */
public interface LogAppConversationService {

    /**
     * 创建应用执行日志会话
     *
     * @param request 创建信息
     * @return 编号
     */
    Long createAppLogConversation(@Valid LogAppConversationCreateReqVO request);

    /**
     * 更新应用执行日志会话
     *
     * @param request 更新信息
     */
    void updateAppLogConversation(@Valid LogAppConversationUpdateReqVO request);

    /**
     * 更新应用执行日志会话状态
     *
     * @param uid    编号
     * @param status 状态
     */
    void updateAppLogConversationStatus(@NotEmpty(message = "会话 Uid 不能为空") String uid, @NotEmpty(message = "会话状态不能为空") String status);

    /**
     * 更新应用执行日志会话状态
     *
     * @param request 更新信息
     */
    void updateAppLogConversationStatus(@Valid LogAppConversationStatusReqVO request);

    /**
     * 删除应用执行日志会话
     *
     * @param id 编号
     */
    void deleteAppLogConversation(Long id);

    /**
     * 获得应用执行日志会话
     *
     * @param id 编号
     * @return 应用执行日志会话
     */
    LogAppConversationDO getAppLogConversation(Long id);

    /**
     * 获得应用执行日志会话
     *
     * @param uid 编号
     * @return 应用执行日志会话
     */
    LogAppConversationDO getAppLogConversation(String uid);

    /**
     * 获得应用执行日志会话列表
     *
     * @param ids 编号
     * @return 应用执行日志会话列表
     */
    List<LogAppConversationDO> listAppLogConversation(Collection<Long> ids);

    /**
     * 获得应用执行日志会话列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 应用执行日志会话列表
     */
    List<LogAppConversationDO> listAppLogConversation(LogAppConversationExportReqVO exportReqVO);

    /**
     * 获得应用执行日志会话分页
     *
     * @param pageReqVO 分页查询
     * @return 应用执行日志会话分页
     */
    PageResult<LogAppConversationDO> pageAppLogConversation(LogAppConversationPageReqVO pageReqVO);

    /**
     * 获取用户最新会话
     *
     * @param appUid  应用uid
     * @param creator 用户uid
     * @return 应用执行日志会话
     */
    LogAppConversationDO getUserRecentlyConversation(String appUid, String creator, String scene);

    /**
     * 根据 应用 UID 分页查询应用执行日志会话数据 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 应用执行日志会话数据
     */
    PageResult<LogAppConversationInfoPO> pageLogAppConversation(AppLogConversationInfoPageUidReqVO query);

    /**
     * 获取 应用执行分页信息
     *
     * @param query 分页查询
     * @return 应用执行日志会话分页
     */
    PageResult<LogAppConversationInfoPO> pageLogAppConversation(AppLogConversationInfoPageReqVO query);

    /**
     * 获取最新的会话
     *
     * @param appUid 应用编号
     * @return 应用执行日志会话
     */
    LogAppConversationDO getRecentlyConversation(String appUid);

}