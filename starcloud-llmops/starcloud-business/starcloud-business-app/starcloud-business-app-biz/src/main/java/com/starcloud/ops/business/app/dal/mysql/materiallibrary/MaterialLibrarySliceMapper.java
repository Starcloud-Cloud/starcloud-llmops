package com.starcloud.ops.business.app.dal.mysql.materiallibrary;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySlicePageReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
                .eqIfPresent(MaterialLibrarySliceDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MaterialLibrarySliceDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MaterialLibrarySliceDO::getCreateTime));
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

    default Long selectSliceDataCountByLibraryId(Long libraryId) {
        LambdaQueryWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MaterialLibrarySliceDO::getLibraryId, libraryId);
        return selectCount(wrapper);
    }

    default List<MaterialLibrarySliceDO> selectListByLibraryId(Long libraryId) {
        LambdaQueryWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MaterialLibrarySliceDO::getLibraryId, libraryId);
        return selectList(wrapper);
    }

    default List<MaterialLibrarySliceDO> selectList(Long libraryId, List<Long> ids) {
        LambdaQueryWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MaterialLibrarySliceDO::getLibraryId, libraryId);
        wrapper.in(MaterialLibrarySliceDO::getId, ids);
        return selectList(wrapper);
    }

    default void deleteSliceByLibraryId(Long libraryId) {
        LambdaQueryWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MaterialLibrarySliceDO::getLibraryId, libraryId);
        delete(wrapper);
    }

    default List<MaterialLibrarySliceDO> selectSliceListByUserLibraryId(Long libraryId,
                                                                        Collection<Long> sliceIdList,
                                                                        Collection<Long> removeSliceIdList,
                                                                        SortingField sortingField) {

        LambdaQueryWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MaterialLibrarySliceDO::getLibraryId, libraryId);
        wrapper.in(Objects.nonNull(sliceIdList) && CollUtil.isNotEmpty(sliceIdList), MaterialLibrarySliceDO::getId, sliceIdList);
        wrapper.notIn(Objects.nonNull(removeSliceIdList) && CollUtil.isNotEmpty(removeSliceIdList), MaterialLibrarySliceDO::getId, removeSliceIdList);

        if (Objects.nonNull(sortingField)) {

            switch (sortingField.getField()) {
                case MaterialLibrarySliceAppReqVO.SORT_FIELD_USED_COUNT:
                    if (sortingField.getOrder().equals(SortingField.ORDER_ASC)) {
                        wrapper.orderByAsc(MaterialLibrarySliceDO::getUsedCount);
                    } else {
                        wrapper.orderByDesc(MaterialLibrarySliceDO::getUsedCount);
                    }
                    break;
                case MaterialLibrarySliceAppReqVO.SORT_FIELD_CREATE_TIME:

                    if (sortingField.getOrder().equals(SortingField.ORDER_ASC)) {
                        wrapper.orderByAsc(MaterialLibrarySliceDO::getCreateTime);
                    } else {
                        wrapper.orderByDesc(MaterialLibrarySliceDO::getCreateTime);
                    }
                    break;
                case MaterialLibrarySliceAppReqVO.SORT_FIELD_UPDATE_TIME:

                    if (sortingField.getOrder().equals(SortingField.ORDER_ASC)) {
                        wrapper.orderByAsc(MaterialLibrarySliceDO::getUpdateTime);
                    } else {
                        wrapper.orderByDesc(MaterialLibrarySliceDO::getUpdateTime);
                    }
                    break;
                default:
                    wrapper.orderByAsc(MaterialLibrarySliceDO::getCreateTime);
                    break;
            }
        } else {
            wrapper.orderByAsc(MaterialLibrarySliceDO::getCreateTime);
        }

        return selectList(wrapper);
    }

    default PageResult<MaterialLibrarySliceDO> selectPage2(Long libraryId, MaterialLibrarySliceAppPageReqVO appPageReqVO) {
        return selectPage(appPageReqVO, new LambdaQueryWrapperX<MaterialLibrarySliceDO>()
                .eq(MaterialLibrarySliceDO::getLibraryId, libraryId)
                .orderByDesc(MaterialLibrarySliceDO::getCreateTime)
        );
    }

    default Long selectCountByLibraryId(Long libraryId){
        LambdaQueryWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MaterialLibrarySliceDO::getLibraryId, libraryId);
        return selectCount(wrapper);
    }
}