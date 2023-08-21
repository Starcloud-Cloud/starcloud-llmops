package com.starcloud.ops.business.dataset.convert.datasethandlerules;


import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DatasetHandleRulesDO;
import com.starcloud.ops.business.dataset.pojo.dto.CleanRuleVO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DatasetHandleRulesConvert {
    DatasetHandleRulesConvert INSTANCE = Mappers.getMapper(DatasetHandleRulesConvert.class);

    // DatasetHandleRulesDO convert(DatasetHandleRulesCreateReqVO bean);

    default DatasetHandleRulesDO convert(DatasetHandleRulesUpdateReqVO bean) {
        DatasetHandleRulesDO handleRulesDO = new DatasetHandleRulesDO();
        handleRulesDO.setId(bean.getId());
        handleRulesDO.setDatasetId(bean.getDatasetId());
        handleRulesDO.setCleanRule(JSONUtil.toJsonStr(bean.getCleanRuleVO()));
        handleRulesDO.setSplitRule(JSONUtil.toJsonStr(bean.getSplitRule()));
        return handleRulesDO;
    }

    default DatasetHandleRulesRespVO convert(DatasetHandleRulesDO bean) {
        DatasetHandleRulesRespVO datasetHandleRulesRespVO = new DatasetHandleRulesRespVO();
        datasetHandleRulesRespVO.setId(bean.getId());
        datasetHandleRulesRespVO.setCleanRuleVO(JSONUtil.toBean(bean.getCleanRule(), CleanRuleVO.class));
        datasetHandleRulesRespVO.setSplitRule(JSONUtil.toBean(bean.getSplitRule(), SplitRule.class));
        datasetHandleRulesRespVO.setDatasetId(bean.getDatasetId());
        return datasetHandleRulesRespVO;
    }
}
