package com.starcloud.ops.business.app.dal.mysql.materiallibrary;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySlicePageReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 素材知识库数据 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MaterialLibrarySliceMapper extends BaseMapperX<MaterialLibrarySliceDO> {

    default PageResult<MaterialLibrarySliceDO> selectPage(MaterialLibrarySlicePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MaterialLibrarySliceDO>()
                .eqIfPresent(MaterialLibrarySliceDO::getLibraryId, reqVO.getLibraryId())
                .eqIfPresent(MaterialLibrarySliceDO::getCharCount, reqVO.getCharCount())
                .eqIfPresent(MaterialLibrarySliceDO::getUsedCount, reqVO.getUsedCount())
                .eqIfPresent(MaterialLibrarySliceDO::getContent, reqVO.getContent())
                .eqIfPresent(MaterialLibrarySliceDO::getSequence, reqVO.getSequence())
                .eqIfPresent(MaterialLibrarySliceDO::getUrl, reqVO.getUrl())
                .eqIfPresent(MaterialLibrarySliceDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MaterialLibrarySliceDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MaterialLibrarySliceDO::getId));
    }

    default MaterialLibrarySliceDO selectLastSequence(Long libraryId) {

        LambdaQueryWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MaterialLibrarySliceDO::getLibraryId, libraryId);
        wrapper.orderByDesc(MaterialLibrarySliceDO::getSequence);
        wrapper.last("LIMIT 1");

        return selectOne(wrapper);
    }

    default List<MaterialLibrarySliceDO> selectSliceShareData(Long libraryId) {

        LambdaQueryWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MaterialLibrarySliceDO::getLibraryId, libraryId);
        wrapper.eq(MaterialLibrarySliceDO::getIsShare, CommonStatusEnum.ENABLE.getStatus());

        return selectList(wrapper);
    }

    default Long selectSliceDataCountByLibraryId(Long libraryId){
        LambdaQueryWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MaterialLibrarySliceDO::getLibraryId, libraryId);
        return selectCount(wrapper);
    }

   default List<MaterialLibrarySliceDO> selectListByLibraryId(Long libraryId){
       LambdaQueryWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaQuery();
       wrapper.eq(MaterialLibrarySliceDO::getLibraryId, libraryId);
       return selectList(wrapper);
   }
}