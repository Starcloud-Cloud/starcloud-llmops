package com.starcloud.ops.business.app.convert.xhs.batch;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.recommend.RecommendStepWrapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 */
@Mapper
public interface CreativePlanBatchConvert {

    CreativePlanBatchConvert INSTANCE = Mappers.getMapper(CreativePlanBatchConvert.class);

    /**
     * 将创建创作计划响应对象转换为创建创作计划批次请求对象
     *
     * @param response 创作计划
     * @return 创建创作计划批次请求对象
     */
    default CreativePlanBatchReqVO convert(CreativePlanRespVO response) {
        CreativePlanBatchReqVO request = new CreativePlanBatchReqVO();
        request.setPlanUid(response.getUid());
        request.setAppUid(response.getAppUid());
        request.setVersion(response.getVersion());
        request.setSource(response.getSource());
        request.setConfiguration(response.getConfiguration());
        request.setTotalCount(response.getTotalCount());
        return request;
    }

    /**
     * 将创建创作计划批次请求对象转换为实体对象
     *
     * @param request 请求
     * @return 实体对象
     */
    default CreativePlanBatchDO convert(CreativePlanBatchReqVO request) {
        CreativePlanBatchDO bath = new CreativePlanBatchDO();
        bath.setPlanUid(request.getPlanUid());
        bath.setAppUid(request.getAppUid());
        bath.setVersion(request.getVersion());
        bath.setSource(request.getSource());
        bath.setConfiguration(JsonUtils.toJsonString(request.getConfiguration()));
        bath.setTotalCount(request.getTotalCount());
        bath.setFailureCount(0);
        bath.setSuccessCount(0);
        bath.setStartTime(LocalDateTime.now());
        bath.setElapsed(0L);
        bath.setStatus(CreativePlanStatusEnum.PENDING.name());
        bath.setDeleted(Boolean.FALSE);
        bath.setCreateTime(LocalDateTime.now());
        bath.setUpdateTime(LocalDateTime.now());
        return bath;
    }

    /**
     * 将实体转换为响应对象
     *
     * @param bath 实体
     * @return 响应结果
     */
    default CreativePlanBatchRespVO convert(CreativePlanBatchDO bath) {
        CreativePlanBatchRespVO response = new CreativePlanBatchRespVO();
        response.setId(bath.getId());
        response.setUid(bath.getUid());
        response.setPlanUid(bath.getPlanUid());
        response.setAppUid(bath.getAppUid());
        response.setVersion(bath.getVersion());
        response.setSource(bath.getSource());
        if (StringUtils.isNotBlank(bath.getConfiguration())) {
            CreativePlanConfigurationDTO configuration = JsonUtils.parseObject(bath.getConfiguration(), CreativePlanConfigurationDTO.class);
            if (null != configuration) {
                AppMarketRespVO appInformation = configuration.getAppInformation();
                if (null != appInformation) {
                    appInformation.supplementStepVariable(RecommendStepWrapperFactory.getStepVariable());
                    configuration.setAppInformation(appInformation);
                }
            }
            response.setConfiguration(configuration);
        }
        response.setTotalCount(bath.getTotalCount());
        response.setFailureCount(bath.getFailureCount());
        response.setSuccessCount(bath.getSuccessCount());
        response.setStartTime(bath.getStartTime());
        response.setEndTime(bath.getEndTime());
        response.setElapsed(bath.getElapsed());
        response.setStatus(bath.getStatus());
        response.setCreator(bath.getCreator());
        response.setCreateTime(bath.getCreateTime());
        return response;
    }

    /**
     * 将实体列表转换为响应对象
     *
     * @param planBatchList 实体列表
     * @return 响应列表
     */
    default List<CreativePlanBatchRespVO> convert(List<CreativePlanBatchDO> planBatchList) {
        return CollectionUtil.emptyIfNull(planBatchList)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

}
