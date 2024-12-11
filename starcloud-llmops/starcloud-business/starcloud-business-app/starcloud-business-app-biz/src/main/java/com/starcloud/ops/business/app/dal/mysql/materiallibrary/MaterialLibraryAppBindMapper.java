package com.starcloud.ops.business.app.dal.mysql.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 应用素材绑定 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MaterialLibraryAppBindMapper extends BaseMapperX<MaterialLibraryAppBindDO> {

    default PageResult<MaterialLibraryAppBindDO> selectPage(MaterialLibraryAppBindPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MaterialLibraryAppBindDO>()
                .eqIfPresent(MaterialLibraryAppBindDO::getLibraryId, reqVO.getLibraryId())
                .eqIfPresent(MaterialLibraryAppBindDO::getAppType, reqVO.getAppType())
                .eqIfPresent(MaterialLibraryAppBindDO::getAppUid, reqVO.getAppUid())
                .eqIfPresent(MaterialLibraryAppBindDO::getUserId, reqVO.getUserId())
                .betweenIfPresent(MaterialLibraryAppBindDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MaterialLibraryAppBindDO::getId));
    }

    default MaterialLibraryAppBindDO selectOneByApp(String appUid) {
        LambdaQueryWrapper<MaterialLibraryAppBindDO> wrapper = Wrappers.lambdaQuery(MaterialLibraryAppBindDO.class)
                .eq(MaterialLibraryAppBindDO::getAppUid, appUid)
                .eq(MaterialLibraryAppBindDO::getStatus, true)
                .orderByDesc(MaterialLibraryAppBindDO::getCreateTime)
                .last(" limit 1");
        return selectOne(wrapper);
    }


    default List<MaterialLibraryAppBindDO> selectListByApp(String appUid) {
        LambdaQueryWrapper<MaterialLibraryAppBindDO> wrapper = Wrappers.lambdaQuery(MaterialLibraryAppBindDO.class)
                .eq(MaterialLibraryAppBindDO::getAppUid, appUid);
        return selectList(wrapper);
    }

    default List<MaterialLibraryAppBindDO> selectListByLibrary(Long libraryId) {
        LambdaQueryWrapper<MaterialLibraryAppBindDO> wrapper = Wrappers.lambdaQuery(MaterialLibraryAppBindDO.class)
                .eq(MaterialLibraryAppBindDO::getLibraryId, libraryId)
                .eq(MaterialLibraryAppBindDO::getStatus, true)
                ;
        return selectList(wrapper);
    }
}