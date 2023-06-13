package com.starcloud.ops.business.limits.convert.userbenefitsstrategy;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyRespVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyUpdateReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户权益策略表
 Convert
 *
 * @author AlanCusack
 */
@UtilityClass
public class UserBenefitsStrategyConvert {

    public UserBenefitsStrategyDO convert(UserBenefitsStrategyCreateReqVO bean) {
        if ( bean == null ) {
            return null;
        }

        UserBenefitsStrategyDO.UserBenefitsStrategyDOBuilder userBenefitsStrategyDO = UserBenefitsStrategyDO.builder();

        userBenefitsStrategyDO.code( bean.getCode() );
        userBenefitsStrategyDO.strategyName( bean.getStrategyName() );
        userBenefitsStrategyDO.strategyDesc( bean.getStrategyDesc() );
        userBenefitsStrategyDO.strategyType( bean.getStrategyType() );
        userBenefitsStrategyDO.appCount( bean.getAppCount() );
        userBenefitsStrategyDO.datasetCount( bean.getDatasetCount() );
        userBenefitsStrategyDO.imageCount( bean.getImageCount() );
        userBenefitsStrategyDO.tokenCount( bean.getTokenCount() );
        userBenefitsStrategyDO.scope( bean.getScope() );
        userBenefitsStrategyDO.scopeNum( bean.getScopeNum() );
        userBenefitsStrategyDO.limitUnit( bean.getLimitUnit() );
        userBenefitsStrategyDO.enabled( bean.getEnabled() );

        return userBenefitsStrategyDO.build();
    }

    public UserBenefitsStrategyDO convert(UserBenefitsStrategyUpdateReqVO bean) {
        if ( bean == null ) {
            return null;
        }

        UserBenefitsStrategyDO.UserBenefitsStrategyDOBuilder userBenefitsStrategyDO = UserBenefitsStrategyDO.builder();

        userBenefitsStrategyDO.id( bean.getId() );
        userBenefitsStrategyDO.code( bean.getCode() );
        userBenefitsStrategyDO.strategyName( bean.getStrategyName() );
        userBenefitsStrategyDO.strategyDesc( bean.getStrategyDesc() );
        userBenefitsStrategyDO.appCount( bean.getAppCount() );
        userBenefitsStrategyDO.datasetCount( bean.getDatasetCount() );
        userBenefitsStrategyDO.imageCount( bean.getImageCount() );
        userBenefitsStrategyDO.tokenCount( bean.getTokenCount() );
        userBenefitsStrategyDO.limitUnit( bean.getLimitUnit() );

        return userBenefitsStrategyDO.build();
    }

    public UserBenefitsStrategyRespVO convert(UserBenefitsStrategyDO bean) {
        if ( bean == null ) {
            return null;
        }

        UserBenefitsStrategyRespVO userBenefitsStrategyRespVO = new UserBenefitsStrategyRespVO();

        userBenefitsStrategyRespVO.setCode( bean.getCode() );
        userBenefitsStrategyRespVO.setStrategyName( bean.getStrategyName() );
        userBenefitsStrategyRespVO.setStrategyDesc( bean.getStrategyDesc() );
        userBenefitsStrategyRespVO.setStrategyType( bean.getStrategyType() );
        userBenefitsStrategyRespVO.setAppCount( bean.getAppCount() );
        userBenefitsStrategyRespVO.setDatasetCount( bean.getDatasetCount() );
        userBenefitsStrategyRespVO.setImageCount( bean.getImageCount() );
        userBenefitsStrategyRespVO.setTokenCount( bean.getTokenCount() );
        userBenefitsStrategyRespVO.setScope( bean.getScope() );
        userBenefitsStrategyRespVO.setScopeNum( bean.getScopeNum() );
        userBenefitsStrategyRespVO.setLimitUnit( bean.getLimitUnit() );
        userBenefitsStrategyRespVO.setEnabled( bean.getEnabled() );
        userBenefitsStrategyRespVO.setArchived( bean.getArchived() );
        userBenefitsStrategyRespVO.setArchivedBy( bean.getArchivedBy() );
        userBenefitsStrategyRespVO.setArchivedTime( bean.getArchivedTime() );
        userBenefitsStrategyRespVO.setId( bean.getId() );
        userBenefitsStrategyRespVO.setCreateTime( bean.getCreateTime() );

        return userBenefitsStrategyRespVO;
    }

    public List<UserBenefitsStrategyRespVO> convertList(List<UserBenefitsStrategyDO> list) {
        if ( list == null ) {
            return null;
        }

        List<UserBenefitsStrategyRespVO> list1 = new ArrayList<UserBenefitsStrategyRespVO>( list.size() );
        for ( UserBenefitsStrategyDO userBenefitsStrategyDO : list ) {
            list1.add( convert( userBenefitsStrategyDO ) );
        }

        return list1;
    }

    public PageResult<UserBenefitsStrategyRespVO> convertPage(PageResult<UserBenefitsStrategyDO> page) {
        if ( page == null ) {
            return null;
        }

        PageResult<UserBenefitsStrategyRespVO> pageResult = new PageResult<UserBenefitsStrategyRespVO>();

        pageResult.setList( convertList( page.getList() ) );
        pageResult.setTotal( page.getTotal() );

        return pageResult;
    }

}