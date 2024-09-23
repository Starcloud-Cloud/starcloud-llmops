
package com.starcloud.ops.business.poster.dal.mysql.materialgroup;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPageReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.materialgroup.MaterialGroupDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 海报素材分组 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MaterialGroupMapper extends BaseMapperX<MaterialGroupDO> {

    default PageResult<MaterialGroupDO> selectPage(MaterialGroupPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MaterialGroupDO>()
                .eqIfPresent(MaterialGroupDO::getUid, reqVO.getUid())
                .likeIfPresent(MaterialGroupDO::getName, reqVO.getName())
                .eqIfPresent(MaterialGroupDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(MaterialGroupDO::getOvertStatus, reqVO.getOvertStatus())
                .eqIfPresent(MaterialGroupDO::getType, reqVO.getType())
                .eqIfPresent(MaterialGroupDO::getUserType, reqVO.getUserType())
                .betweenIfPresent(MaterialGroupDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MaterialGroupDO::getId));
    }

}