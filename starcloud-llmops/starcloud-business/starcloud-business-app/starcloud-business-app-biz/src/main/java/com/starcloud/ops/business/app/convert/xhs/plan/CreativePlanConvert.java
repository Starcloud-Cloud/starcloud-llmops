package com.starcloud.ops.business.app.convert.xhs.plan;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigDTO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanPO;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativeTypeEnum;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Mapper
public interface CreativePlanConvert {

    /**
     * CreationPlanConvert
     */
    CreativePlanConvert INSTANCE = Mappers.getMapper(CreativePlanConvert.class);

    /**
     * 转换创建请求
     *
     * @param request 请求信息
     * @return 数据对象
     */
    default CreativePlanDO convertCreateRequest(CreativePlanReqVO request) {
        CreativePlanConfigDTO config = request.getConfig();
        AppValidate.notNull(config, CreativeErrorCodeConstants.PLAN_CONFIG_NOT_NULL, request.getName());
        CreativePlanDO creativePlan = new CreativePlanDO();
        creativePlan.setUid(IdUtil.fastSimpleUUID());
        creativePlan.setName(request.getName());
        creativePlan.setType(StringUtils.isBlank(request.getType()) ? CreativeTypeEnum.XHS.name() : request.getType());
        creativePlan.setConfig(JSONUtil.toJsonStr(request.getConfig()));
        creativePlan.setRandomType(request.getRandomType());
        creativePlan.setTotal(request.getTotal());
        creativePlan.setStatus(CreativePlanStatusEnum.PENDING.name());
        creativePlan.setStartTime(null);
        creativePlan.setEndTime(null);
        creativePlan.setElapsed(0L);
        creativePlan.setDescription(StringUtils.isBlank(creativePlan.getDescription()) ? "" : creativePlan.getDescription());
        creativePlan.setDeleted(Boolean.FALSE);
        creativePlan.setCreateTime(LocalDateTime.now());
        creativePlan.setUpdateTime(LocalDateTime.now());
        return creativePlan;
    }

    /***
     * 转换修改请求
     * @param request 请求信息
     * @return CreationPlanDO
     */
    default CreativePlanDO convertModifyRequest(CreativePlanModifyReqVO request) {
        CreativePlanConfigDTO config = request.getConfig();
        AppValidate.notNull(config, CreativeErrorCodeConstants.PLAN_CONFIG_NOT_NULL, request.getName());
        CreativePlanDO creativePlan = new CreativePlanDO();
        creativePlan.setUid(request.getUid());
        creativePlan.setName(request.getName());
        creativePlan.setType(StringUtils.isBlank(request.getType()) ? CreativeTypeEnum.XHS.name() : request.getType());
        creativePlan.setConfig(JSONUtil.toJsonStr(request.getConfig()));
        creativePlan.setRandomType(request.getRandomType());
        creativePlan.setTotal(request.getTotal());
        creativePlan.setDescription(StringUtils.isBlank(creativePlan.getDescription()) ? "" : creativePlan.getDescription());
        creativePlan.setDeleted(Boolean.FALSE);
        creativePlan.setUpdateTime(LocalDateTime.now());
        return creativePlan;
    }

    /**
     * 转换响应
     *
     * @param creativePlan 数据对象
     * @return 响应信息
     */
    default CreativePlanRespVO convertResponse(CreativePlanPO creativePlan) {
        CreativePlanRespVO response = new CreativePlanRespVO();
        response.setUid(creativePlan.getUid());
        response.setName(creativePlan.getName());
        response.setType(creativePlan.getType());
        if (StringUtils.isNotBlank(creativePlan.getConfig())) {
            response.setConfig(JSONUtil.toBean(creativePlan.getConfig(), CreativePlanConfigDTO.class));
        }
        response.setRandomType(creativePlan.getRandomType());
        response.setSuccessCount(creativePlan.getSuccessCount());
        response.setFailureCount(creativePlan.getFailureCount());
        response.setPendingCount(creativePlan.getPendingCount());
        response.setTotal(creativePlan.getTotal());
        response.setStatus(creativePlan.getStatus());
        response.setStartTime(creativePlan.getStartTime());
        response.setEndTime(creativePlan.getEndTime());
        response.setElapsed(creativePlan.getElapsed());
        response.setDescription(creativePlan.getDescription());
        response.setCreator(UserUtils.getUsername(creativePlan.getCreator()));
        response.setUpdater(UserUtils.getUsername(creativePlan.getUpdater()));
        response.setCreateTime(creativePlan.getCreateTime());
        response.setUpdateTime(creativePlan.getUpdateTime());
        return response;
    }

    /**
     * 转换响应
     *
     * @param creativePlan 数据对象
     * @return 响应信息
     */
    default CreativePlanRespVO convertResponse(CreativePlanDO creativePlan) {
        CreativePlanRespVO response = new CreativePlanRespVO();
        response.setUid(creativePlan.getUid());
        response.setName(creativePlan.getName());
        response.setType(creativePlan.getType());
        if (StringUtils.isNotBlank(creativePlan.getConfig())) {
            response.setConfig(JSONUtil.toBean(creativePlan.getConfig(), CreativePlanConfigDTO.class));
        }
        response.setRandomType(creativePlan.getRandomType());
        response.setTotal(creativePlan.getTotal());
        response.setStatus(creativePlan.getStatus());
        response.setStartTime(creativePlan.getStartTime());
        response.setEndTime(creativePlan.getEndTime());
        response.setElapsed(creativePlan.getElapsed());
        response.setDescription(creativePlan.getDescription());
        response.setCreator(UserUtils.getUsername(creativePlan.getCreator()));
        response.setUpdater(UserUtils.getUsername(creativePlan.getUpdater()));
        response.setCreateTime(creativePlan.getCreateTime());
        response.setUpdateTime(creativePlan.getUpdateTime());
        return response;
    }

    /**
     * 分页转换
     *
     * @param page 分页对象
     * @return 分页数据
     */
    default PageResp<CreativePlanRespVO> convertPage(IPage<CreativePlanPO> page) {
        if (page == null) {
            return PageResp.of(Collections.emptyList(), 0L, 1L, 10L);
        }
        List<CreativePlanPO> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return PageResp.of(Collections.emptyList(), page.getTotal(), page.getCurrent(), page.getSize());
        }

        // 用户创建者ID列表。
        List<Long> creatorList = records.stream().map(item -> Long.valueOf(item.getCreator())).distinct().collect(Collectors.toList());
        // 获取用户创建者ID，昵称 Map。
        Map<Long, String> creatorMap = UserUtils.getUserNicknameMapByIds(creatorList);

        // 用户更新者ID列表。
        List<Long> updaterList = records.stream().map(item -> Long.valueOf(item.getUpdater())).distinct().collect(Collectors.toList());
        // 获取用户更新者ID，昵称 Map。
        Map<Long, String> updaterMap = UserUtils.getUserNicknameMapByIds(updaterList);

        List<CreativePlanRespVO> collect = records.stream().map(item -> {
            CreativePlanRespVO response = convertResponse(item);
            response.setCreator(creatorMap.get(Long.valueOf(item.getCreator())));
            response.setUpdater(updaterMap.get(Long.valueOf(item.getUpdater())));
            return response;
        }).collect(Collectors.toList());

        return PageResp.of(collect, page.getTotal(), page.getCurrent(), page.getSize());
    }

}
