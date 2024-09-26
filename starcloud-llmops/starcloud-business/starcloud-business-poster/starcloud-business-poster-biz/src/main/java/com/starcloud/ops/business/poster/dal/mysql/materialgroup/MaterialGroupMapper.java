
package com.starcloud.ops.business.poster.dal.mysql.materialgroup;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPageReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.MaterialDO;
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

        MPJLambdaWrapperX<MaterialGroupDO> query = new MPJLambdaWrapperX<MaterialGroupDO>()
                .eqIfPresent(MaterialGroupDO::getUid, reqVO.getUid())
                .likeIfPresent(MaterialGroupDO::getName, reqVO.getName())
                .eqIfPresent(MaterialGroupDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(MaterialGroupDO::getOvertStatus, reqVO.getOvertStatus())
                .eqIfPresent(MaterialGroupDO::getType, reqVO.getType())
                .eqIfPresent(MaterialGroupDO::getUserType, reqVO.getUserType())
                .betweenIfPresent(MaterialGroupDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MaterialGroupDO::getId);

        query.selectAll(MaterialGroupDO.class);
        query.leftJoin(MaterialDO.class, MaterialDO::getGroupId, MaterialGroupDO::getId)
                .groupBy(MaterialGroupDO::getId)  // 添加分组操作
                .selectCount(MaterialDO::getGroupId, "materialCount");  // 添加统计操作

        return selectJoinPage(reqVO, MaterialGroupDO.class, query);

        // return selectPage(reqVO, new LambdaQueryWrapperX<MaterialGroupDO>()

    }


}