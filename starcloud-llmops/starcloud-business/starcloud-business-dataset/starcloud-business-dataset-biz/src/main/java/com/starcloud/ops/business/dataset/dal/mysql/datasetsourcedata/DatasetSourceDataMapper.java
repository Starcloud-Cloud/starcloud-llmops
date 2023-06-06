package com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataPageReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
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
                .eqIfPresent(DatasetSourceDataDO::getUid, reqVO.getUid())
                .likeIfPresent(DatasetSourceDataDO::getName, reqVO.getName())
                .eqIfPresent(DatasetSourceDataDO::getStorageId, reqVO.getStorageId())
                .eqIfPresent(DatasetSourceDataDO::getPosition, reqVO.getPosition())
                .eqIfPresent(DatasetSourceDataDO::getDataSourceType, reqVO.getDataSourceType())
                .eqIfPresent(DatasetSourceDataDO::getDataSourceInfo, reqVO.getDataSourceInfo())
                .eqIfPresent(DatasetSourceDataDO::getDatasetProcessRuleId, reqVO.getDatasetProcessRuleId())
                .eqIfPresent(DatasetSourceDataDO::getBatch, reqVO.getBatch())
                .eqIfPresent(DatasetSourceDataDO::getCreatedFrom, reqVO.getCreatedFrom())
                .eqIfPresent(DatasetSourceDataDO::getWordCount, reqVO.getWordCount())
                .eqIfPresent(DatasetSourceDataDO::getTokens, reqVO.getTokens())
                .eqIfPresent(DatasetSourceDataDO::getDatasetId, reqVO.getDatasetId())
                .eqIfPresent(DatasetSourceDataDO::getCreatedApiRequestId, reqVO.getCreatedApiRequestId())
                .betweenIfPresent(DatasetSourceDataDO::getParsingCompletedTime, reqVO.getParsingCompletedTime())
                .betweenIfPresent(DatasetSourceDataDO::getCleaningCompletedTime, reqVO.getCleaningCompletedTime())
                .betweenIfPresent(DatasetSourceDataDO::getSplittingCompletedTime, reqVO.getSplittingCompletedTime())
                .betweenIfPresent(DatasetSourceDataDO::getIndexingTime, reqVO.getIndexingTime())
                .betweenIfPresent(DatasetSourceDataDO::getProcessingStartedTime, reqVO.getProcessingStartedTime())
                .eqIfPresent(DatasetSourceDataDO::getCompletedAt, reqVO.getCompletedAt())
                .eqIfPresent(DatasetSourceDataDO::getErrorMessage, reqVO.getErrorMessage())
                .betweenIfPresent(DatasetSourceDataDO::getStoppedTime, reqVO.getStoppedTime())
                .eqIfPresent(DatasetSourceDataDO::getPausedBy, reqVO.getPausedBy())
                .betweenIfPresent(DatasetSourceDataDO::getPausedTime, reqVO.getPausedTime())
                .eqIfPresent(DatasetSourceDataDO::getDisabledAt, reqVO.getDisabledAt())
                .betweenIfPresent(DatasetSourceDataDO::getDisabledTime, reqVO.getDisabledTime())
                .eqIfPresent(DatasetSourceDataDO::getCreater, reqVO.getCreater())
                .betweenIfPresent(DatasetSourceDataDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(DatasetSourceDataDO::getIndexingStatus, reqVO.getIndexingStatus())
                .eqIfPresent(DatasetSourceDataDO::getEnabled, reqVO.getEnabled())
                .eqIfPresent(DatasetSourceDataDO::getDocType, reqVO.getDocType())
                .eqIfPresent(DatasetSourceDataDO::getDocMetadata, reqVO.getDocMetadata())
                .eqIfPresent(DatasetSourceDataDO::getArchived, reqVO.getArchived())
                .eqIfPresent(DatasetSourceDataDO::getArchivedBy, reqVO.getArchivedBy())
                .eqIfPresent(DatasetSourceDataDO::getArchivedReason, reqVO.getArchivedReason())
                .eqIfPresent(DatasetSourceDataDO::getIsPaused, reqVO.getIsPaused())
                .betweenIfPresent(DatasetSourceDataDO::getArchivedTime, reqVO.getArchivedTime())
                .orderByDesc(DatasetSourceDataDO::getId));
    }


}