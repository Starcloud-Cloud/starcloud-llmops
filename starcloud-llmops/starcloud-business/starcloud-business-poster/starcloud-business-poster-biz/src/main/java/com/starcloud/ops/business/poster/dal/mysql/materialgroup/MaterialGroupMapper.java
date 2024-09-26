
package com.starcloud.ops.business.poster.dal.mysql.materialgroup;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupRespVO;
import com.starcloud.ops.business.poster.dal.dataobject.materialgroup.MaterialGroupDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 海报素材分组 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MaterialGroupMapper extends BaseMapperX<MaterialGroupDO> {

    // PageResult<MaterialGroupRespVO> selectPage(MaterialGroupPageReqVO reqVO);

    // return selectPage(reqVO, new LambdaQueryWrapperX<MaterialGroupDO>()
    // List<MaterialGroupRespVO> selectPage(@Param("reqVO") MaterialGroupPageReqVO reqVO, @Param("start") Integer start, @Param("size") Integer size);

    IPage<MaterialGroupRespVO> selectPage(Page<?> page, @Param("reqVO") MaterialGroupPageReqVO reqVO);


}