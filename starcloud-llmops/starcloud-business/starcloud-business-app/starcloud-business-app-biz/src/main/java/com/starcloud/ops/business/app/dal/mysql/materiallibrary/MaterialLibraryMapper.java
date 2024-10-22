package com.starcloud.ops.business.app.dal.mysql.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryPageRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 素材知识库 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MaterialLibraryMapper extends BaseMapperX<MaterialLibraryDO> {

    default PageResult<MaterialLibraryDO> selectPage(Long userId, MaterialLibraryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MaterialLibraryDO>()
                .eqIfPresent(MaterialLibraryDO::getCreator, userId)
                .likeIfPresent(MaterialLibraryDO::getName, reqVO.getName())
                .eqIfPresent(MaterialLibraryDO::getIconUrl, reqVO.getIconUrl())
                .eqIfPresent(MaterialLibraryDO::getDescription, reqVO.getDescription())
                .eqIfPresent(MaterialLibraryDO::getFormatType, reqVO.getFormatType())
                .eqIfPresent(MaterialLibraryDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MaterialLibraryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MaterialLibraryDO::getId));
    }


    default PageResult<MaterialLibraryDO> selectPage2(MaterialLibraryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MaterialLibraryDO>()
                .likeIfPresent(MaterialLibraryDO::getName, reqVO.getName())
                .eqIfPresent(MaterialLibraryDO::getIconUrl, reqVO.getIconUrl())
                .eqIfPresent(MaterialLibraryDO::getDescription, reqVO.getDescription())
                .eqIfPresent(MaterialLibraryDO::getFormatType, reqVO.getFormatType())
                .eqIfPresent(MaterialLibraryDO::getStatus, reqVO.getStatus())
                .eqIfPresent(MaterialLibraryDO::getLibraryType, reqVO.getLibraryType())
                .betweenIfPresent(MaterialLibraryDO::getCreateTime, reqVO.getCreateTime()));
    }

    default MaterialLibraryDO selectByUid(String uid) {
        return selectOne(new LambdaQueryWrapperX<MaterialLibraryDO>()
                .eq(MaterialLibraryDO::getUid, uid));
    }


    default MaterialLibraryDO selectByIdAndUser(Long userId, Long Id) {
        return selectOne(new LambdaQueryWrapperX<MaterialLibraryDO>()
                .eq(MaterialLibraryDO::getId, Id)
                .eq(MaterialLibraryDO::getCreator, userId)
        );
    }


    IPage<MaterialLibraryPageRespVO> selectPage3(Page<?> page, @Param("query") MaterialLibraryPageReqVO reqVO);

}