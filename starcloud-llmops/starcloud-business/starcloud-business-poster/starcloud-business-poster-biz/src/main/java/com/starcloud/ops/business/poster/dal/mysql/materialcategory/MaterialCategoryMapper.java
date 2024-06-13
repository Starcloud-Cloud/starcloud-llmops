package com.starcloud.ops.business.poster.dal.mysql.materialcategory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategoryListReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategoryPageReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.materialcategory.MaterialCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 素材分类 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MaterialCategoryMapper extends BaseMapperX<MaterialCategoryDO> {

    default PageResult<MaterialCategoryDO> selectPage(MaterialCategoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MaterialCategoryDO>()
                .eqIfPresent(MaterialCategoryDO::getParentId, reqVO.getParentId())
                .likeIfPresent(MaterialCategoryDO::getName, reqVO.getName())
                .eqIfPresent(MaterialCategoryDO::getThumbnail, reqVO.getThumbnail())
                .eqIfPresent(MaterialCategoryDO::getSort, reqVO.getSort())
                .betweenIfPresent(MaterialCategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MaterialCategoryDO::getId));
    }
    default List<MaterialCategoryDO> selectList(MaterialCategoryListReqVO listReqVO) {
        return selectList(new LambdaQueryWrapperX<MaterialCategoryDO>()
                .likeIfPresent(MaterialCategoryDO::getName, listReqVO.getName())
                .eqIfPresent(MaterialCategoryDO::getParentId, listReqVO.getParentId())
                .eqIfPresent(MaterialCategoryDO::getStatus, listReqVO.getStatus())
                .orderByDesc(MaterialCategoryDO::getId));
    }


    default Long selectCountByParentId(Long parentId) {
        return selectCount(MaterialCategoryDO::getParentId, parentId);
    }

    default List<MaterialCategoryDO> selectListByStatus(Integer status) {
        return selectList(MaterialCategoryDO::getStatus, status);
    }
}