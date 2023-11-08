package com.starcloud.ops.business.app.service.plan.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanConfigDTO;
import com.starcloud.ops.business.app.api.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.api.plan.vo.request.CreativePlanReqVO;
import com.starcloud.ops.business.app.api.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.api.xhs.XhsAppResponse;
import com.starcloud.ops.business.app.convert.plan.CreativePlanConvert;
import com.starcloud.ops.business.app.dal.databoject.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.plan.CreativePlanPO;
import com.starcloud.ops.business.app.dal.mysql.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.enums.plan.CreativeRandomTypeEnum;
import com.starcloud.ops.business.app.enums.plan.CreativeTypeEnum;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.plan.CreativePlanService;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Slf4j
@Service
public class CreativePlanServiceImpl implements CreativePlanService {

    @Resource
    private CreativePlanMapper creativePlanMapper;

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private AppDictionaryService appDictionaryService;

    /**
     * 文案模板列表
     *
     * @param type 类型
     * @return 文案模板列表
     */
    @Override
    public List<XhsAppResponse> copyWritingTemplates(String type) {
        AppValidate.notBlank(type, ErrorCodeConstants.CREATIVE_PLAN_TYPE_REQUIRED);
        CreativeTypeEnum typeEnum = CreativeTypeEnum.of(type);
        if (typeEnum == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_TYPE_NOT_SUPPORTED, type);
        }
        AppMarketListQuery query = new AppMarketListQuery();
        query.setIsSimple(Boolean.FALSE);
        query.setTags(typeEnum.getTagType().getTags());
        List<AppMarketRespVO> list = appMarketService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(item -> {
            List<VariableItemRespVO> variableList = Optional.ofNullable(item).map(AppMarketRespVO::getWorkflowConfig)
                    .map(WorkflowConfigRespVO::getSteps)
                    .map(steps -> steps.get(0)).map(WorkflowStepWrapperRespVO::getVariable)
                    .map(VariableRespVO::getVariables)
                    .orElseThrow(() -> ServiceExceptionUtil.exception(new ErrorCode(310900100, "系统步骤不能为空")));
            XhsAppResponse response = new XhsAppResponse();
            response.setUid(item.getUid());
            response.setName(item.getName());
            response.setCategory(item.getCategory());
            response.setIcon(item.getIcon());
            response.setDescription(item.getDescription());
            response.setVariables(variableList);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 获取创作计划详情
     *
     * @param uid 创作计划UID
     * @return 创作计划详情
     */
    @Override
    public CreativePlanRespVO get(String uid) {
        AppValidate.notBlank(uid, ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        CreativePlanDO creativePlan = creativePlanMapper.get(uid);
        AppValidate.notNull(creativePlan, ErrorCodeConstants.CREATIVE_PLAN_NOT_EXIST, uid);
        return CreativePlanConvert.INSTANCE.convertResponse(creativePlan);
    }

    /**
     * 获取模板列表
     *
     * @return 模板列表
     */
    @Override
    public List<CreativePlanRespVO> listTemplates() {
        CreativePlanConfigDTO config = new CreativePlanConfigDTO();
        List<XhsAppResponse> xhsAppResponses = copyWritingTemplates(CreativeTypeEnum.XHS.name());
        if (CollectionUtil.isEmpty(xhsAppResponses)) {
            config.setCopyWritingList(Collections.emptyList());
            config.setVariableList(Collections.emptyList());
        } else {
            XhsAppResponse app = xhsAppResponses.get(0);
            config.setCopyWritingList(Collections.singletonList(app.getUid()));
            config.setVariableList(variableList(app.getVariables()));
        }
        config.setImageStyleList(appDictionaryService.xhsImageStyles());
        config.setRandomType(CreativeRandomTypeEnum.RANDOM.name());
        config.setTotal(50);

        CreativePlanRespVO redBookResponse = new CreativePlanRespVO();
        redBookResponse.setUid("red-book");
        redBookResponse.setName("小红书模板");
        redBookResponse.setType(CreativeTypeEnum.XHS.name());
        redBookResponse.setConfig(config);
        redBookResponse.setRandomType(config.getRandomType());
        redBookResponse.setTotal(config.getTotal());
        redBookResponse.setDescription("小红书模板");
        return Collections.singletonList(redBookResponse);
    }

    /**
     * 获取创作计划分页列表
     *
     * @param query 请求参数
     * @return 创作计划分页列表
     */
    @Override
    public PageResp<CreativePlanRespVO> page(CreativePlanPageQuery query) {
        IPage<CreativePlanPO> page = creativePlanMapper.pageCreativePlan(PageUtil.page(query), query);
        return CreativePlanConvert.INSTANCE.convertPage(page);
    }

    /**
     * 创建创作计划
     *
     * @param request 创作计划请求
     */
    @Override
    public void create(CreativePlanReqVO request) {
        handlerAndValidate(request);
        CreativePlanDO plan = CreativePlanConvert.INSTANCE.convertCreateRequest(request);
        creativePlanMapper.insert(plan);
    }

    /**
     * 复制创作计划
     *
     * @param request 创作计划请求
     */
    @Override
    public void copy(UidRequest request) {
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        CreativePlanDO plan = creativePlanMapper.get(request.getUid());
        AppValidate.notNull(plan, ErrorCodeConstants.CREATIVE_PLAN_NOT_EXIST);

        CreativePlanDO copyPlan = new CreativePlanDO();
        copyPlan.setUid(IdUtil.fastSimpleUUID());
        copyPlan.setName(plan.getName() + "-Copy");
        copyPlan.setType(plan.getType());
        copyPlan.setConfig(plan.getConfig());
        copyPlan.setTotal(plan.getTotal());
        copyPlan.setStatus(CreativePlanStatusEnum.PENDING.name());
        copyPlan.setStartTime(null);
        copyPlan.setEndTime(null);
        copyPlan.setElapsed(0L);
        copyPlan.setDescription(plan.getDescription());
        copyPlan.setDeleted(Boolean.FALSE);
        copyPlan.setCreateTime(LocalDateTime.now());
        copyPlan.setEndTime(LocalDateTime.now());

        creativePlanMapper.insert(copyPlan);
    }

    /**
     * 修改创作计划
     *
     * @param request 创作计划请求
     */
    @Override
    public void modify(CreativePlanModifyReqVO request) {
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        handlerAndValidate(request);
        CreativePlanDO plan = creativePlanMapper.get(request.getUid());
        AppValidate.notNull(plan, ErrorCodeConstants.CREATIVE_PLAN_NOT_EXIST);
        if (!CreativePlanStatusEnum.PENDING.name().equals(plan.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_STATUS_NOT_SUPPORT_MODIFY);
        }
        CreativePlanDO modifyPlan = CreativePlanConvert.INSTANCE.convertModifyRequest(request);
        modifyPlan.setId(plan.getId());
        creativePlanMapper.updateById(modifyPlan);
    }

    /**
     * 删除创作计划
     *
     * @param uid 创作计划UID
     */
    @Override
    public void delete(String uid) {
        AppValidate.notBlank(uid, ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        CreativePlanDO plan = creativePlanMapper.get(uid);
        AppValidate.notNull(plan, ErrorCodeConstants.CREATIVE_PLAN_NOT_EXIST);
        creativePlanMapper.deleteById(plan.getId());
    }

    /**
     * 处理并且校验
     *
     * @param request 创作计划请求
     */
    private void handlerAndValidate(CreativePlanReqVO request) {
        if (!CreativeTypeEnum.contains(request.getType())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_TYPE_NOT_SUPPORTED, request.getType());
        }
        CreativePlanConfigDTO config = request.getConfig();
        AppValidate.notNull(config, ErrorCodeConstants.CREATIVE_PLAN_CONFIG_NOT_NULL, request.getName());
        config.validate();
    }

    /**
     * 变量列表转换
     *
     * @param variableRespList 变量列表
     * @return 转换结果
     */
    private List<VariableItemDTO> variableList(List<VariableItemRespVO> variableRespList) {
        return CollectionUtil.emptyIfNull(variableRespList).stream().map(variable -> {
            VariableItemDTO variableItemDTO = new VariableItemDTO();
            variableItemDTO.setField(variable.getField());
            variableItemDTO.setLabel(variable.getLabel());
            variableItemDTO.setType(variable.getType());
            variableItemDTO.setStyle(variable.getStyle());
            variableItemDTO.setGroup(variable.getGroup());
            variableItemDTO.setOrder(variable.getOrder());
            variableItemDTO.setValue(variable.getValue());
            variableItemDTO.setDefaultValue(variable.getDefaultValue());
            variableItemDTO.setIsShow(variable.getIsShow());
            variableItemDTO.setIsPoint(variable.getIsPoint());
            variableItemDTO.setDescription(variable.getDescription());
            variableItemDTO.setOptions(variable.getOptions());
            return variableItemDTO;
        }).collect(Collectors.toList());
    }
}
