package com.starcloud.ops.business.dataset.service.datasethandlerules;

import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesDebugReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesUpdateReqVO;
import org.springframework.validation.annotation.Validated;

/**
 * 数据集 数据出力规则 Service 接口
 *
 * @author Alan Cusack
 */
public interface DatasetDataHandleRulesService {

    /**
     * 创建规则
     *
     * @param datasetId 创建信息
     * @return 编号
     */
    Boolean createDefaultRules(Long datasetId);


    /**
     * 创建规则
     *
     * @param updateReqVO 创建信息
     * @return 编号
     */
    Boolean updateRules(@Validated DatasetHandleRulesUpdateReqVO updateReqVO);

    /**
     * 通过 Id 获取规则信息
     *
     * @param Id 创建信息
     * @return 编号
     */
    DatasetHandleRulesRespVO getRuleById(Long Id);

    /**
     * 通过 Id 获取规则信息
     *
     * @param datasetId 创建信息
     * @return 编号
     */
    DatasetHandleRulesRespVO getRuleByDatasetId(Long datasetId);

    /**
     *  规则调试
     *
     * @param debugReqVO 调试信息
     * @return 编号
     */
    String debugRule(DatasetHandleRulesDebugReqVO debugReqVO);
}
