package com.starcloud.ops.business.poster.dal.mysql.materialcategory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.PosterMaterialCategoryListReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.PosterMaterialCategoryPageReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.materialcategory.PosterMaterialCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 素材分类 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface PosterMaterialCategoryMapper extends BaseMapperX<PosterMaterialCategoryDO> {

    default PageResult<PosterMaterialCategoryDO> selectPage(PosterMaterialCategoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PosterMaterialCategoryDO>()
                .eqIfPresent(PosterMaterialCategoryDO::getParentId, reqVO.getParentId())
                .likeIfPresent(PosterMaterialCategoryDO::getName, reqVO.getName())
                .eqIfPresent(PosterMaterialCategoryDO::getThumbnail, reqVO.getThumbnail())
                .eqIfPresent(PosterMaterialCategoryDO::getSort, reqVO.getSort())
                .betweenIfPresent(PosterMaterialCategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PosterMaterialCategoryDO::getId));
    }
    default List<PosterMaterialCategoryDO> selectList(PosterMaterialCategoryListReqVO listReqVO) {
        return selectList(new LambdaQueryWrapperX<PosterMaterialCategoryDO>()
                .likeIfPresent(PosterMaterialCategoryDO::getName, listReqVO.getName())
                .eqIfPresent(PosterMaterialCategoryDO::getParentId, listReqVO.getParentId())
                .eqIfPresent(PosterMaterialCategoryDO::getStatus, listReqVO.getStatus())
                .orderByDesc(PosterMaterialCategoryDO::getId));
    }


    default Long selectCountByParentId(Long parentId) {
        return selectCount(PosterMaterialCategoryDO::getParentId, parentId);
    }

    default List<PosterMaterialCategoryDO> selectListByStatus(Integer status) {
        return selectList(PosterMaterialCategoryDO::getStatus, status);
    }
}