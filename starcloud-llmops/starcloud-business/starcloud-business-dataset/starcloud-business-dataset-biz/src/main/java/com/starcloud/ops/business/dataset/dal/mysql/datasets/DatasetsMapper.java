package com.starcloud.ops.business.dataset.dal.mysql.datasets;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsExportReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsPageReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DatasetsMapper extends BaseMapperX<DatasetsDO> {

    default PageResult<DatasetsDO> selectPage(DatasetsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DatasetsDO>()
                .eqIfPresent(DatasetsDO::getUid, reqVO.getUid())
                .likeIfPresent(DatasetsDO::getName, reqVO.getName())
                .eqIfPresent(DatasetsDO::getDescription, reqVO.getDescription())
                .eqIfPresent(DatasetsDO::getProvider, reqVO.getProvider())
                .eqIfPresent(DatasetsDO::getPermission, reqVO.getPermission())
                .eqIfPresent(DatasetsDO::getSourceType, reqVO.getSourceType())
                .eqIfPresent(DatasetsDO::getIndexingModel, reqVO.getIndexingModel())
                .eqIfPresent(DatasetsDO::getIndexStruct, reqVO.getIndexStruct())
                .betweenIfPresent(DatasetsDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(DatasetsDO::getEnabled, reqVO.getEnabled())
                .orderByDesc(DatasetsDO::getId));
    }

    default List<DatasetsDO> selectList(DatasetsExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<DatasetsDO>()
                .eqIfPresent(DatasetsDO::getUid, reqVO.getUid())
                .likeIfPresent(DatasetsDO::getName, reqVO.getName())
                .eqIfPresent(DatasetsDO::getDescription, reqVO.getDescription())
                .eqIfPresent(DatasetsDO::getProvider, reqVO.getProvider())
                .eqIfPresent(DatasetsDO::getPermission, reqVO.getPermission())
                .eqIfPresent(DatasetsDO::getSourceType, reqVO.getSourceType())
                .eqIfPresent(DatasetsDO::getIndexingModel, reqVO.getIndexingModel())
                .eqIfPresent(DatasetsDO::getIndexStruct, reqVO.getIndexStruct())
                .betweenIfPresent(DatasetsDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(DatasetsDO::getEnabled, reqVO.getEnabled())
                .orderByDesc(DatasetsDO::getId));
    }

}