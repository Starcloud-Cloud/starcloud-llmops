package com.starcloud.ops.business.poster.dal.mysql.material;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.poster.controller.admin.material.vo.PosterMaterialPageReqsVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.PosterMaterialPageReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.PosterMaterialDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * 海报素材 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface PosterMaterialMapper extends BaseMapperX<PosterMaterialDO> {

    default PageResult<PosterMaterialDO> selectPage(PosterMaterialPageReqsVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PosterMaterialDO>()
                .eqIfPresent(PosterMaterialDO::getUid, reqVO.getUid())
                .likeIfPresent(PosterMaterialDO::getName, reqVO.getName())
                .eqIfPresent(PosterMaterialDO::getTitle, reqVO.getTitle())
                .eqIfPresent(PosterMaterialDO::getThumbnail, reqVO.getThumbnail())
                .eqIfPresent(PosterMaterialDO::getIntroduction, reqVO.getIntroduction())
                .eqIfPresent(PosterMaterialDO::getType, reqVO.getType())
                .eqIfPresent(PosterMaterialDO::getMaterialTags, reqVO.getMaterialTags())
                .eqIfPresent(PosterMaterialDO::getMaterialData, reqVO.getMaterialData())
                .eqIfPresent(PosterMaterialDO::getRequestParams, reqVO.getRequestParams())
                .eqIfPresent(PosterMaterialDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(PosterMaterialDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PosterMaterialDO::getSort, reqVO.getSort())
                .eqIfPresent(PosterMaterialDO::getUserType, reqVO.getUserType())
                .betweenIfPresent(PosterMaterialDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PosterMaterialDO::getId));
    }

    default PageResult<PosterMaterialDO> selectPage(PosterMaterialPageReqVO pageReqVO, Set<Long> categoryIds) {
        LambdaQueryWrapperX<PosterMaterialDO> query = new LambdaQueryWrapperX<PosterMaterialDO>()
                // 分类
                .inIfPresent(PosterMaterialDO::getCategoryId, categoryIds);
        return selectPage(pageReqVO, query);
    }

}