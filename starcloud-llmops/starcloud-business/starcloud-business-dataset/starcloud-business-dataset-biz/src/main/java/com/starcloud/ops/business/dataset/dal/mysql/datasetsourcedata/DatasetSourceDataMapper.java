package com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataPageReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集源数据 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DatasetSourceDataMapper extends BaseMapperX<DatasetSourceDataDO> {

    default PageResult<DatasetSourceDataDO> selectPage(DatasetSourceDataPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DatasetSourceDataDO>()
                .eqIfPresent(DatasetSourceDataDO::getDatasetId, reqVO.getDatasetId())
                .orderByDesc(DatasetSourceDataDO::getId));
    }


    default List<DatasetSourceDataDO> selectByDatasetId(String datasetId, Integer dataModel) {
        LambdaQueryWrapper<DatasetSourceDataDO> queryWrapper = Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                .eq(DatasetSourceDataDO::getDataModel, dataModel)
                .in(DatasetSourceDataDO::getDatasetId,datasetId)
                .orderByAsc(DatasetSourceDataDO::getCreateTime);
        return selectList(queryWrapper);
    }


}