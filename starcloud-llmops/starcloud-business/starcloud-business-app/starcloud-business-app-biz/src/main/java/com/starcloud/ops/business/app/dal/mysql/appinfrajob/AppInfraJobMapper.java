package com.starcloud.ops.business.app.dal.mysql.appinfrajob;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.app.controller.admin.appinfrajob.vo.AppInfraJobPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.appinfrajob.AppInfraJobDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用定时执行任务 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface AppInfraJobMapper extends BaseMapperX<AppInfraJobDO> {

    default PageResult<AppInfraJobDO> selectPage(AppInfraJobPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AppInfraJobDO>()
                .likeIfPresent(AppInfraJobDO::getName, reqVO.getName())
                .eqIfPresent(AppInfraJobDO::getAppFrom, reqVO.getAppFrom())
                .eqIfPresent(AppInfraJobDO::getAppUid, reqVO.getAppUid())
                .eqIfPresent(AppInfraJobDO::getCronExpression, reqVO.getCronExpression())
                .betweenIfPresent(AppInfraJobDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(AppInfraJobDO::getStatus, reqVO.getStatus())
                .orderByDesc(AppInfraJobDO::getId));
    }

}