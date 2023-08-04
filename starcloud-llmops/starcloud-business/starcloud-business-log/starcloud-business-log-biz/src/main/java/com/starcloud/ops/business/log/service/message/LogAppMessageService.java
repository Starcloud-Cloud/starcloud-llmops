package com.starcloud.ops.business.log.service.message;

import java.util.*;
import javax.validation.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.message.vo.*;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;

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