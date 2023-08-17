package com.starcloud.ops.business.log.service.message;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.log.api.message.vo.AppLogMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageExportReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageUpdateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 应用执行日志结果 Service 接口
 *
 * @author 芋道源码
 */
public interface LogAppMessageService {

    /**
     * 创建应用执行日志结果
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAppMessage(@Valid LogAppMessageCreateReqVO createReqVO);

    /**
     * 更新应用执行日志结果
     *
     * @param updateReqVO 更新信息
     */
    void updateAppMessage(@Valid LogAppMessageUpdateReqVO updateReqVO);

    /**
     * 删除应用执行日志结果
     *
     * @param id 编号
     */
    void deleteAppMessage(Long id);

    /**
     * 获得应用执行日志结果
     *
     * @param id 编号
     * @return 应用执行日志结果
     */
    LogAppMessageDO getAppMessage(Long id);

    /**
     * 获得应用执行日志结果
     *
     * @param uid 编号
     * @return 应用执行日志结果
     */
    LogAppMessageDO getAppMessage(String uid);

    /**
     * 根据会话uid获取消息列表
     *
     * @param query 查询条件
     * @return 消息列表
     */
    Page<LogAppMessageDO> getAppMessageList(AppLogMessagePageReqVO query);

    /**
     * 获得应用执行日志结果列表
     *
     * @param ids 编号
     * @return 应用执行日志结果列表
     */
    List<LogAppMessageDO> getAppMessageList(Collection<Long> ids);

    /**
     * 获得应用执行日志结果分页
     *
     * @param pageReqVO 分页查询
     * @return 应用执行日志结果分页
     */
    PageResult<LogAppMessageDO> getAppMessagePage(LogAppMessagePageReqVO pageReqVO);

    /**
     * 排除系统总结场景
     * @param pageReqVO 分页查询
     * @return 应用执行日志结果分页
     */
    PageResult<LogAppMessageDO> userMessagePage(LogAppMessagePageReqVO pageReqVO);

    /**
     * 获得应用执行日志结果列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 应用执行日志结果列表
     */
    List<LogAppMessageDO> getAppMessageList(LogAppMessageExportReqVO exportReqVO);

}