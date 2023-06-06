package com.starcloud.ops.business.dataset.convert.datasetsourcedata;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import lombok.experimental.UtilityClass;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 数据集源数据 Convert
 *
 * @author 芋道源码
 */
@UtilityClass
public class DatasetSourceDataConvert {

    public DatasetSourceDataDO convert(DatasetSourceDataCreateReqVO bean) {
        if ( bean == null ) {
            return null;
        }

        DatasetSourceDataDO.DatasetSourceDataDOBuilder datasetSourceDataDO = DatasetSourceDataDO.builder();
        //datasetSourceDataDO.datasetProcessRuleId( bean.getDatasetProcessRuleId() );

        return datasetSourceDataDO.build();
    }

    public DatasetSourceDataDO convert(DatasetSourceDataUpdateReqVO bean) {
        if ( bean == null ) {
            return null;
        }

        DatasetSourceDataDO.DatasetSourceDataDOBuilder datasetSourceDataDO = DatasetSourceDataDO.builder();

        datasetSourceDataDO.id( bean.getId() );
        datasetSourceDataDO.uid( bean.getUid() );
        datasetSourceDataDO.name( bean.getName() );
        datasetSourceDataDO.storageId( bean.getStorageId() );
        datasetSourceDataDO.position( bean.getPosition() );
        datasetSourceDataDO.dataSourceType( bean.getDataSourceType() );
        datasetSourceDataDO.dataSourceInfo( bean.getDataSourceInfo() );
        datasetSourceDataDO.datasetProcessRuleId( bean.getDatasetProcessRuleId() );
        datasetSourceDataDO.batch( bean.getBatch() );
        datasetSourceDataDO.createdFrom( bean.getCreatedFrom() );
        datasetSourceDataDO.wordCount( bean.getWordCount() );
        datasetSourceDataDO.tokens( bean.getTokens() );
        datasetSourceDataDO.datasetId( bean.getDatasetId() );
        datasetSourceDataDO.createdApiRequestId( bean.getCreatedApiRequestId() );
        datasetSourceDataDO.parsingCompletedTime( bean.getParsingCompletedTime() );
        datasetSourceDataDO.cleaningCompletedTime( bean.getCleaningCompletedTime() );
        datasetSourceDataDO.splittingCompletedTime( bean.getSplittingCompletedTime() );
        datasetSourceDataDO.indexingTime( bean.getIndexingTime() );
        datasetSourceDataDO.processingStartedTime( bean.getProcessingStartedTime() );
        datasetSourceDataDO.completedAt( bean.getCompletedAt() );
        datasetSourceDataDO.errorMessage( bean.getErrorMessage() );
        datasetSourceDataDO.stoppedTime( bean.getStoppedTime() );
        datasetSourceDataDO.pausedBy( bean.getPausedBy() );
        datasetSourceDataDO.pausedTime( bean.getPausedTime() );
        datasetSourceDataDO.disabledAt( bean.getDisabledAt() );
        datasetSourceDataDO.disabledTime( bean.getDisabledTime() );
        datasetSourceDataDO.indexingStatus( bean.getIndexingStatus() );
        datasetSourceDataDO.enabled( bean.getEnabled() );
        datasetSourceDataDO.docType( bean.getDocType() );
        datasetSourceDataDO.docMetadata( bean.getDocMetadata() );
        datasetSourceDataDO.archived( bean.getArchived() );
        datasetSourceDataDO.archivedBy( bean.getArchivedBy() );
        datasetSourceDataDO.archivedReason( bean.getArchivedReason() );
        datasetSourceDataDO.isPaused( bean.getIsPaused() );
        datasetSourceDataDO.archivedTime( bean.getArchivedTime() );

        return datasetSourceDataDO.build();
    }

    public DatasetSourceDataRespVO convert(DatasetSourceDataDO bean) {
        if ( bean == null ) {
            return null;
        }

        DatasetSourceDataRespVO datasetSourceDataRespVO = new DatasetSourceDataRespVO();

        datasetSourceDataRespVO.setUid( bean.getUid() );
        datasetSourceDataRespVO.setName( bean.getName() );
        datasetSourceDataRespVO.setStorageId( bean.getStorageId() );
        datasetSourceDataRespVO.setPosition( bean.getPosition() );
        datasetSourceDataRespVO.setDataSourceType( bean.getDataSourceType() );
        datasetSourceDataRespVO.setDataSourceInfo( bean.getDataSourceInfo() );
        datasetSourceDataRespVO.setDatasetProcessRuleId( bean.getDatasetProcessRuleId() );
        datasetSourceDataRespVO.setBatch( bean.getBatch() );
        datasetSourceDataRespVO.setCreatedFrom( bean.getCreatedFrom() );
        datasetSourceDataRespVO.setWordCount( bean.getWordCount() );
        datasetSourceDataRespVO.setTokens( bean.getTokens() );
        datasetSourceDataRespVO.setDatasetId( bean.getDatasetId() );
        datasetSourceDataRespVO.setCreatedApiRequestId( bean.getCreatedApiRequestId() );
        datasetSourceDataRespVO.setParsingCompletedTime( bean.getParsingCompletedTime() );
        datasetSourceDataRespVO.setCleaningCompletedTime( bean.getCleaningCompletedTime() );
        datasetSourceDataRespVO.setSplittingCompletedTime( bean.getSplittingCompletedTime() );
        datasetSourceDataRespVO.setIndexingTime( bean.getIndexingTime() );
        datasetSourceDataRespVO.setProcessingStartedTime( bean.getProcessingStartedTime() );
        datasetSourceDataRespVO.setCompletedAt( bean.getCompletedAt() );
        datasetSourceDataRespVO.setErrorMessage( bean.getErrorMessage() );
        datasetSourceDataRespVO.setStoppedTime( bean.getStoppedTime() );
        datasetSourceDataRespVO.setPausedBy( bean.getPausedBy() );
        datasetSourceDataRespVO.setPausedTime( bean.getPausedTime() );
        datasetSourceDataRespVO.setDisabledAt( bean.getDisabledAt() );
        datasetSourceDataRespVO.setDisabledTime( bean.getDisabledTime() );
        datasetSourceDataRespVO.setIndexingStatus( bean.getIndexingStatus() );
        datasetSourceDataRespVO.setEnabled( bean.getEnabled() );
        datasetSourceDataRespVO.setDocType( bean.getDocType() );
        datasetSourceDataRespVO.setDocMetadata( bean.getDocMetadata() );
        datasetSourceDataRespVO.setArchived( bean.getArchived() );
        datasetSourceDataRespVO.setArchivedBy( bean.getArchivedBy() );
        datasetSourceDataRespVO.setArchivedReason( bean.getArchivedReason() );
        datasetSourceDataRespVO.setIsPaused( bean.getIsPaused() );
        datasetSourceDataRespVO.setArchivedTime( bean.getArchivedTime() );
        datasetSourceDataRespVO.setId( bean.getId() );
        datasetSourceDataRespVO.setCreateTime( bean.getCreateTime() );

        return datasetSourceDataRespVO;
    }


    public List<DatasetSourceDataRespVO> convertList(List<DatasetSourceDataDO> list) {
        if ( list == null ) {
            return null;
        }

        List<DatasetSourceDataRespVO> list1 = new ArrayList<DatasetSourceDataRespVO>( list.size() );
        for ( DatasetSourceDataDO datasetSourceDataDO : list ) {
            list1.add( convert( datasetSourceDataDO ) );
        }

        return list1;
    }

    public PageResult<DatasetSourceDataRespVO> convertPage(PageResult<DatasetSourceDataDO> page) {
        if ( page == null ) {
            return null;
        }

        PageResult<DatasetSourceDataRespVO> pageResult = new PageResult<DatasetSourceDataRespVO>();

        pageResult.setList( convertList( page.getList() ) );
        pageResult.setTotal( page.getTotal() );

        return pageResult;
    }

}