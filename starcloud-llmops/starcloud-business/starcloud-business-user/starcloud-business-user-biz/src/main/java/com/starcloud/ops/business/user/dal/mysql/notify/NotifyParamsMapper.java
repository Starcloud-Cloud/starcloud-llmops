package com.starcloud.ops.business.user.dal.mysql.notify;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starcloud.ops.business.user.dal.dataobject.notify.PurchaseExperienceParamsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotifyParamsMapper extends BaseMapper<Object> {

    Long countPurchaseExperienceParams();

    List<PurchaseExperienceParamsDTO> pagePurchaseExperienceParams(@Param("start") Integer start, @Param("size") Integer size);

    List<PurchaseExperienceParamsDTO> purchaseExperienceParams();
}
