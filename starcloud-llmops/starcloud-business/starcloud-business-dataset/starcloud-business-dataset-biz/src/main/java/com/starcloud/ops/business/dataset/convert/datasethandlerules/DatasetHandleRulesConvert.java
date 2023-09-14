package com.starcloud.ops.business.dataset.convert.datasethandlerules;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasethandlerules.DatasetHandleRulesDO;
import com.starcloud.ops.business.dataset.pojo.dto.CleanRuleVO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface DatasetHandleRulesConvert {
    DatasetHandleRulesConvert INSTANCE = Mappers.getMapper(DatasetHandleRulesConvert.class);

    default DatasetHandleRulesDO convert(DatasetHandleRulesCreateReqVO bean, Long datasetId) {
        if (bean == null) {
            return null;
        }

        DatasetHandleRulesDO.DatasetHandleRulesDOBuilder rulesDOBuilder = DatasetHandleRulesDO.builder();

        rulesDOBuilder.ruleName(bean.getRuleName());

        rulesDOBuilder.datasetId(datasetId);
        rulesDOBuilder.ruleFilter(CollUtil.join(bean.getRuleFilter(), ","));

        rulesDOBuilder.cleanRule(JSONUtil.toJsonStr(bean.getCleanRule()));
        rulesDOBuilder.splitRule(JSONUtil.toJsonStr(bean.getSplitRule()));

        rulesDOBuilder.ruleType(bean.getRuleType());
        rulesDOBuilder.enable(bean.getEnable());
        rulesDOBuilder.fromScene(bean.getFromScene());

        return rulesDOBuilder.build();
    }

    default DatasetHandleRulesDO convert(DatasetHandleRulesUpdateReqVO bean,Long datasetId) {
        if (bean == null) {
            return null;
        }

        DatasetHandleRulesDO.DatasetHandleRulesDOBuilder rulesDOBuilder = DatasetHandleRulesDO.builder();

        rulesDOBuilder.id(bean.getId());
        rulesDOBuilder.ruleName(bean.getRuleName());

        rulesDOBuilder.ruleFilter(CollUtil.join(bean.getRuleFilter(), ","));

        rulesDOBuilder.cleanRule(JSONUtil.toJsonStr(bean.getCleanRule()));
        rulesDOBuilder.splitRule(JSONUtil.toJsonStr(bean.getSplitRule()));

        rulesDOBuilder.ruleType(bean.getRuleType());
        rulesDOBuilder.fromScene(bean.getFromScene());
        rulesDOBuilder.enable(bean.getEnable());

            rulesDOBuilder.datasetId(datasetId);

        return rulesDOBuilder.build();
    }

    default DatasetHandleRulesRespVO convert(DatasetHandleRulesDO bean) {
        DatasetHandleRulesRespVO datasetHandleRulesRespVO = new DatasetHandleRulesRespVO();
        datasetHandleRulesRespVO.setId(bean.getId());
        datasetHandleRulesRespVO.setRuleName(bean.getRuleName());
        datasetHandleRulesRespVO.setRuleFilter(CollUtil.toList(bean.getRuleFilter().split(",")));
        datasetHandleRulesRespVO.setCleanRule(JSONUtil.toBean(bean.getCleanRule(), CleanRuleVO.class));
        datasetHandleRulesRespVO.setSplitRule(JSONUtil.toBean(bean.getSplitRule(), SplitRule.class));
        datasetHandleRulesRespVO.setRuleType(bean.getRuleType());
        datasetHandleRulesRespVO.setFromScene(bean.getFromScene());
        datasetHandleRulesRespVO.setEnable(bean.getEnable());
        datasetHandleRulesRespVO.setCreateTime(bean.getCreateTime());
        datasetHandleRulesRespVO.setUpdateTime(bean.getUpdateTime());
        return datasetHandleRulesRespVO;
    }


    default List<DatasetHandleRulesRespVO> convertList(List<DatasetHandleRulesDO> list) {
        if (list == null) {
            return null;
        }

        List<DatasetHandleRulesRespVO> list1 = new ArrayList<DatasetHandleRulesRespVO>(list.size());
        for (DatasetHandleRulesDO datasetsDO : list) {
            list1.add(convert(datasetsDO));
        }
        return list1;
    }


    default PageResult<DatasetHandleRulesRespVO> convertPage(PageResult<DatasetHandleRulesDO> page) {
        if (page == null) {
            return null;
        }

        PageResult<DatasetHandleRulesRespVO> pageResult = new PageResult<DatasetHandleRulesRespVO>();

        pageResult.setList(convertList(page.getList()));
        pageResult.setTotal(page.getTotal());

        return pageResult;
    }
}
