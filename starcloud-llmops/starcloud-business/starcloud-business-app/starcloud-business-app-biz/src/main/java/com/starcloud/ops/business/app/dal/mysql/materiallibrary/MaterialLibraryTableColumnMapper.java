package com.starcloud.ops.business.app.dal.mysql.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Objects;

/**
 * 素材知识库表格信息 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MaterialLibraryTableColumnMapper extends BaseMapperX<MaterialLibraryTableColumnDO> {

    default PageResult<MaterialLibraryTableColumnDO> selectPage(MaterialLibraryTableColumnPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MaterialLibraryTableColumnDO>()
                .eqIfPresent(MaterialLibraryTableColumnDO::getLibraryId, reqVO.getLibraryId())
                .likeIfPresent(MaterialLibraryTableColumnDO::getColumnName, reqVO.getColumnName())
                .eqIfPresent(MaterialLibraryTableColumnDO::getColumnType, reqVO.getColumnType())
                .eqIfPresent(MaterialLibraryTableColumnDO::getDescription, reqVO.getDescription())
                .eqIfPresent(MaterialLibraryTableColumnDO::getIsRequired, reqVO.getIsRequired())
                .eqIfPresent(MaterialLibraryTableColumnDO::getSequence, reqVO.getSequence())
                .betweenIfPresent(MaterialLibraryTableColumnDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MaterialLibraryTableColumnDO::getId));
    }

    default List<MaterialLibraryTableColumnDO> selectMaterialLibraryTableColumnByLibrary(Long libraryId) {
        LambdaQueryWrapper<MaterialLibraryTableColumnDO> wrapper = Wrappers.lambdaQuery(MaterialLibraryTableColumnDO.class);
        wrapper.eq(MaterialLibraryTableColumnDO::getLibraryId, libraryId);
        wrapper.orderByAsc(MaterialLibraryTableColumnDO::getSequence);
        return selectList(wrapper);
    }

    default void deleteByLibraryId(Long libraryId) {
        LambdaUpdateWrapper<MaterialLibraryTableColumnDO> wrapper = Wrappers.lambdaUpdate(MaterialLibraryTableColumnDO.class);
        wrapper.eq(MaterialLibraryTableColumnDO::getLibraryId, libraryId);
        delete(wrapper);
    }


    default void deleteByLibraryId(Long libraryId, List<String> columnCodeList) {
        LambdaUpdateWrapper<MaterialLibraryTableColumnDO> wrapper = Wrappers.lambdaUpdate(MaterialLibraryTableColumnDO.class);
        wrapper.eq(MaterialLibraryTableColumnDO::getLibraryId, libraryId)
                .in(MaterialLibraryTableColumnDO::getColumnCode, columnCodeList)
        ;
        delete(wrapper);
    }

    default int selectCountByName(Long libraryId, Long ignoreId, List<String> columnName) {
        return Math.toIntExact(selectCount(new LambdaQueryWrapper<>(MaterialLibraryTableColumnDO.class)
                .eq(MaterialLibraryTableColumnDO::getLibraryId, libraryId)
                .notIn(Objects.nonNull(ignoreId), MaterialLibraryTableColumnDO::getId, ignoreId)
                .in(MaterialLibraryTableColumnDO::getColumnName, columnName)));
    }
}