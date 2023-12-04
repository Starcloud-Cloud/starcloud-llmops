package com.starcloud.ops.business.app.dal.mysql.xhs.content;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeQueryReqVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.XhsCreativeContentBusinessPO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.XhsCreativeContentDTO;
import com.starcloud.ops.business.app.util.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CreativeContentMapper extends BaseMapperX<XhsCreativeContentDO> {


    default PageResult<XhsCreativeContentDO> page(CreativeContentPageReqVO req) {
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

    List<XhsCreativeContentBusinessPO> listGroupByBusinessUid(@Param("planUidList") List<String> planUidList);

    default XhsCreativeContentDO selectByType(String businessUid, String type) {
        LambdaQueryWrapper<XhsCreativeContentDO> wrapper = Wrappers.lambdaQuery(XhsCreativeContentDO.class)
                .eq(XhsCreativeContentDO::getBusinessUid, businessUid)
                .eq(XhsCreativeContentDO::getType, type)
                .orderByDesc(XhsCreativeContentDO::getCreateTime)
                .last(" limit 1");
        return selectOne(wrapper);
    }

    default List<XhsCreativeContentDO> listByBusinessUid(String businessUid) {
        LambdaQueryWrapper<XhsCreativeContentDO> wrapper = Wrappers.lambdaQuery(XhsCreativeContentDO.class)
                .eq(XhsCreativeContentDO::getBusinessUid, businessUid);
        if (UserUtils.isNotAdmin()) {
            wrapper.eq(XhsCreativeContentDO::getCreator, String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        }

        return selectList(wrapper);
    }


    default int delete(String businessUid) {
        LambdaQueryWrapper<XhsCreativeContentDO> wrapper = Wrappers.lambdaQuery(XhsCreativeContentDO.class)
                .eq(XhsCreativeContentDO::getBusinessUid, businessUid);
        return delete(wrapper);
    }

    default void deleteByPlanUid(String planUid) {
        LambdaQueryWrapper<XhsCreativeContentDO> wrapper = Wrappers.lambdaQuery(XhsCreativeContentDO.class)
                .eq(XhsCreativeContentDO::getPlanUid, planUid);
        delete(wrapper);
    }

    default List<XhsCreativeContentDO> selectByBusinessUids(List<String> businessUids, Boolean claim) {
        LambdaQueryWrapper<XhsCreativeContentDO> wrapper = Wrappers.lambdaQuery(XhsCreativeContentDO.class)
                .eq(XhsCreativeContentDO::getClaim, claim)
                .in(XhsCreativeContentDO::getBusinessUid, businessUids);
        return selectList(wrapper);
    }

    default int claim(List<String> businessUids) {
        LambdaUpdateWrapper<XhsCreativeContentDO> updateWrapper = Wrappers.lambdaUpdate(XhsCreativeContentDO.class)
                .in(XhsCreativeContentDO::getBusinessUid, businessUids)
                .set(XhsCreativeContentDO::getClaim, true);
        return this.update(null, updateWrapper);
    }


    Long selectCount(@Param("req") CreativeContentPageReqVO req);

    List<XhsCreativeContentDTO> pageSelect(@Param("req") CreativeContentPageReqVO req,
                                           @Param("start") Integer start, @Param("end") Integer end);


    XhsCreativeContentDTO detail(@Param("businessUid") String businessUid);

    List<XhsCreativeContentDO> jobQuery(@Param("req") CreativeQueryReqVO queryReq);

    Long countByBusinessUid(@Param("businessUids") List<String> businessUids);

    List<XhsCreativeContentDTO> selectByBusinessUid(@Param("businessUids") List<String> businessUids);
}
