package com.starcloud.ops.business.dataset.dal.mysql.segment;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DocumentSegmentMapper extends BaseMapperX<DocumentSegmentDO> {

    default List<DocumentSegmentDO> selectByDatasetId(String datasetId) {
        LambdaQueryWrapper<DocumentSegmentDO> queryWrapper = Wrappers.lambdaQuery(DocumentSegmentDO.class)
                .eq(DocumentSegmentDO::getDeleted, false)
                .eq(DocumentSegmentDO::getDatasetId, datasetId)
                .orderByAsc(DocumentSegmentDO::getCreateTime);
        return this.selectList(queryWrapper);
    }

    default int updateStatus(String documentId,String status) {
        LambdaUpdateWrapper<DocumentSegmentDO> updateWrapper = Wrappers.lambdaUpdate(DocumentSegmentDO.class)
                .eq(DocumentSegmentDO::getDocumentId, documentId)
                .set(DocumentSegmentDO::getStatus,status);
        return this.update(null,updateWrapper);
    }

    default List<DocumentSegmentDO> segmentDetail(String datasetId,boolean disable, String docId, int lastPosition) {
        LambdaQueryWrapper<DocumentSegmentDO> queryWrapper = Wrappers.lambdaQuery(DocumentSegmentDO.class)
                .eq(DocumentSegmentDO::getDeleted, false)
                .eq(DocumentSegmentDO::getDisabled, disable)
                .eq(DocumentSegmentDO::getDocumentId,docId)
                .eq(DocumentSegmentDO::getDatasetId, datasetId)
                .gt(DocumentSegmentDO::getPosition,lastPosition)
                .last("limit 10");
        return this.selectList(queryWrapper);
    }

    default int updateEnable(String datasetId, String segmentId, boolean enable){
        LambdaUpdateWrapper<DocumentSegmentDO> updateWrapper = Wrappers.lambdaUpdate(DocumentSegmentDO.class)
                .eq(DocumentSegmentDO::getDatasetId, datasetId)
                .eq(DocumentSegmentDO::getId,segmentId)
                .set(DocumentSegmentDO::getDisabled,!enable);
        return this.update(null,updateWrapper);
    }

    default int deleteSegment(String datasetId, String documentId) {
        LambdaUpdateWrapper<DocumentSegmentDO> deleteWrapper = Wrappers.lambdaUpdate(DocumentSegmentDO.class)
                .eq(DocumentSegmentDO::getDatasetId, datasetId)
                .eq(DocumentSegmentDO::getDocumentId,documentId)
                .set(DocumentSegmentDO::getDeleted,true);
        return this.update(null,deleteWrapper);
    }
}
