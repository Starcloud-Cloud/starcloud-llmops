package com.starcloud.ops.business.app.dal.mysql.xhs;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentPageReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeQueryReq;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XhsCreativeContentMapper extends BaseMapperX<XhsCreativeContentDO> {


    default PageResult<XhsCreativeContentDO> page(XhsCreativeContentPageReq req) {
        LambdaQueryWrapper<XhsCreativeContentDO> wrapper = Wrappers.lambdaQuery(XhsCreativeContentDO.class);
        wrapper.eq(StringUtils.isNotBlank(req.getPlanUid()), XhsCreativeContentDO::getPlanUid, req.getPlanUid());
        wrapper.select(XhsCreativeContentDO::getId)
                .select(XhsCreativeContentDO::getUid)
                .select(XhsCreativeContentDO::getPlanUid)
                .select(XhsCreativeContentDO::getTempUid)
                .select(XhsCreativeContentDO::getStatus)
                .select(XhsCreativeContentDO::getStartTime)
                .select(XhsCreativeContentDO::getEndTime)
                .select(XhsCreativeContentDO::getExecuteTime)
                .select(XhsCreativeContentDO::getErrorMsg)
                .select(XhsCreativeContentDO::getRetryCount)
                .select(XhsCreativeContentDO::getClaim);
        return selectPage(req, wrapper);
    }


    default List<XhsCreativeContentDO> selectByPlanUid(String planUid) {
        LambdaQueryWrapper<XhsCreativeContentDO> wrapper = Wrappers.lambdaQuery(XhsCreativeContentDO.class)
                .eq(XhsCreativeContentDO::getPlanUid, planUid);
        return selectList(wrapper);
    }

    default XhsCreativeContentDO selectByType(String businessUid, String type) {
        LambdaQueryWrapper<XhsCreativeContentDO> wrapper = Wrappers.lambdaQuery(XhsCreativeContentDO.class)
                .eq(XhsCreativeContentDO::getBusinessUid, businessUid)
                .eq(XhsCreativeContentDO::getType, type)
                .orderByDesc(XhsCreativeContentDO::getCreateTime)
                .last(" limit 1");
        return selectOne(wrapper);
    }


    default int delete(String businessUid) {
        LambdaQueryWrapper<XhsCreativeContentDO> wrapper = Wrappers.lambdaQuery(XhsCreativeContentDO.class)
                .eq(XhsCreativeContentDO::getBusinessUid, businessUid);
        return delete(wrapper);
    }

    Long selectCount(@Param("req") XhsCreativeContentPageReq req);

    List<XhsCreativeContentDTO> pageSelect(@Param("req") XhsCreativeContentPageReq req,
                                           @Param("start") Integer start, @Param("end") Integer end);


    XhsCreativeContentDTO detail(@Param("businessUid") String businessUid);

    List<XhsCreativeContentDO> jobQuery(@Param("req") XhsCreativeQueryReq queryReq);
}