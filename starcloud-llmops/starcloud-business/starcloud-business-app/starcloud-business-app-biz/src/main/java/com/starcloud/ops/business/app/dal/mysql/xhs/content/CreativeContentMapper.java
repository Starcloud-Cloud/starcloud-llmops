package com.starcloud.ops.business.app.dal.mysql.xhs.content;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeQueryReqVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentBusinessPO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDTO;
import com.starcloud.ops.business.app.util.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CreativeContentMapper extends BaseMapperX<CreativeContentDO> {


    default PageResult<CreativeContentDO> page(CreativeContentPageReqVO req) {
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class);
        wrapper.eq(StringUtils.isNotBlank(req.getPlanUid()), CreativeContentDO::getPlanUid, req.getPlanUid());
        wrapper.select(CreativeContentDO::getId)
                .select(CreativeContentDO::getUid)
                .select(CreativeContentDO::getPlanUid)
                .select(CreativeContentDO::getTempUid)
                .select(CreativeContentDO::getStatus)
                .select(CreativeContentDO::getStartTime)
                .select(CreativeContentDO::getEndTime)
                .select(CreativeContentDO::getExecuteTime)
                .select(CreativeContentDO::getErrorMsg)
                .select(CreativeContentDO::getRetryCount)
                .select(CreativeContentDO::getClaim);
        return selectPage(req, wrapper);
    }


    default List<CreativeContentDO> selectByPlanUid(String planUid) {
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class)
                .eq(CreativeContentDO::getPlanUid, planUid);
        return selectList(wrapper);
    }

    List<CreativeContentBusinessPO> listGroupByPlanUid(@Param("planUidList") List<String> planUidList);

    default CreativeContentDO selectByType(String businessUid, String type) {
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class)
                .eq(CreativeContentDO::getBusinessUid, businessUid)
                .eq(CreativeContentDO::getType, type)
                .orderByDesc(CreativeContentDO::getCreateTime)
                .last(" limit 1");
        return selectOne(wrapper);
    }

    default List<CreativeContentDO> listByBusinessUid(String businessUid) {
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class)
                .eq(CreativeContentDO::getBusinessUid, businessUid);
        if (UserUtils.isNotAdmin()) {
            wrapper.eq(CreativeContentDO::getCreator, String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        }

        return selectList(wrapper);
    }


    default int delete(String businessUid) {
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class)
                .eq(CreativeContentDO::getBusinessUid, businessUid);
        return delete(wrapper);
    }

    default void deleteByPlanUid(String planUid) {
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class)
                .eq(CreativeContentDO::getPlanUid, planUid);
        delete(wrapper);
    }

    default List<CreativeContentDO> selectByBusinessUids(List<String> businessUids, Boolean claim) {
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class)
                .eq(CreativeContentDO::getClaim, claim)
                .in(CreativeContentDO::getBusinessUid, businessUids);
        return selectList(wrapper);
    }

    default int claim(List<String> businessUids, Boolean claim) {
        LambdaUpdateWrapper<CreativeContentDO> updateWrapper = Wrappers.lambdaUpdate(CreativeContentDO.class)
                .in(CreativeContentDO::getBusinessUid, businessUids)
                .set(CreativeContentDO::getClaim, claim);
        return this.update(null, updateWrapper);
    }


    Long selectCount(@Param("req") CreativeContentPageReqVO req);

    List<CreativeContentDTO> pageSelect(@Param("req") CreativeContentPageReqVO req,
                                        @Param("start") Integer start, @Param("end") Integer end);

    Page<CreativeContentDTO> allTypePage(IPage<CreativeContentDTO> page,  @Param("req") CreativeContentPageReqVO req);


    CreativeContentDTO detail(@Param("businessUid") String businessUid);
    CreativeContentDTO allTypeDetail(@Param("businessUid") String businessUid);

    List<CreativeContentDO> jobQuery(@Param("req") CreativeQueryReqVO queryReq);

    Long countByBusinessUid(@Param("businessUids") List<String> businessUids);

    List<CreativeContentDTO> selectByBusinessUid(@Param("businessUids") List<String> businessUids, @Param("claim") Boolean claim);
}
