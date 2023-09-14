package com.starcloud.ops.business.dataset.dal.mysql.datasethandlerules;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesPageReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasethandlerules.DatasetHandleRulesDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DatasetHandleRulesMapper extends BaseMapperX<DatasetHandleRulesDO> {


    default PageResult<DatasetHandleRulesDO> selectPage(DatasetHandleRulesPageReqVO reqVO,Long datasetId) {

        return selectPage(reqVO, new LambdaQueryWrapperX<DatasetHandleRulesDO>()
                .eqIfPresent(DatasetHandleRulesDO::getDatasetId, datasetId)
                .orderByDesc(DatasetHandleRulesDO::getId));
    }
}
