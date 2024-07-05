package com.starcloud.ops.business.app.dal.mysql.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 素材知识库 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MaterialLibraryMapper extends BaseMapperX<MaterialLibraryDO> {

    default PageResult<MaterialLibraryDO> selectPage(MaterialLibraryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MaterialLibraryDO>()
                .likeIfPresent(MaterialLibraryDO::getName, reqVO.getName())
                .eqIfPresent(MaterialLibraryDO::getIconUrl, reqVO.getIconUrl())
                .eqIfPresent(MaterialLibraryDO::getDescription, reqVO.getDescription())
                .eqIfPresent(MaterialLibraryDO::getFormatType, reqVO.getFormatType())
                .eqIfPresent(MaterialLibraryDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MaterialLibraryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MaterialLibraryDO::getId));
    }

}