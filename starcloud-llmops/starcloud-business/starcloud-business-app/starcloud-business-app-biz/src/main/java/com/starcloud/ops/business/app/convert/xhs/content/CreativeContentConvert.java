package com.starcloud.ops.business.app.convert.xhs.content;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteParam;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteResult;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.recommend.RecommendStepWrapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Mapper
public interface CreativeContentConvert {

    CreativeContentConvert INSTANCE = Mappers.getMapper(CreativeContentConvert.class);

    /**
     * 创作内容实体转为创作内容相应
     *
     * @param creativeContent 创作内容实体
     * @return 创作内容响应
     */
    default CreativeContentRespVO convert(CreativeContentDO creativeContent) {
        CreativeContentRespVO response = new CreativeContentRespVO();
        response.setUid(creativeContent.getUid());
        response.setBatchUid(creativeContent.getBatchUid());
        response.setPlanUid(creativeContent.getPlanUid());
        response.setConversationUid(creativeContent.getConversationUid());
        response.setType(creativeContent.getType());
        response.setSource(creativeContent.getSource());
        // 执行请求
        if (StringUtils.isNoneBlank(creativeContent.getExecuteParam())) {
            CreativeContentExecuteParam executeParam = JsonUtils.parseObject(creativeContent.getExecuteParam(), CreativeContentExecuteParam.class);
            if (null != executeParam) {
                AppMarketRespVO appInformation = executeParam.getAppInformation();
                if (appInformation != null) {
                    appInformation.supplementStepVariable(RecommendStepWrapperFactory.getStepVariable());
                    executeParam.setAppInformation(appInformation);
                }
            }
            response.setExecuteParam(executeParam);
        }
        // 执行结果
        if (StringUtils.isNoneBlank(creativeContent.getExecuteResult())) {
            CreativeContentExecuteResult executeResult = JsonUtils.parseObject(
                    creativeContent.getExecuteResult(), CreativeContentExecuteResult.class);
            response.setExecuteResult(executeResult);
        }
        response.setStartTime(creativeContent.getStartTime());
        response.setEndTime(creativeContent.getEndTime());
        response.setElapsed(creativeContent.getElapsed());
        response.setStatus(creativeContent.getStatus());
        response.setRetryCount(creativeContent.getRetryCount());
        response.setErrorMessage(creativeContent.getErrorMessage());
        response.setLiked(creativeContent.getLiked());
        response.setClaim(creativeContent.getClaim());
        response.setCreator(creativeContent.getCreator());
        response.setCreateTime(creativeContent.getCreateTime());
        response.setTenantId(creativeContent.getTenantId());
        return response;
    }

    /**
     * 创作内容创建请求转为创作内容实体
     *
     * @param request 创作内容创建请求
     * @return 创作内容实体
     */
    default CreativeContentDO convert(CreativeContentCreateReqVO request) {
        CreativeContentDO creativeContent = new CreativeContentDO();
        creativeContent.setUid(IdUtil.fastSimpleUUID());
        creativeContent.setBatchUid(request.getBatchUid());
        creativeContent.setPlanUid(request.getPlanUid());
        creativeContent.setConversationUid(request.getConversationUid());
        creativeContent.setType(request.getType());
        creativeContent.setSource(request.getSource());
        creativeContent.setExecuteParam(JsonUtils.toJsonString(request.getExecuteParam()));
        creativeContent.setExecuteResult(StrUtil.EMPTY_JSON);
        creativeContent.setStatus(CreativeContentStatusEnum.INIT.name());
        creativeContent.setRetryCount(0);
        creativeContent.setErrorMessage(StrUtil.EMPTY);
        creativeContent.setLiked(Boolean.FALSE);
        creativeContent.setClaim(Boolean.FALSE);
        creativeContent.setDeleted(Boolean.FALSE);
        creativeContent.setCreateTime(LocalDateTime.now());
        creativeContent.setUpdateTime(LocalDateTime.now());
        return creativeContent;
    }

    /**
     * 创作内容创建请求转为创作内容实体
     *
     * @param request 修改请求
     * @return 内容实体
     */
    default CreativeContentDO convert(CreativeContentModifyReqVO request) {
        CreativeContentDO creativeContent = new CreativeContentDO();
        creativeContent.setUid(request.getUid());
        creativeContent.setExecuteResult(JsonUtils.toJsonString(request.getExecuteResult()));
        return creativeContent;
    }

    /**
     * 创作内容创建请求列表转为创作内容实体列表
     *
     * @param requestList 请求列表
     * @return 实体列表
     */
    default List<CreativeContentDO> convertList(List<CreativeContentCreateReqVO> requestList) {
        return CollectionUtil.emptyIfNull(requestList)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    /**
     * 创作内容实体列表转为创作内容响应列表
     *
     * @param contentList 创作内容实体列表
     * @return 创作内容响应列表
     */
    default List<CreativeContentRespVO> convertResponseList(List<CreativeContentDO> contentList) {
        return CollectionUtil.emptyIfNull(contentList)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    /**
     * 获取执行请求
     *
     * @param request 请求字符串
     * @return 执行请求
     */
    default CreativeContentExecuteParam toExecuteParam(String request) {
        return JsonUtils.parseObject(request, CreativeContentExecuteParam.class);
    }
}
