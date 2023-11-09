package com.starcloud.ops.business.app.service.plan.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import com.starcloud.ops.business.app.api.xhs.XhsImageStyleDTO;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsBathImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.dto.XhsCreativeContentExecuteParamsDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentCreateReq;
import com.starcloud.ops.business.app.convert.plan.CreativePlanConvert;
import com.starcloud.ops.business.app.dal.databoject.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.plan.CreativePlanPO;
import com.starcloud.ops.business.app.dal.mysql.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.enums.plan.CreativeRandomTypeEnum;
import com.starcloud.ops.business.app.enums.plan.CreativeTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentTypeEnums;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.plan.CreativePlanService;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeContentService;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    /**
     * 临时图片值
     */
    private static final String TEMP_IMAGE_VALUE = "TEMP_STARCLOUD_CREATIVE_PLAN_IMAGE_TEMPLATE_PARAM_a589310bb1764f5a8e0d139378f7fe51";

    @Resource
    private CreativePlanMapper creativePlanMapper;

    @Resource
    private XhsCreativeContentService xhsCreativeContentService;

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
        if (creativePlanMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_NAME_EXIST, request.getName());
        }
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

        copyPlan.setName(getCopyName(plan.getName()));
        copyPlan.setType(plan.getType());
        copyPlan.setConfig(plan.getConfig());
        copyPlan.setRandomType(plan.getRandomType());
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
        // 名称做了修改，且修改之后的名称已经存在
        if (!plan.getName().equals(request.getName()) && creativePlanMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_NAME_EXIST, request.getName());
        }
        CreativePlanDO modifyPlan = CreativePlanConvert.INSTANCE.convertModifyRequest(request);
        modifyPlan.setId(plan.getId());
        creativePlanMapper.updateById(modifyPlan);
    }

    /**
     * 修改创作计划状态
     *
     * @param uid    创作计划UID
     * @param status 修改状态
     */
    @Override
    public void updateStatus(String uid, String status) {
        AppValidate.notBlank(uid, ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        AppValidate.notBlank(status, ErrorCodeConstants.CREATIVE_PLAN_STATUS_REQUIRED);
        if (!CreativePlanStatusEnum.contains(status) || CreativePlanStatusEnum.PENDING.name().equals(status)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_STATUS_NOT_SUPPORT_MODIFY);
        }

        CreativePlanDO plan = creativePlanMapper.get(uid);
        AppValidate.notNull(plan, ErrorCodeConstants.CREATIVE_PLAN_NOT_EXIST);

        // 更新
        LambdaUpdateWrapper<CreativePlanDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(CreativePlanDO::getStatus, status);
        updateWrapper.set(CreativePlanDO::getStartTime, LocalDateTime.now());
        updateWrapper.eq(CreativePlanDO::getUid, uid);
        creativePlanMapper.update(null, updateWrapper);
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
     * 执行创作计划
     *
     * @param uid 创作计划UID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(String uid) {
        AppValidate.notBlank(uid, ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        CreativePlanRespVO plan = this.get(uid);
        CreativePlanConfigDTO config = plan.getConfig();
        AppValidate.notNull(config, ErrorCodeConstants.CREATIVE_PLAN_CONFIG_NOT_NULL);

        // 图片素材列表
        List<String> imageUrlList = config.getImageUrlList();
        if (CollectionUtil.isEmpty(imageUrlList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_UPLOAD_IMAGE_EMPTY);
        }

        String randomType = config.getRandomType();
        if (CreativeRandomTypeEnum.RANDOM.name().equals(randomType)) {
            this.createRandomTasks(plan.getUid(), config);
            // 更新状态
            LambdaUpdateWrapper<CreativePlanDO> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.set(CreativePlanDO::getStatus, CreativePlanStatusEnum.RUNNING.name());
            updateWrapper.set(CreativePlanDO::getStartTime, LocalDateTime.now());
            updateWrapper.eq(CreativePlanDO::getUid, uid);
            creativePlanMapper.update(null, updateWrapper);
            return;
        }
        throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_RANDOM_TYPE_NOT_SUPPORTED, randomType);
    }

    /**
     * 创建随机任务
     *
     * @param planUid 创作计划UID
     * @param config  配置
     */
    private void createRandomTasks(String planUid, CreativePlanConfigDTO config) {
        // 获取生成任务数量量
        Integer total = config.getTotal();
        // 图片素材列表
        List<String> imageUrlList = config.getImageUrlList();
        // 处理并且获取文案参数
        List<XhsAppExecuteRequest> xhsAppExecuteRequests = this.handlerAppExecuteRequestList(config);
        if (CollectionUtil.isEmpty(imageUrlList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_COPY_WRITING_EMPTY);
        }
        // 处理并且获取图片参数
        List<XhsBathImageExecuteRequest> bathImageExecuteRequestList = handlerCreativePlanImageRequestList(config);
        if (CollectionUtil.isEmpty(imageUrlList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_IMAGE_STYLE_EMPTY);
        }

        // 插入任务
        List<XhsCreativeContentCreateReq> xhsCreativeContentCreateReqList = new ArrayList<>(total * 2);
        for (int i = 0; i < total; i++) {
            String businessUid = IdUtil.fastSimpleUUID();
            // 应用执行任务
            XhsCreativeContentCreateReq appCreateRequest = new XhsCreativeContentCreateReq();
            int appRandomInt = RandomUtil.randomInt(xhsAppExecuteRequests.size());
            // 克隆图片执行参数, 防止引用问题
            XhsAppExecuteRequest appExecuteRequest = SerializationUtils.clone(xhsAppExecuteRequests.get(appRandomInt));
            appCreateRequest.setPlanUid(planUid);
            appCreateRequest.setBusinessUid(businessUid);
            appCreateRequest.setType(XhsCreativeContentTypeEnums.COPY_WRITING.name());
            appCreateRequest.setTempUid(appExecuteRequest.getUid());
            appCreateRequest.setExecuteParams(XhsCreativeContentExecuteParamsDTO.ofApp(appExecuteRequest));
            xhsCreativeContentCreateReqList.add(appCreateRequest);

            // 图片执行任务
            XhsCreativeContentCreateReq imageCreateRequest = new XhsCreativeContentCreateReq();
            int imageRandomInt = RandomUtil.randomInt(bathImageExecuteRequestList.size());
            // 克隆图片执行参数, 防止引用问题
            XhsBathImageExecuteRequest bathImageExecuteRequest = SerializationUtils.clone(bathImageExecuteRequestList.get(imageRandomInt));

            // 随机从素材库中放入图片参数
            List<String> templateIdList = new ArrayList<>();
            // 使用的图片集合
            List<String> useImageList = new ArrayList<>();
            // 处理图片执行参数
            List<XhsImageExecuteRequest> imageExecuteRequests = new ArrayList<>();
            for (XhsImageExecuteRequest imageExecuteRequest : CollectionUtil.emptyIfNull(bathImageExecuteRequest.getImageRequests())) {
                templateIdList.add(imageExecuteRequest.getImageTemplate());
                Map<String, Object> params = imageExecuteRequest.getParams();
                if (CollectionUtil.isEmpty(params)) {
                    imageExecuteRequests.add(imageExecuteRequest);
                    continue;
                }
                Map<String, Object> handlerParams = new HashMap<>();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    // 图片参数替换为素材库中的随机图片
                    if (Objects.nonNull(entry.getValue()) && TEMP_IMAGE_VALUE.equals(entry.getValue().toString())) {
                        int randomInt = RandomUtil.randomInt(imageUrlList.size());
                        String image = imageUrlList.get(randomInt);
                        handlerParams.put(entry.getKey(), image);
                        // 备份待使用图片
                        useImageList.add(image);
                    } else {
                        handlerParams.put(entry.getKey(), entry.getValue());
                    }
                }
                imageExecuteRequest.setParams(handlerParams);
                imageExecuteRequests.add(imageExecuteRequest);
            }
            bathImageExecuteRequest.setImageRequests(imageExecuteRequests);

            imageCreateRequest.setPlanUid(planUid);
            imageCreateRequest.setBusinessUid(businessUid);
            imageCreateRequest.setType(XhsCreativeContentTypeEnums.PICTURE.name());
            imageCreateRequest.setTempUid(String.join(",", templateIdList));
            imageCreateRequest.setExecuteParams(XhsCreativeContentExecuteParamsDTO.ofBathImage(bathImageExecuteRequest));
            imageCreateRequest.setUsePicture(useImageList.stream().distinct().collect(Collectors.toList()));
            xhsCreativeContentCreateReqList.add(imageCreateRequest);
        }
        xhsCreativeContentService.create(xhsCreativeContentCreateReqList);

    }

    /**
     * 处理文案参数
     *
     * @param config 配置
     * @return 文案执行请求列表
     */
    private List<XhsAppExecuteRequest> handlerAppExecuteRequestList(CreativePlanConfigDTO config) {
        // 文案列表
        List<String> copyWritingList = config.getCopyWritingList();
        if (CollectionUtil.isEmpty(copyWritingList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_COPY_WRITING_NOT_EXIST);
        }
        AppMarketListQuery query = new AppMarketListQuery();
        query.setIsSimple(Boolean.FALSE);
        query.setUidList(copyWritingList);
        List<AppMarketRespVO> list = appMarketService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_COPY_WRITING_NOT_EXIST);
        }

        // 处理变量列表
        Map<String, Object> variableItemMap = CollectionUtil.emptyIfNull(config.getVariableList()).stream()
                .collect(Collectors.toMap(VariableItemDTO::getField, VariableItemDTO::getValue));

        List<XhsAppExecuteRequest> appExecuteRequestList = new ArrayList<>();
        for (AppMarketRespVO marketApp : list) {
            List<VariableItemRespVO> variableList = Optional.of(marketApp).map(AppMarketRespVO::getWorkflowConfig)
                    .map(WorkflowConfigRespVO::getSteps)
                    .map(steps -> steps.get(0)).map(WorkflowStepWrapperRespVO::getVariable)
                    .map(VariableRespVO::getVariables)
                    .orElseThrow(() -> ServiceExceptionUtil.exception(new ErrorCode(310900100, "系统步骤不能为空")));

            XhsAppExecuteRequest appExecuteRequest = new XhsAppExecuteRequest();
            appExecuteRequest.setUid(marketApp.getUid());
            appExecuteRequest.setScene(AppSceneEnum.XHS_WRITING.name());
            Map<String, Object> params = new HashMap<>();
            for (VariableItemRespVO itemResponse : variableList) {
                params.put(itemResponse.getField(), variableItemMap.getOrDefault(itemResponse.getField(), null));
            }
            appExecuteRequest.setParams(params);
            appExecuteRequestList.add(appExecuteRequest);
        }
        return appExecuteRequestList;
    }

    /**
     * 处理图片参数
     *
     * @param config 配置信息
     * @return 图片请求
     */
    private List<XhsBathImageExecuteRequest> handlerCreativePlanImageRequestList(CreativePlanConfigDTO config) {

        // 图片风格参数列表
        List<XhsImageStyleDTO> imageStyleList = config.getImageStyleList();
        if (CollectionUtil.isEmpty(imageStyleList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_IMAGE_STYLE_EMPTY);
        }

        List<XhsBathImageExecuteRequest> bathImageExecuteRequestList = new ArrayList<>();

        for (XhsImageStyleDTO imageStyle : imageStyleList) {
            // 耽搁图片风格里面的图片里模板列表
            List<XhsImageTemplateDTO> templateList = imageStyle.getTemplateList();
            // 批量处理图片请求
            XhsBathImageExecuteRequest bathImageExecuteRequest = new XhsBathImageExecuteRequest();
            // 图片参数信息
            List<XhsImageExecuteRequest> imageExecuteRequestList = new ArrayList<>();

            // 处理图片变量
            for (XhsImageTemplateDTO imageTemplate : templateList) {
                Map<String, Object> params = new HashMap<>();
                for (VariableItemDTO variableItem : CollectionUtil.emptyIfNull(imageTemplate.getVariables())) {
                    // 图片参数，随机放入图片
                    if ("IMAGE".equals(variableItem.getType())) {
                        params.put(variableItem.getField(), TEMP_IMAGE_VALUE);
                    } else {
                        params.put(variableItem.getField(), variableItem.getValue());
                    }
                }
                XhsImageExecuteRequest imageExecuteRequest = new XhsImageExecuteRequest();
                imageExecuteRequest.setImageTemplate(imageTemplate.getId());
                imageExecuteRequest.setParams(params);
                imageExecuteRequestList.add(imageExecuteRequest);
            }

            bathImageExecuteRequest.setImageRequests(imageExecuteRequestList);
            bathImageExecuteRequestList.add(bathImageExecuteRequest);
        }

        return bathImageExecuteRequestList;
    }

    /**
     * 获取复制名称
     *
     * @param name 名称
     * @return 复制名称
     */
    private String getCopyName(String name) {
        String copyName = name + "-Copy";
        if (!creativePlanMapper.distinctName(copyName)) {
            return copyName;
        }
        return getCopyName(copyName);
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
