package com.starcloud.ops.business.dataset.convert.datasetsourcedata;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataSplitPageRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
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

    DatasetSourceDataRespVO convert(DatasetSourceDataDO bean);

    List<DatasetSourceDataRespVO> convertList(List<DatasetSourceDataDO> list);

    PageResult<DatasetSourceDataRespVO> convertPage(PageResult<DatasetSourceDataDO> page);




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