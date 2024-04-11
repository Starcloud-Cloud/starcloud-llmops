package com.starcloud.ops.business.app.convert.xhs.plan;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.util.UserUtils;
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
        AppValidate.notNull(request.getConfiguration(), "创作计划配置信息不能为空！");
        AppValidate.notNull(request.getConfiguration().getAppInformation(), "应用配置信息不能为空！");
        AppMarketRespVO appInformation = request.getConfiguration().getAppInformation();

        CreativePlanDO creativePlan = new CreativePlanDO();
        creativePlan.setUid(IdUtil.fastSimpleUUID());
        creativePlan.setAppUid(appInformation.getUid());
        creativePlan.setVersion(appInformation.getVersion());
        creativePlan.setConfiguration(JsonUtils.toJsonString(request.getConfiguration()));
        creativePlan.setTotalCount(request.getTotalCount());
        creativePlan.setTags(JsonUtils.toJsonString(request.getTags()));
        creativePlan.setStatus(CreativePlanStatusEnum.PENDING.name());
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
        CreativePlanDO creativePlan = convertCreateRequest(request);
        creativePlan.setCreateTime(null);
        creativePlan.setUid(request.getUid());
        return creativePlan;
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
        response.setAppUid(creativePlan.getAppUid());
        response.setVersion(creativePlan.getVersion());

        // 应用
        if (StringUtils.isNotBlank(creativePlan.getConfiguration())) {
            response.setConfiguration(JsonUtils.parseObject(
                    creativePlan.getConfiguration(), CreativePlanConfigurationDTO.class));
        }

        // 标签
        if (StringUtils.isNoneBlank(creativePlan.getTags())) {
            response.setTags(JsonUtils.parseArray(creativePlan.getTags(), String.class));
        }

        response.setTotalCount(creativePlan.getTotalCount());
        response.setStatus(creativePlan.getStatus());
        response.setCreateTime(creativePlan.getCreateTime());
        response.setUpdateTime(creativePlan.getUpdateTime());
        return response;
    }

    /**
     * 集合转换
     *
     * @param list 列表
     * @return 列表
     */
    default List<CreativePlanRespVO> convertList(List<CreativePlanDO> list) {
        return list.stream().map(this::convertResponse).collect(Collectors.toList());
    }

    /**
     * 分页转换
     *
     * @param page 分页对象
     * @return 分页数据
     */
    default PageResp<CreativePlanRespVO> convertPage(IPage<CreativePlanDO> page) {
        if (page == null) {
            return PageResp.of(Collections.emptyList(), 0L, 1L, 10L);
        }
        List<CreativePlanDO> records = page.getRecords();
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
