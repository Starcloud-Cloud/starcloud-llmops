package com.starcloud.ops.business.limits.convert.userbenefitsusagelog;

import java.time.LocalDateTime;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogRespVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogUpdateReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsusagelog.UserBenefitsUsageLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 用户权益使用日志 Convert
 *
 * @author AlanCusack
 */
@Mapper
public interface UserBenefitsUsageLogConvert {

    UserBenefitsUsageLogConvert INSTANCE = Mappers.getMapper(UserBenefitsUsageLogConvert.class);

    UserBenefitsUsageLogDO convert(UserBenefitsUsageLogCreateReqVO bean);

    default UserBenefitsUsageLogDO InsertConvert(UserBenefitsUsageLogCreateReqVO bean) {

        UserBenefitsUsageLogDO userBenefitsUsageLogDO = new UserBenefitsUsageLogDO();
        userBenefitsUsageLogDO.setUserId(bean.getUserId());
        userBenefitsUsageLogDO.setAction(bean.getAction());
        userBenefitsUsageLogDO.setBenefitsType(bean.getBenefitsType());
        userBenefitsUsageLogDO.setAmount(bean.getAmount());
        userBenefitsUsageLogDO.setOutId(bean.getOutId());
        userBenefitsUsageLogDO.setBenefitsIds(bean.getBenefitsIds());
        userBenefitsUsageLogDO.setUsageTime(bean.getUsageTime());
        userBenefitsUsageLogDO.setCreator(bean.getCreator());
        userBenefitsUsageLogDO.setUpdater(bean.getUpdater());
        userBenefitsUsageLogDO.setTenantId(bean.getTenantId());
        return userBenefitsUsageLogDO;
    }

    UserBenefitsUsageLogDO convert(UserBenefitsUsageLogUpdateReqVO bean);

    UserBenefitsUsageLogRespVO convert(UserBenefitsUsageLogDO bean);

    List<UserBenefitsUsageLogRespVO> convertList(List<UserBenefitsUsageLogDO> list);

    PageResult<UserBenefitsUsageLogRespVO> convertPage(PageResult<UserBenefitsUsageLogDO> page);


}
