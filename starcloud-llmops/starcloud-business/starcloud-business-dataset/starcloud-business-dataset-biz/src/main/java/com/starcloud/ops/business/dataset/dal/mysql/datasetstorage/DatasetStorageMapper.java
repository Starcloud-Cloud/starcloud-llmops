package com.starcloud.ops.business.dataset.dal.mysql.datasetstorage;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStoragePageReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import org.apache.ibatis.annotations.Mapper;
/**
 * 数据集源数据存储 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DatasetStorageMapper extends BaseMapperX<DatasetStorageDO> {

    default PageResult<DatasetStorageDO> selectPage(DatasetStoragePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DatasetStorageDO>()
                .eqIfPresent(DatasetStorageDO::getUid, reqVO.getUid())
                .likeIfPresent(DatasetStorageDO::getName, reqVO.getName())
                .eqIfPresent(DatasetStorageDO::getType, reqVO.getType())
                .eqIfPresent(DatasetStorageDO::getStorageKey, reqVO.getStorageKey())
                .eqIfPresent(DatasetStorageDO::getStorageType, reqVO.getStorageType())
                .eqIfPresent(DatasetStorageDO::getSize, reqVO.getSize())
                .eqIfPresent(DatasetStorageDO::getMimeType, reqVO.getMimeType())
                .eqIfPresent(DatasetStorageDO::getUsed, reqVO.getUsed())
                .eqIfPresent(DatasetStorageDO::getUsedBy, reqVO.getUsedBy())
                .eqIfPresent(DatasetStorageDO::getUsedAt, reqVO.getUsedAt())
                .eqIfPresent(DatasetStorageDO::getHash, reqVO.getHash())
                .betweenIfPresent(DatasetStorageDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DatasetStorageDO::getId));
    }
}