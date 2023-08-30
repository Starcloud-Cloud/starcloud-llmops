package com.starcloud.ops.business.dataset.service.datasethandlerules;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.*;
import com.starcloud.ops.business.dataset.dal.dataobject.datasethandlerules.DatasetHandleRulesDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
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
    Boolean createRules(DatasetHandleRulesCreateReqVO createReqVO);


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


    DatasetHandleRulesDO getFilteredRule(Long datasetId, String ruleType, String data, Long ruleId);

    /**
     * 通过 Id 获取规则信息
     *
     * @param rulesDO 创建信息
     * @param data    创建信息
     * @return 编号
     */
    String processCleanRule(DatasetHandleRulesDO rulesDO, String data);

    /**
     * 调试规则
     *
     * @param debugReqVO 创建信息
     * @return 编号
     */
    DatasetHandleRulesDebugRespVO debugRule(@Validated DatasetHandleRulesDebugReqVO debugReqVO);


    /**
     * 根据规则 ID 删除规则
     * @param ruleId  规则 ID
     * @return Boolean
     */
    Boolean deleteRule(Long ruleId);

    List<HandleRuleTypeRespVO> getRuleType();


    List<HandleRuleTypeRespVO> getFormatType();

    /**
     * 执行数据清洗
     * 1.根据数据类型判断，获取需要清洗的数据
     * 2.根据数据获取清洗规则
     * 3.执行清洗流程，
     * 4.返回清洗数据
     * @return 返回清洗后的数据
     */
    HandleRuleProcessResultRespVO processDataClean(DatasetSourceDataDO sourceDataDO);

    /**
     * 根据规则获取网页预设语言
     * @param url
     * @return
     */
    String getHtmlLanguageRule(Long datasetId, String url);

}
