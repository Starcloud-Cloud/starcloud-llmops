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
@Mapper
public interface DatasetSourceDataConvert {

    DatasetSourceDataConvert INSTANCE = Mappers.getMapper(DatasetSourceDataConvert.class);

    DatasetSourceDataDO convert(DatasetSourceDataCreateReqVO bean);

    DatasetSourceDataDO convert(DatasetSourceDataUpdateReqVO bean);

    DatasetSourceDataRespVO convert(DatasetSourceDataDO bean);

    List<DatasetSourceDataRespVO> convertList(List<DatasetSourceDataDO> list);

    PageResult<DatasetSourceDataRespVO> convertPage(PageResult<DatasetSourceDataDO> page);

}