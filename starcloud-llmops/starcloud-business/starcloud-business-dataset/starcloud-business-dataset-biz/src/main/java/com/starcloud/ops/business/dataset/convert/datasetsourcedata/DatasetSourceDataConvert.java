package com.starcloud.ops.business.dataset.convert.datasetsourcedata;

import cn.hutool.db.sql.Order;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.*;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.service.dto.DataSourceInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据集源数据 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface DatasetSourceDataConvert {

    DatasetSourceDataConvert INSTANCE = Mappers.getMapper(DatasetSourceDataConvert.class);

    DatasetSourceDataDO convert(DatasetSourceDataCreateReqVO bean);

    DatasetSourceDataDO convert(DatasetSourceDataUpdateReqVO bean);

    ListDatasetSourceDataRespVO convert(DatasetSourceDataDO bean);

    List<ListDatasetSourceDataRespVO> convertList(List<DatasetSourceDataDO> list);

    PageResult<ListDatasetSourceDataRespVO> convertPage(PageResult<DatasetSourceDataDO> page);


    default DatasetSourceDataBasicInfoVO convertBasicInfo(DatasetSourceDataDO bean){
        if ( bean == null ) {
            return null;
        }

        DatasetSourceDataBasicInfoVO basicInfoVO = new DatasetSourceDataBasicInfoVO();

        basicInfoVO.setId( bean.getId() );
        basicInfoVO.setUid( bean.getUid() );
        basicInfoVO.setName( bean.getName() );
        basicInfoVO.setDescription( bean.getDescription() );
        basicInfoVO.setDataType( bean.getDataType() );
        basicInfoVO.setStatus( bean.getStatus() );
        basicInfoVO.setCreateTime( bean.getCreateTime() );
        basicInfoVO.setUpdateTime( bean.getUpdateTime() );
        basicInfoVO.setCleanId( bean.getCleanStorageId() );
        if (DataSourceDataTypeEnum.URL.name().equals(bean.getDataType())){
            DataSourceInfoDTO dataSourceInfoDTO = JSONObject.parseObject(bean.getDataSourceInfo(), DataSourceInfoDTO.class);
            basicInfoVO.setAddress(dataSourceInfoDTO.getInitAddress());
        }
        return basicInfoVO;
    }

    default List<DatasetSourceDataBasicInfoVO> convertBasicInfoList(List<DatasetSourceDataDO> list){
        if ( list == null ) {
            return null;
        }

        List<DatasetSourceDataBasicInfoVO> list1 = new ArrayList<DatasetSourceDataBasicInfoVO>( list.size() );
        for ( DatasetSourceDataDO datasetSourceDataDO : list ) {
            list1.add( convertBasicInfo( datasetSourceDataDO ) );
        }

        return list1;
    }





    default DatasetSourceDataSplitPageRespVO convert(DocumentSegmentDO bean){
        if ( bean == null ) {
            return null;
        }

        DatasetSourceDataSplitPageRespVO splitPageRespVO = new DatasetSourceDataSplitPageRespVO();

        splitPageRespVO.setDatasetId( bean.getDatasetId() );
        splitPageRespVO.setUid( bean.getDocumentId() );
        splitPageRespVO.setId( bean.getId() );
        splitPageRespVO.setPosition( bean.getPosition() );
        splitPageRespVO.setWordCount( bean.getWordCount() );
        splitPageRespVO.setSegmentHash( bean.getSegmentHash() );
        splitPageRespVO.setStatus( bean.getStatus() );
        splitPageRespVO.setContent( bean.getContent() );
        splitPageRespVO.setDisabled( bean.getDisabled() );
        return splitPageRespVO;
    }

    default PageResult<DatasetSourceDataSplitPageRespVO> convertSplitPage(PageResult<DocumentSegmentDO> page){

        if ( page == null ) {
            return null;
        }

        PageResult<DatasetSourceDataSplitPageRespVO> pageResult = new PageResult<DatasetSourceDataSplitPageRespVO>();

        pageResult.setList( convertSplitList( page.getList() ) );
        pageResult.setTotal( page.getTotal() );

        return pageResult;
    }
    default List<DatasetSourceDataSplitPageRespVO> convertSplitList(List<DocumentSegmentDO> list){

        if ( list == null ) {
            return null;
        }

        List<DatasetSourceDataSplitPageRespVO> list1 = new ArrayList<DatasetSourceDataSplitPageRespVO>( list.size() );
        for ( DocumentSegmentDO documentSegmentDO : list ) {
            list1.add( convert( documentSegmentDO ) );
        }

        return list1;
    }

}