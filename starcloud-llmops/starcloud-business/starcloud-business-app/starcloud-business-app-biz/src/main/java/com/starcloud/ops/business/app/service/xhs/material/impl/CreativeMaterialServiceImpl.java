package com.starcloud.ops.business.app.service.xhs.material.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.BaseMaterialVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.FilterMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.GeneralFieldCodeReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ModifyMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.MaterialRespLogVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.MaterialRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.xhs.material.CreativeMaterialConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.material.CreativeMaterialDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.material.CreativeMaterialMapper;
import com.starcloud.ops.business.app.domain.parser.JsonSchemaParser;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.ColumnTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialFieldTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.model.creative.CreativeMaterialGenerationDTO;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialService;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.business.app.util.PinyinUtils;
import com.starcloud.ops.business.app.utils.MaterialDefineUtil;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageUidReqVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.util.RandomUtil.BASE_CHAR_NUMBER_LOWER;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.FILED_DESC_IS_BLANK;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.FILED_DESC_LENGTH;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_NOT_EXIST;

@Slf4j
@Service
public class CreativeMaterialServiceImpl implements CreativeMaterialService {

    @Resource
    private CreativeMaterialMapper creativeMaterialMapper;

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private AppService appService;

    @Resource
    private CreativePlanService creativePlanService;

    @Resource
    private AppLogService appLogService;

    @Resource
    private MaterialLibraryService materialLibraryService;

    @Override
    public Map<String, Object> metadata() {
        Map<String, Object> result = new HashMap<>();
        result.put(MaterialTypeEnum.class.getSimpleName(), MaterialTypeEnum.allOptions());
        result.put(FieldTypeEnum.class.getSimpleName(), FieldTypeEnum.options());
        result.put(MaterialFieldTypeEnum.class.getSimpleName(), MaterialFieldTypeEnum.options());
        return result;
    }

    @Override
    public void creatMaterial(BaseMaterialVO reqVO) {
        AbstractCreativeMaterialDTO materialDetail = reqVO.getMaterialDetail();
        materialDetail.valid();
        CreativeMaterialDO materialDO = CreativeMaterialConvert.INSTANCE.convert(reqVO, materialDetail);
        materialDO.setUid(IdUtil.fastSimpleUUID());
        creativeMaterialMapper.insert(materialDO);
    }

    @Override
    public void deleteMaterial(String uid) {
        CreativeMaterialDO materialDO = getByUid(uid);
        creativeMaterialMapper.deleteById(materialDO.getId());
    }

    @Override
    public void modifyMaterial(ModifyMaterialReqVO reqVO) {
        AbstractCreativeMaterialDTO materialDetail = reqVO.getMaterialDetail();
        materialDetail.valid();
        CreativeMaterialDO materialDO = getByUid(reqVO.getUid());
        CreativeMaterialDO updateDO = CreativeMaterialConvert.INSTANCE.convert(reqVO, materialDetail);
        updateDO.setId(materialDO.getId());
        creativeMaterialMapper.updateById(updateDO);
    }

    @Override
    public List<MaterialRespVO> filterMaterial(FilterMaterialReqVO queryReq) {
        List<CreativeMaterialDO> creativeMaterialDOList = creativeMaterialMapper.filterMaterial(queryReq);
        return CreativeMaterialConvert.INSTANCE.convert(creativeMaterialDOList);
    }

    @Override
    public void batchInsert(List<? extends AbstractCreativeMaterialDTO> materialDTOList) {
        List<CreativeMaterialDO> materialDOList = CreativeMaterialConvert.INSTANCE.convert2(materialDTOList);
        creativeMaterialMapper.insertBatch(materialDOList);
    }

    /**
     * 素材生成
     *
     * @param request 请求
     */
    @SuppressWarnings("all")
    @Override
    public JSON materialGenerate(CreativeMaterialGenerationDTO request) {
        AppValidate.notEmpty(request.getMaterialList(), "素材列表不能为空");
        AppValidate.notEmpty(request.getCheckedFieldList(), "选中的字段定义列表不能为空");
        AppValidate.notBlank(request.getRequirement(), "素材生成要求不能为空");

        AppValidate.notBlank(request.getBizUid(), "素材库UID不能为空");
        MaterialLibraryRespVO materialLibrary = materialLibraryService.getMaterialLibraryByUid(request.getBizUid());
//        if (StringUtils.isBlank(request.getPlanSource())) {
//        } else {
//            AppValidate.notBlank(request.getBizUid(), "应用UID不能为空！");
//            AppValidate.notBlank(request.getPlanSource(), "应用来源不能为空！");
//            CreativePlanSourceEnum planSource = CreativePlanSourceEnum.of(request.getPlanSource());
//            AppValidate.notNull(planSource, "应用来源不支持！");
//
//            MaterialLibraryAppReqVO materialLibraryRequest = new MaterialLibraryAppReqVO();
//            materialLibraryRequest.setAppUid(request.getBizUid());
//            materialLibraryRequest.setAppType(planSource.getCode());
//            materialLibraryRequest.setUserId(SecurityFrameworkUtils.getLoginUserId());
//            materialLibrary = materialLibraryService.getMaterialLibraryByUid(request.getBizUid());
//        }

        AppValidate.notNull(materialLibrary, "未找到素材库配置，请联系管理员！");
        AppValidate.notEmpty(materialLibrary.getTableMeta(), "素材库字段配置为空，请联系管理员！");

        // 根据标签查询生成素材的应用信息
        AppMarketListQuery query = new AppMarketListQuery();
        query.setTags(Collections.singletonList("CreativeMaterialGenerate"));
        List<AppMarketRespVO> list = appMarketService.list(query);
        AppValidate.notEmpty(list, "未找到生成素材的应用信息，请联系管理员！");

        // 获取第一个应用信息
        AppMarketRespVO appMarketResponse = list.get(0);
        // 获取第一个步骤
        String stepId = Optional.ofNullable(appMarketResponse.getWorkflowConfig())
                .map(WorkflowConfigRespVO::getSteps)
                .map(stepList -> stepList.get(0))
                .map(WorkflowStepWrapperRespVO::getField)
                .orElseThrow(() -> new IllegalArgumentException("生成素材的应用信息配置异常！请联系管理员"));

        // 素材数据
        List<Map<String, Object>> materialList = request.getMaterialList();
        // 去除空值
        MaterialDefineUtil.removeNull(materialList);

        // 所有字段定义列表
        List<MaterialLibraryTableColumnRespVO> fieldList = materialLibrary.getTableMeta();
        // 选中的字段定义列表
        List<String> checkedFieldList = request.getCheckedFieldList();
        // 素材要求
        String requirement = request.getRequirement();

        // 合并字段列表，使选中的字段配置完整
        List<MaterialLibraryTableColumnRespVO> mergeCheckedFieldList = mergeCheckedFieldList(checkedFieldList, fieldList);
        // 素材字段配置转换为 JSON Schema
        JsonSchema jsonSchema = materialFieldToJsonSchema(mergeCheckedFieldList, Boolean.TRUE);

        // 排序
        List<String> sortedField = fieldList.stream()
                .filter(config -> !ColumnTypeEnum.IMAGE.getCode().equals(config.getColumnType()))
                .sorted(Comparator.comparingLong(MaterialLibraryTableColumnRespVO::getSequence))
                .map(MaterialLibraryTableColumnRespVO::getColumnCode)
                .collect(Collectors.toList());

        // MATERIAL_LIST 移除选中的字段 uuid,group 并排序
        List<Map<String, Object>> cleanMaterialList = cleanMaterialList(materialList, checkedFieldList, sortedField);

        // FIELD_LIST 只保留fieldName,desc两个字段
        List<Map<String, String>> fieldMapList = cleanFieldConfig(fieldList);

        appMarketResponse.putVariable(stepId, "MATERIAL_LIST", JsonUtils.toJsonPrettyString(cleanMaterialList));
        appMarketResponse.putVariable(stepId, "FIELD_LIST", JsonUtils.toJsonPrettyString(fieldMapList));
        appMarketResponse.putVariable(stepId, "CHECKED_FIELD_LIST", JSONUtil.toJsonPrettyStr(mergeCheckedFieldList.stream()
                .map(MaterialLibraryTableColumnRespVO::getColumnCode)
                .collect(Collectors.toList())));
        appMarketResponse.putVariable(stepId, "REQUIREMENT", requirement);
        appMarketResponse.putVariable(stepId, "JSON_SCHEMA", JsonUtils.toJsonPrettyString(jsonSchema));

        // 构造请求
        AppExecuteReqVO appExecuteRequest = new AppExecuteReqVO();
        appExecuteRequest.setAppUid(appMarketResponse.getUid());
        appExecuteRequest.setStepId(stepId);
        appExecuteRequest.setContinuous(Boolean.FALSE);
        appExecuteRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        appExecuteRequest.setUserId(SecurityFrameworkUtils.getLoginUserId());
        appExecuteRequest.setAppReqVO(AppConvert.INSTANCE.convertRequest(appMarketResponse));
        // 执行应用
        AppExecuteRespVO executeResponse = appService.execute(appExecuteRequest);
        if (!executeResponse.getSuccess() || executeResponse.getResult() == null) {
            throw new IllegalArgumentException("生成素材失败：" + executeResponse.getResultDesc());
        }

        // 获取执行结果
        Object result = executeResponse.getResult();
        log.info("生成素材结果：{}", result);
        JsonSchemaParser jsonSchemaParser = new JsonSchemaParser(jsonSchema);
        return jsonSchemaParser.parse(String.valueOf(result));
    }

    /**
     * 自定义素材生成
     *
     * @param request 请求
     * @return 素材
     */
    @SuppressWarnings("all")
    @Override
    public JSON customMaterialGenerate(CreativeMaterialGenerationDTO request) {
        AppValidate.notEmpty(request.getCheckedFieldList(), "选中的字段定义列表不能为空");
        AppValidate.notBlank(request.getRequirement(), "素材生成要求不能为空");
        AppValidate.notNull(request.getGenerateCount(), "生成数量不能为空");

        MaterialLibraryRespVO materialLibrary = materialLibraryService.getMaterialLibraryByUid(request.getBizUid());
//        if (StringUtils.isBlank(request.getPlanSource())) {
//            AppValidate.notBlank(request.getBizUid(), "素材库UID不能为空");
//            materialLibrary = materialLibraryService.getMaterialLibraryByUid(request.getBizUid());
//        } else {
//            AppValidate.notBlank(request.getBizUid(), "应用UID不能为空！");
//            AppValidate.notBlank(request.getPlanSource(), "应用来源不能为空！");
//            CreativePlanSourceEnum planSource = CreativePlanSourceEnum.of(request.getPlanSource());
//            AppValidate.notNull(planSource, "应用来源不支持！");
//
//            MaterialLibraryAppReqVO materialLibraryRequest = new MaterialLibraryAppReqVO();
//            materialLibraryRequest.setAppUid(request.getBizUid());
//            materialLibraryRequest.setAppType(planSource.getCode());
//            materialLibraryRequest.setUserId(SecurityFrameworkUtils.getLoginUserId());
//            materialLibrary = materialLibraryService.getMaterialLibraryByApp(materialLibraryRequest);
//        }

        AppValidate.notNull(materialLibrary, "未找到素材库配置，请联系管理员！");
        AppValidate.notEmpty(materialLibrary.getTableMeta(), "素材库字段配置为空，请联系管理员！");

        // 根据标签查询生成素材的应用信息
        AppMarketListQuery query = new AppMarketListQuery();
        query.setTags(Collections.singletonList("CreativeMaterialCustomGenerate"));
        List<AppMarketRespVO> list = appMarketService.list(query);
        AppValidate.notEmpty(list, "未找到生成素材的应用信息，请联系管理员！");

        // 获取第一个应用信息
        AppMarketRespVO appMarketResponse = list.get(0);
        // 获取第一个步骤
        String stepId = Optional.ofNullable(appMarketResponse.getWorkflowConfig())
                .map(WorkflowConfigRespVO::getSteps)
                .map(stepList -> stepList.get(0))
                .map(WorkflowStepWrapperRespVO::getField)
                .orElseThrow(() -> new IllegalArgumentException("生成素材的应用信息配置异常！请联系管理员"));

        // 所有字段定义列表
        List<MaterialLibraryTableColumnRespVO> fieldList = materialLibrary.getTableMeta();
        // 选中的字段定义列表
        List<String> checkedFieldList = request.getCheckedFieldList();
        // 素材生成要求
        String requirement = request.getRequirement();
        // 素材生成数量
        Integer generateCount = request.getGenerateCount();

        // 合并字段列表，使选中的字段配置完整
        List<MaterialLibraryTableColumnRespVO> mergeCheckedFieldList = mergeCheckedFieldList(checkedFieldList, fieldList);
        // 素材字段配置转换为 JSON Schema
        JsonSchema jsonSchema = materialFieldToJsonSchema(mergeCheckedFieldList, Boolean.TRUE);

        // FIELD_LIST 只保留fieldName,desc两个字段
        List<Map<String, String>> fieldMapList = cleanFieldConfig(fieldList);

        appMarketResponse.putVariable(stepId, "FIELD_LIST", JsonUtils.toJsonPrettyString(fieldMapList));
        appMarketResponse.putVariable(stepId, "CHECKED_FIELD_LIST", JSONUtil.toJsonPrettyStr(mergeCheckedFieldList.stream()
                .map(MaterialLibraryTableColumnRespVO::getColumnCode)
                .collect(Collectors.toList())));
        appMarketResponse.putVariable(stepId, "REQUIREMENT", requirement);
        appMarketResponse.putVariable(stepId, "GENERATE_COUNT", generateCount);
        appMarketResponse.putVariable(stepId, "JSON_SCHEMA", JsonSchemaUtils.jsonSchema2Str(jsonSchema));

        // 构造请求
        AppExecuteReqVO appExecuteRequest = new AppExecuteReqVO();
        appExecuteRequest.setAppUid(appMarketResponse.getUid());
        appExecuteRequest.setStepId(stepId);
        appExecuteRequest.setContinuous(Boolean.FALSE);
        appExecuteRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        appExecuteRequest.setUserId(SecurityFrameworkUtils.getLoginUserId());
        appExecuteRequest.setAppReqVO(AppConvert.INSTANCE.convertRequest(appMarketResponse));
        // 执行应用
        AppExecuteRespVO executeResponse = appService.execute(appExecuteRequest);
        if (!executeResponse.getSuccess() || executeResponse.getResult() == null) {
            throw new IllegalArgumentException("生成素材失败：" + executeResponse.getResultDesc());
        }

        // 获取执行结果
        Object result = executeResponse.getResult();
        log.info("生成素材结果：{}", result);
        JsonSchemaParser jsonSchemaParser = new JsonSchemaParser(jsonSchema);
        return jsonSchemaParser.parse(String.valueOf(result));
    }

    @Override
    public List<MaterialFieldConfigDTO> generalFieldCode(GeneralFieldCodeReqVO reqVO) {
        List<MaterialFieldConfigDTO> fieldConfigList = reqVO.getFieldConfigDTOList();
        MaterialDefineUtil.verifyMaterialFieldDesc(fieldConfigList);

        // 已有fieldName的字段排在前面，不重复生成，防止新字段fieldName重复
        fieldConfigList = fieldConfigList.stream().sorted((a, b) -> {
            if (StringUtils.isBlank(b.getFieldName())) {
                return -1;
            }
            return 1;
        }).collect(Collectors.toList());

        // 已有的fieldName 验证新生成的是否重复
        List<String> fieldCodeExist = new ArrayList<>(fieldConfigList.size());
        for (MaterialFieldConfigDTO materialFieldConfigDTO : fieldConfigList) {
            String desc = materialFieldConfigDTO.getDesc();
            if (StringUtils.isBlank(desc)) {
                throw exception(FILED_DESC_IS_BLANK);
            }
            if (desc.length() > 20) {
                throw exception(FILED_DESC_LENGTH, desc);
            }
            // 已有fieldName的字段跳过
            if (StringUtils.isNoneBlank(materialFieldConfigDTO.getFieldName())) {
                fieldCodeExist.add(materialFieldConfigDTO.getFieldName());
                continue;
            }

            char[] nameChar = desc.trim().toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char c : nameChar) {
                sb.append(PinyinUtils.pinyinFirstChar(c));
            }
            String code = pinyinFirstCharUnique(sb.toString(), fieldCodeExist);
            fieldCodeExist.add(code);
            materialFieldConfigDTO.setFieldName(code);
        }
        return fieldConfigList.stream()
                .sorted(Comparator.comparingInt(MaterialFieldConfigDTO::getOrder)).collect(Collectors.toList());
    }

    @Override
    public Boolean judgePicture(String uid, String planSource) {
        AppMarketRespVO appRespVO = creativePlanService.getAppRespVO(uid, planSource);
        try {
            return CreativeUtils.judgePicture(appRespVO);
        } catch (Exception e) {
            // 默认返回 false 显示列表
            return false;
        }
    }

    /**
     * @param
     * @return
     */
    @Override
    public PageResult<MaterialRespLogVO> infoPageByMarketUid(AppLogConversationInfoPageUidReqVO reqVO) {
        if (getLoginUserId() == null) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        // 根据标签查询生成素材的应用信息
        AppMarketListQuery query = new AppMarketListQuery();
        query.setTags(Collections.singletonList("CreativeMaterialCustomGenerate"));
        List<AppMarketRespVO> list = appMarketService.list(query);
        AppValidate.notEmpty(list, "未找到生成素材的应用信息，请联系管理员！");

        // 获取第一个应用信息
        AppMarketRespVO appMarketResponse = list.get(0);

        AppLogConversationInfoPageUidReqVO pageUidReqVO = new AppLogConversationInfoPageUidReqVO();
        pageUidReqVO.setMarketUid(appMarketResponse.getUid());

        PageResult<AppLogMessageRespVO> appLogMessageRespVOPageResult = appLogService.pageLogConversationByMarketUid(pageUidReqVO);

        // 无结果 直接返回空结果
        if (appLogMessageRespVOPageResult.getList().isEmpty() || appLogMessageRespVOPageResult.getTotal() == 0L) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }

        ArrayList<MaterialRespLogVO> materialRespLogVOS = new ArrayList<>();
        appLogMessageRespVOPageResult.getList().forEach(logVO -> {
            List<VariableItemRespVO> variables = logVO.getAppInfo().getWorkflowConfig().getSteps().get(0).getVariable().getVariables();
            Object o = variables.stream().filter(variablesData -> variablesData.getField().equals(CreativeConstants.REQUIREMENT)).map(VariableItemRespVO::getValue).findFirst().orElse("无数据");
            materialRespLogVOS.add(new MaterialRespLogVO().setRequestContent(o.toString())
                    .setCreateTime(logVO.getCreateTime()));
        });
        PageResult<MaterialRespLogVO> materialRespLogVOPageResult = new PageResult<>();

        materialRespLogVOPageResult.setTotal(appLogMessageRespVOPageResult.getTotal());
        materialRespLogVOPageResult.setList(materialRespLogVOS);

        return materialRespLogVOPageResult;
    }

    /**
     * code重复拼接随机字符串
     *
     * @param fieldName
     * @param fieldCodeExist
     * @return
     */
    private String pinyinFirstCharUnique(String fieldName, List<String> fieldCodeExist) {
        if (fieldCodeExist.contains(fieldName)) {
            return pinyinFirstCharUnique(fieldName + RandomUtil.randomString(BASE_CHAR_NUMBER_LOWER, 1), fieldCodeExist);
        }
        return fieldName;
    }

    /**
     * 根据 UID 获取素材
     *
     * @param uid UID
     * @return 素材
     */
    private CreativeMaterialDO getByUid(String uid) {
        CreativeMaterialDO materialDO = creativeMaterialMapper.getByUid(uid);
        if (Objects.isNull(materialDO)) {
            throw exception(MATERIAL_NOT_EXIST, uid);
        }
        return materialDO;
    }

    /**
     * 合并字段列表，使选中的字段配置完整
     *
     * @param checkedFieldList 选中的字段配置列表
     * @param fieldList        所有字段配置列表
     * @return 合并后的选中的字段配置列表
     */
    private List<MaterialLibraryTableColumnRespVO> mergeCheckedFieldList(List<String> checkedFieldList, List<MaterialLibraryTableColumnRespVO> fieldList) {
        Map<String, MaterialLibraryTableColumnRespVO> fieldMap = CollectionUtil.emptyIfNull(fieldList).stream()
                .collect(Collectors.toMap(MaterialLibraryTableColumnRespVO::getColumnCode, Function.identity()));

        List<MaterialLibraryTableColumnRespVO> mergeCheckedFieldList = new ArrayList<>();
        for (String fieldName : checkedFieldList) {
            MaterialLibraryTableColumnRespVO field = fieldMap.get(fieldName);
            if (Objects.isNull(field)) {
                continue;
            }
            mergeCheckedFieldList.add(field);
        }
        return mergeCheckedFieldList.stream().sorted(Comparator.comparingLong(MaterialLibraryTableColumnRespVO::getSequence)).collect(Collectors.toList());
    }

    /**
     * 素材字段配置转换为 JSON Schema
     *
     * @param fieldList 素材字段配置列表
     * @param isArray   是否为数组
     * @return JSON Schema 字符串
     */
    @SuppressWarnings("all")
    private static JsonSchema materialFieldToJsonSchema(List<MaterialLibraryTableColumnRespVO> fieldList, Boolean isArray) {
        if (CollectionUtil.isEmpty(fieldList)) {
            throw new IllegalArgumentException("素材字段配置列表不能为空！");
        }
        if (isArray) {
            return materialFieldToArraySchema(fieldList);
        } else {
            return materialFieldToObjectSchema(fieldList);
        }
    }

    /**
     * 素材字段配置转换为 JSON Schema
     *
     * @param fieldList 素材字段配置列表
     * @return JSON Schema 字符串
     */
    private static ArraySchema materialFieldToArraySchema(List<MaterialLibraryTableColumnRespVO> fieldList) {
        ObjectSchema itemsSchema = materialFieldToObjectSchema(fieldList);
        itemsSchema.setId("urn:jsonschema:material:item:" + IdUtil.fastSimpleUUID());

        ArraySchema arraySchema = JsonSchemaUtils.generateJsonSchema(List.class).asArraySchema();
        arraySchema.setId("urn:jsonschema:material:array:" + IdUtil.fastSimpleUUID());
        arraySchema.setDescription("素材列表字段配置信息");
        arraySchema.setRequired(true);
        arraySchema.setItemsSchema(itemsSchema);
        return arraySchema;
    }

    /**
     * 素材字段配置转换为 JSON Schema
     *
     * @param fieldList 素材字段配置列表
     * @return JSON Schema 字符串
     */
    private static ObjectSchema materialFieldToObjectSchema(List<MaterialLibraryTableColumnRespVO> fieldList) {
        // 创建 JSON Schema 对象
        ObjectSchema objectSchema = JsonSchemaUtils.generateJsonSchema(Object.class).asObjectSchema();
        objectSchema.setId("urn:jsonschema:material:object:" + IdUtil.fastSimpleUUID());
        objectSchema.setDescription("素材字段信息");
        objectSchema.setRequired(true);

        // 遍历字段配置，向 JSON Schema 对象中添加字段
        for (MaterialLibraryTableColumnRespVO materialField : CollectionUtil.emptyIfNull(fieldList)) {
            Integer type = materialField.getColumnType();
            if (ColumnTypeEnum.STRING.getCode().equals(type) ||
                    ColumnTypeEnum.IMAGE.getCode().equals(type) ||
                    ColumnTypeEnum.DOCUMENT.getCode().equals(type)) {

                // 字符串类型JsonSchema
                StringSchema stringSchema = JsonSchemaUtils.generateJsonSchema(String.class).asStringSchema();
                stringSchema.setRequired(materialField.getIsRequired());
                stringSchema.setDescription(materialField.getDescription());

                // 添加到对象中
                objectSchema.putProperty(materialField.getColumnCode(), stringSchema);
            } else {
                throw new IllegalArgumentException("不支持的素材字段类型：" + type);
            }
        }

        return objectSchema;
    }


    private List<Map<String, String>> cleanFieldConfig(List<MaterialLibraryTableColumnRespVO> fieldList) {
        List<Map<String, String>> fieldMapList = new ArrayList<>(fieldList.size());
        for (MaterialLibraryTableColumnRespVO fieldConfigDTO : fieldList) {
            if (ColumnTypeEnum.IMAGE.getCode().equals(fieldConfigDTO.getColumnType())) {
                continue;
            }
            Map<String, String> fieldMap = new HashMap<>();
            fieldMap.put("fieldName", fieldConfigDTO.getColumnCode());
            fieldMap.put("desc", fieldConfigDTO.getDescription());
            fieldMapList.add(fieldMap);
        }
        return fieldMapList;
    }

    /**
     * 移除选中的字段 uuid,group 并排序
     *
     * @param materialList
     * @param checkedFieldList
     * @param sortedField
     * @return
     */
    private List<Map<String, Object>> cleanMaterialList(List<Map<String, Object>> materialList, List<String> checkedFieldList, List<String> sortedField) {
        List<Map<String, Object>> cleanMaterialList = new ArrayList<>(materialList.size());

        for (Map<String, Object> map : materialList) {
            Map<String, Object> cleanMaterial = new LinkedHashMap<>();
            for (String fieldName : sortedField) {
                if (checkedFieldList.contains(fieldName)) {
                    continue;
                }
                cleanMaterial.put(fieldName, map.get(fieldName));
            }
            cleanMaterialList.add(cleanMaterial);
        }
        return cleanMaterialList;
    }

}
