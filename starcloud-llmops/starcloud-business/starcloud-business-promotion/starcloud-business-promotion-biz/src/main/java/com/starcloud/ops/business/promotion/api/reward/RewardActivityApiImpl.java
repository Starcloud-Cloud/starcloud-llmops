package com.starcloud.ops.business.promotion.api.reward;

import com.starcloud.ops.business.promotion.api.reward.dto.RewardActivityMatchRespDTO;
import com.starcloud.ops.business.promotion.service.reward.RewardActivityService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 满减送活动 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class RewardActivityApiImpl implements RewardActivityApi {

    @Resource
    private RewardActivityService rewardActivityService;

    @Override
    public List<RewardActivityMatchRespDTO> getMatchRewardActivityList(Collection<Long> spuIds) {
        return rewardActivityService.getMatchRewardActivityList(spuIds);
    }

}
