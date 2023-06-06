package com.starcloud.ops.business.dataset.convert.datasetstorage;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 数据集源数据存储 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface DatasetStorageConvert {

    DatasetStorageConvert INSTANCE = Mappers.getMapper(DatasetStorageConvert.class);

    DatasetStorageDO convert(DatasetStorageCreateReqVO bean);

    DatasetStorageDO convert(DatasetStorageUpdateReqVO bean);

    DatasetStorageRespVO convert(DatasetStorageDO bean);

    List<DatasetStorageRespVO> convertList(List<DatasetStorageDO> list);

    PageResult<DatasetStorageRespVO> convertPage(PageResult<DatasetStorageDO> page);

}