package com.starcloud.ops.business.poster.dal.mysql.material;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialPageReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.MaterialDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 海报素材 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MaterialMapper extends BaseMapperX<MaterialDO> {

    default PageResult<MaterialDO> selectPage(MaterialPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MaterialDO>()
                .eqIfPresent(MaterialDO::getUid, reqVO.getUid())
                .likeIfPresent(MaterialDO::getName, reqVO.getName())
                .eqIfPresent(MaterialDO::getThumbnail, reqVO.getThumbnail())
                .eqIfPresent(MaterialDO::getType, reqVO.getType())
                .eqIfPresent(MaterialDO::getMaterialTags, reqVO.getMaterialTags())
                .eqIfPresent(MaterialDO::getMaterialData, reqVO.getMaterialData())
                .eqIfPresent(MaterialDO::getRequestParams, reqVO.getRequestParams())
                .eqIfPresent(MaterialDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(MaterialDO::getStatus, reqVO.getStatus())
                .eqIfPresent(MaterialDO::getSort, reqVO.getSort())
                .eqIfPresent(MaterialDO::getUserType, reqVO.getUserType())
                .betweenIfPresent(MaterialDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MaterialDO::getId));
    }

}