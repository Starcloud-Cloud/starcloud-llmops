package com.starcloud.ops.business.dataset.convert.datasetstorage;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import lombok.experimental.UtilityClass;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据集源数据存储 Convert
 *
 * @author AlanCusack
 */
@Mapper
public interface DatasetStorageConvert {

    DatasetStorageConvert INSTANCE = Mappers.getMapper(DatasetStorageConvert.class);

    DatasetStorageDO convert(DatasetStorageCreateReqVO bean);

    DatasetStorageDO convert(DatasetStorageUpdateReqVO bean);

    DatasetStorageRespVO convert(DatasetStorageDO bean);

    List<DatasetStorageRespVO> convertList(List<DatasetStorageDO> list);

    List<DatasetStorageDO> convertCreateList(List<DatasetStorageCreateReqVO> list);



    PageResult<DatasetStorageRespVO> convertPage(PageResult<DatasetStorageDO> page);




}