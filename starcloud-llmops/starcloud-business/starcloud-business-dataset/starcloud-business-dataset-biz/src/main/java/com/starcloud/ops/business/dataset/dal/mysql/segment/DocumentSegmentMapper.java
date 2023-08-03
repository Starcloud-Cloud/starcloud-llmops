package com.starcloud.ops.business.dataset.dal.mysql.segment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.pojo.request.SegmentPageQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface DocumentSegmentMapper extends BaseMapperX<DocumentSegmentDO> {

    default List<DocumentSegmentDO> selectByDatasetIds(List<String> datasetIds) {
        LambdaQueryWrapper<DocumentSegmentDO> queryWrapper = Wrappers.lambdaQuery(DocumentSegmentDO.class)
                .eq(DocumentSegmentDO::getDeleted, false)
                .in(DocumentSegmentDO::getDatasetId,datasetIds)
                .orderByAsc(DocumentSegmentDO::getCreateTime);
        return this.selectList(queryWrapper);
    }

    default List<DocumentSegmentDO> selectByDocId(String DocumentId) {
        LambdaQueryWrapper<DocumentSegmentDO> queryWrapper = Wrappers.lambdaQuery(DocumentSegmentDO.class)
                .eq(DocumentSegmentDO::getDeleted, false)
                .eq(DocumentSegmentDO::getDocumentId,DocumentId)
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
                .eq(DocumentSegmentDO::getDocumentId, datasetId)
                .eq(DocumentSegmentDO::getId,segmentId)
                .set(DocumentSegmentDO::getDisabled,!enable);
        return this.update(null,updateWrapper);
    }

    default PageResult<DocumentSegmentDO> page(SegmentPageQuery segmentPageQuery) {
        LambdaQueryWrapper<DocumentSegmentDO> queryWrapper = Wrappers.lambdaQuery(DocumentSegmentDO.class)
                .eq(DocumentSegmentDO::getDatasetId,segmentPageQuery.getDatasetUid())
                .eq(DocumentSegmentDO::getDocumentId,segmentPageQuery.getDocumentUid())
                .orderByAsc(DocumentSegmentDO::getPosition)
                ;

        return selectPage(segmentPageQuery, queryWrapper);
    }

    default int deleteSegment(String datasetId, String documentId) {
        LambdaUpdateWrapper<DocumentSegmentDO> deleteWrapper = Wrappers.lambdaUpdate(DocumentSegmentDO.class)
                .eq(DocumentSegmentDO::getDatasetId, datasetId)
                .eq(DocumentSegmentDO::getDocumentId,documentId)
                .set(DocumentSegmentDO::getDeleted,true);
        return this.update(null,deleteWrapper);
    }
}
