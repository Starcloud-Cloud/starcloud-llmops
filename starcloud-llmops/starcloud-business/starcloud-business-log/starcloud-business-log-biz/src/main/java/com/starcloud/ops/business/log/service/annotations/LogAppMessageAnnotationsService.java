package com.starcloud.ops.business.log.service.annotations;

import java.util.*;
import javax.validation.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.annotations.vo.*;
import com.starcloud.ops.business.log.dal.dataobject.*;

/**
 * 应用执行日志结果反馈标注 Service 接口
 *
 * @author 芋道源码
 */
public interface LogAppMessageAnnotationsService {

    /**
     * 创建应用执行日志结果反馈标注
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAppMessageAnnotations(@Valid LogAppMessageAnnotationsCreateReqVO createReqVO);

    /**
     * 更新应用执行日志结果反馈标注
     *
     * @param updateReqVO 更新信息
     */
    void updateAppMessageAnnotations(@Valid LogAppMessageAnnotationsUpdateReqVO updateReqVO);

    /**
     * 删除应用执行日志结果反馈标注
     *
     * @param id 编号
     */
    void deleteAppMessageAnnotations(Long id);

    /**
     * 获得应用执行日志结果反馈标注
     *
     * @param id 编号
     * @return 应用执行日志结果反馈标注
     */
    LogAppMessageAnnotationsDO getAppMessageAnnotations(Long id);

    /**
     * 获得应用执行日志结果反馈标注列表
     *
     * @param ids 编号
     * @return 应用执行日志结果反馈标注列表
     */
    List<LogAppMessageAnnotationsDO> getAppMessageAnnotationsList(Collection<Long> ids);

    /**
     * 获得应用执行日志结果反馈标注分页
     *
     * @param pageReqVO 分页查询
     * @return 应用执行日志结果反馈标注分页
     */
    PageResult<LogAppMessageAnnotationsDO> getAppMessageAnnotationsPage(LogAppMessageAnnotationsPageReqVO pageReqVO);

    /**
     * 获得应用执行日志结果反馈标注列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 应用执行日志结果反馈标注列表
     */
    List<LogAppMessageAnnotationsDO> getAppMessageAnnotationsList(LogAppMessageAnnotationsExportReqVO exportReqVO);

}