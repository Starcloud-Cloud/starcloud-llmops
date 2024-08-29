package com.starcloud.ops.business.app.convert.xhs.plan;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDTO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanMaterialDO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.util.CreativeUtils;
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

import static com.starcloud.ops.business.app.enums.xhs.CreativeConstants.MATERIAL_LIST;

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


    default CreativePlanMaterialDO convertModifyRequest(CreativePlanModifyReqVO request) {
        AppValidate.notNull(request.getConfiguration(), "创作计划配置信息不能为空！");
        AppValidate.notNull(request.getConfiguration().getAppInformation(), "应用配置信息不能为空！");

        AppMarketRespVO appInformation = request.getConfiguration().getAppInformation();

        CreativePlanMaterialDO creativePlanMaterial = new CreativePlanMaterialDO();
        creativePlanMaterial.setUid(IdUtil.fastSimpleUUID());
        creativePlanMaterial.setAppUid(appInformation.getUid());
        creativePlanMaterial.setVersion(appInformation.getVersion());
        creativePlanMaterial.setSource(request.getSource());

        WorkflowStepWrapperRespVO materialStep = CreativeUtils.getMaterialStepWrapper(appInformation);
        if (materialStep != null) {
            materialStep.putVariable(MATERIAL_LIST, StringUtils.EMPTY);
        }
        request.getConfiguration().setMaterialList(Collections.emptyList());
        creativePlanMaterial.setConfiguration(JsonUtils.toJsonString(request.getConfiguration()));

        creativePlanMaterial.setTotalCount(request.getTotalCount());
        creativePlanMaterial.setStatus(CreativePlanStatusEnum.PENDING.name());
        creativePlanMaterial.setDeleted(Boolean.FALSE);
        creativePlanMaterial.setCreateTime(LocalDateTime.now());
        creativePlanMaterial.setUpdateTime(LocalDateTime.now());

        // 修改时，不修改创建时间和状态
        creativePlanMaterial.setCreateTime(null);
        creativePlanMaterial.setStatus(null);
        creativePlanMaterial.setUid(request.getUid());

        return creativePlanMaterial;
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
        response.setSource(creativePlan.getSource());
        // 应用
        if (StringUtils.isNotBlank(creativePlan.getConfiguration())) {
            response.setConfiguration(JsonUtils.parseObject(
                    creativePlan.getConfiguration(), CreativePlanConfigurationDTO.class));
        }

        response.setTotalCount(creativePlan.getTotalCount());
        response.setStatus(creativePlan.getStatus());
        response.setCreateTime(creativePlan.getCreateTime());
        response.setUpdateTime(creativePlan.getUpdateTime());
        return response;
    }

    default CreativePlanRespVO convert(CreativePlanDTO creativePlan) {
        CreativePlanRespVO response = new CreativePlanRespVO();
        response.setUid(creativePlan.getUid());
        response.setAppUid(creativePlan.getAppUid());
        response.setVersion(creativePlan.getVersion());
        response.setSource(creativePlan.getSource());
        // 应用
        CreativePlanConfigurationDTO copy = new CreativePlanConfigurationDTO();
        AppMarketRespVO appMarketRespVO = new AppMarketRespVO();
        appMarketRespVO.setName(creativePlan.getAppName());
        copy.setAppInformation(appMarketRespVO);
        response.setConfiguration(copy);

        response.setTotalCount(creativePlan.getTotalCount());
        response.setStatus(creativePlan.getStatus());
        response.setCreateTime(creativePlan.getCreateTime());
        response.setUpdateTime(creativePlan.getUpdateTime());
        response.setCreatorName(creativePlan.getCreatorName());
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
