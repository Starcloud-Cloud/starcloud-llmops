package com.starcloud.ops.business.dataset.service.datasethandlerules;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.*;
import com.starcloud.ops.business.dataset.dal.dataobject.datasethandlerules.DatasetHandleRulesDO;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 数据集 数据处理规则 Service 接口
 *
 * @author Alan Cusack
 */
public interface DatasetDataHandleRulesService {


    /**
     * 获得规则分页
     *
     * @param pageReqVO 分页查询
     * @return 规则分页
     */
    PageResult<DatasetHandleRulesRespVO> getRulePage(DatasetHandleRulesPageReqVO pageReqVO);


    /**
     * 创建规则
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Boolean createDefaultRules(DatasetHandleRulesCreateReqVO createReqVO);


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
     * 通过 Id集合 获取多条数据
     */
    List<DatasetHandleRulesDO> getRuleByIds(List<Long> Ids);

    /**
     * 通过 Id 获取规则信息
     *
     * @param datasetId 创建信息
     * @return 编号
     */
    DatasetHandleRulesRespVO getRuleByDatasetId(Long datasetId);



    List<Long> getFilteredRuleIds(Long datasetId, String ruleType, String data, Long ruleId);

    /**
     * 通过 Id 获取规则信息
     *
     * @param ruleIds 创建信息
     * @param data 创建信息
     * @return 编号
     */
    String processCleanRule(List<Long> ruleIds, String data);

    /**
     * 通过 Id 获取规则信息
     *
     * @param debugReqVO 创建信息
     * @return 编号
     */
    DatasetHandleRulesDebugRespVO debugRule(@Validated DatasetHandleRulesDebugReqVO debugReqVO);


    Boolean deleteRule(Long ruleId);

    List<HandleRuleTypeRespVO>getRuleType();


    List<HandleRuleTypeRespVO>getFormatType();

}
