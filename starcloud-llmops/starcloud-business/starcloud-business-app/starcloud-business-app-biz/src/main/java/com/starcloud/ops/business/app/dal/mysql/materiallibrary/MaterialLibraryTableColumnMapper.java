package com.starcloud.ops.business.app.dal.mysql.materiallibrary;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import org.apache.ibatis.annotations.Mapper;

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

    default List<MaterialLibraryTableColumnDO> selectMaterialLibraryTableColumnByLibrary(Long libraryId){
        LambdaQueryWrapper<MaterialLibraryTableColumnDO> wrapper = Wrappers.lambdaQuery(MaterialLibraryTableColumnDO.class);
        wrapper.eq(MaterialLibraryTableColumnDO::getLibraryId, libraryId);
        wrapper.orderByDesc(MaterialLibraryTableColumnDO::getSequence);
        return selectList(wrapper);
    }

   default void deleteByLibraryId(Long libraryId){

   }
}