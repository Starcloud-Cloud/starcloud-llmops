package com.starcloud.ops.business.log.service.messagesave;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.messagesave.vo.*;
import com.starcloud.ops.business.log.dal.dataobject.*;

import java.util.*;
import javax.validation.*;

/**
 * 应用执行日志结果保存 Service 接口
 *
 * @author 芋道源码
 */
public interface LogAppMessageSaveService {

    /**
     * 创建应用执行日志结果保存
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAppMessageSave(@Valid LogAppMessageSaveCreateReqVO createReqVO);

    /**
     * 更新应用执行日志结果保存
     *
     * @param updateReqVO 更新信息
     */
    void updateAppMessageSave(@Valid LogAppMessageSaveUpdateReqVO updateReqVO);

    /**
     * 删除应用执行日志结果保存
     *
     * @param id 编号
     */
    void deleteAppMessageSave(Long id);

    /**
     * 获得应用执行日志结果保存
     *
     * @param id 编号
     * @return 应用执行日志结果保存
     */
    LogAppMessageSaveDO getAppMessageSave(Long id);

    /**
     * 获得应用执行日志结果保存列表
     *
     * @param ids 编号
     * @return 应用执行日志结果保存列表
     */
    List<LogAppMessageSaveDO> getAppMessageSaveList(Collection<Long> ids);

    /**
     * 获得应用执行日志结果保存分页
     *
     * @param pageReqVO 分页查询
     * @return 应用执行日志结果保存分页
     */
    PageResult<LogAppMessageSaveDO> getAppMessageSavePage(LogAppMessageSavePageReqVO pageReqVO);

    /**
     * 获得应用执行日志结果保存列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 应用执行日志结果保存列表
     */
    List<LogAppMessageSaveDO> getAppMessageSaveList(LogAppMessageSaveExportReqVO exportReqVO);

}