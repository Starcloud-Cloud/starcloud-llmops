package com.starcloud.ops.business.dataset.convert.datasetstorage;

import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import lombok.experimental.UtilityClass;

/**
 * 数据集源数据存储 Convert
 *
 * @author AlanCusack
 */
@UtilityClass
public class DatasetStorageConvert {
    public DatasetStorageDO convert(DatasetStorageCreateReqVO bean) {
        if ( bean == null ) {
            return null;
        }

        DatasetStorageDO.DatasetStorageDOBuilder datasetStorageDO = DatasetStorageDO.builder();

        datasetStorageDO.uid( bean.getUid() );
        datasetStorageDO.name( bean.getName() );
        datasetStorageDO.type( bean.getType() );
        datasetStorageDO.storageKey( bean.getStorageKey() );
        datasetStorageDO.storageType( bean.getStorageType() );
        datasetStorageDO.size( bean.getSize() );
        datasetStorageDO.mimeType( bean.getMimeType() );
        datasetStorageDO.used( bean.getUsed() );
        datasetStorageDO.usedBy( bean.getUsedBy() );
        datasetStorageDO.usedAt( bean.getUsedAt() );
        datasetStorageDO.hash( bean.getHash() );

        return datasetStorageDO.build();
    }

    public DatasetStorageDO convert(DatasetStorageUpdateReqVO bean) {
        if ( bean == null ) {
            return null;
        }

        DatasetStorageDO.DatasetStorageDOBuilder datasetStorageDO = DatasetStorageDO.builder();

        datasetStorageDO.id( bean.getId() );
        datasetStorageDO.uid( bean.getUid() );
        datasetStorageDO.name( bean.getName() );
        datasetStorageDO.type( bean.getType() );
        datasetStorageDO.storageKey( bean.getStorageKey() );
        datasetStorageDO.storageType( bean.getStorageType() );
        datasetStorageDO.size( bean.getSize() );
        datasetStorageDO.mimeType( bean.getMimeType() );
        datasetStorageDO.used( bean.getUsed() );
        datasetStorageDO.usedBy( bean.getUsedBy() );
        datasetStorageDO.usedAt( bean.getUsedAt() );
        datasetStorageDO.hash( bean.getHash() );

        return datasetStorageDO.build();
    }

    //public DatasetStorageRespVO convert(DatasetStorageDO bean) {
    //    if ( bean == null ) {
    //        return null;
    //    }
    //
    //    DatasetStorageRespVO datasetStorageRespVO = new DatasetStorageRespVO();
    //
    //    datasetStorageRespVO.setUid( bean.getUid() );
    //    datasetStorageRespVO.setName( bean.getName() );
    //    datasetStorageRespVO.setType( bean.getType() );
    //    datasetStorageRespVO.setKey( bean.getKey() );
    //    datasetStorageRespVO.setStorageType( bean.getStorageType() );
    //    datasetStorageRespVO.setSize( bean.getSize() );
    //    datasetStorageRespVO.setMimeType( bean.getMimeType() );
    //    datasetStorageRespVO.setUsed( bean.getUsed() );
    //    datasetStorageRespVO.setUsedBy( bean.getUsedBy() );
    //    datasetStorageRespVO.setUsedAt( bean.getUsedAt() );
    //    datasetStorageRespVO.setHash( bean.getHash() );
    //
    //    return datasetStorageRespVO;
    //}

    public DatasetStorageUpLoadRespVO convert2LoadRespVO(DatasetStorageDO bean) {
        DatasetStorageUpLoadRespVO datasetStorageUpLoadRespVO = new DatasetStorageUpLoadRespVO();
        datasetStorageUpLoadRespVO.setUid( bean.getUid() );
        datasetStorageUpLoadRespVO.setType( bean.getType() );
        datasetStorageUpLoadRespVO.setName( bean.getName() );
        datasetStorageUpLoadRespVO.setStorageType( bean.getStorageType() );
        datasetStorageUpLoadRespVO.setMimeType( bean.getMimeType() );

        return datasetStorageUpLoadRespVO;
    }


    //public List<DatasetStorageRespVO> convertList(List<DatasetStorageDO> list) {
    //    if ( list == null ) {
    //        return null;
    //    }
    //
    //    List<DatasetStorageRespVO> list1 = new ArrayList<DatasetStorageRespVO>( list.size() );
    //    for ( DatasetStorageDO datasetStorageDO : list ) {
    //        list1.add( convert( datasetStorageDO ) );
    //    }
    //
    //    return list1;
    //}

    //public PageResult<DatasetStorageRespVO> convertPage(PageResult<DatasetStorageDO> page) {
    //    if ( page == null ) {
    //        return null;
    //    }
    //
    //    PageResult<DatasetStorageRespVO> pageResult = new PageResult<DatasetStorageRespVO>();
    //
    //    pageResult.setList( convertList( page.getList() ) );
    //    pageResult.setTotal( page.getTotal() );
    //
    //    return pageResult;
    //}

}