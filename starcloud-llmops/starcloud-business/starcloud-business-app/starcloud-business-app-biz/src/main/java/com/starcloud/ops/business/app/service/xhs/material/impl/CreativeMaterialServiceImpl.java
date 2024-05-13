package com.starcloud.ops.business.app.service.xhs.material.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.CreativeMaterialGenerationDTO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.BaseMaterialVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.FilterMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ModifyMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.MaterialRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.xhs.material.CreativeMaterialConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.material.CreativeMaterialDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.material.CreativeMaterialMapper;
import com.starcloud.ops.business.app.domain.parser.JsonSchemaParser;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialFieldTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialService;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_NOT_EXIST;

@Slf4j
@Service
public class CreativeMaterialServiceImpl implements CreativeMaterialService {

    @Resource
    private CreativeMaterialMapper materialMapper;

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private AppService appService;

    @Override
    public Map<String, Object> metadata() {
        Map<String, Object> result = new HashMap<>();
        result.put(MaterialTypeEnum.class.getSimpleName(), MaterialTypeEnum.allOptions());
        result.put(FieldTypeEnum.class.getSimpleName(), FieldTypeEnum.options());
        return result;
    }

    @Override
    public void creatMaterial(BaseMaterialVO reqVO) {
        AbstractCreativeMaterialDTO materialDetail = reqVO.getMaterialDetail();
        materialDetail.valid();
        CreativeMaterialDO materialDO = CreativeMaterialConvert.INSTANCE.convert(reqVO, materialDetail);
        materialDO.setUid(IdUtil.fastSimpleUUID());
        materialMapper.insert(materialDO);
    }

    @Override
    public void deleteMaterial(String uid) {
        CreativeMaterialDO materialDO = getByUid(uid);
        materialMapper.deleteById(materialDO.getId());
    }

    @Override
    public void modifyMaterial(ModifyMaterialReqVO reqVO) {
        AbstractCreativeMaterialDTO materialDetail = reqVO.getMaterialDetail();
        materialDetail.valid();
        CreativeMaterialDO materialDO = getByUid(reqVO.getUid());
        CreativeMaterialDO updateDO = CreativeMaterialConvert.INSTANCE.convert(reqVO, materialDetail);
        updateDO.setId(materialDO.getId());
        materialMapper.updateById(updateDO);
    }

    @Override
    public List<MaterialRespVO> filterMaterial(FilterMaterialReqVO queryReq) {
        List<CreativeMaterialDO> creativeMaterialDOList = materialMapper.filterMaterial(queryReq);
        return CreativeMaterialConvert.INSTANCE.convert(creativeMaterialDOList);
    }

    @Override
    public void batchInsert(List<? extends AbstractCreativeMaterialDTO> materialDTOList) {
        List<CreativeMaterialDO> materialDOList = CreativeMaterialConvert.INSTANCE.convert2(materialDTOList);
        materialMapper.insertBatch(materialDOList);
    }

    /**
     * 素材生成
     *
     * @param request 请求
     */
    @SuppressWarnings("all")
    @Override
    public Object materialGenerate(CreativeMaterialGenerationDTO request) {
        AppValidate.notEmpty(request.getMaterialList(), "素材列表不能为空");
        AppValidate.notEmpty(request.getFieldList(), "所有字段定义列表不能为空");
        AppValidate.notEmpty(request.getCheckedFieldList(), "选中的字段定义列表不能为空");
        AppValidate.notBlank(request.getRequirement(), "素材生成要求不能为空");

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
        List<AbstractCreativeMaterialDTO> materialList = request.getMaterialList();
        // 所有字段定义列表
        List<MaterialFieldConfigDTO> fieldList = request.getFieldList();
        // 选中的字段定义列表
        List<String> checkedFieldList = request.getCheckedFieldList();
        // 素材要求
        String requirement = request.getRequirement();

        // 合并字段列表，使选中的字段配置完整
        List<MaterialFieldConfigDTO> mergeCheckedFieldList = mergeCheckedFieldList(checkedFieldList, fieldList);
        // 素材字段配置转换为 JSON Schema
        JsonSchema jsonSchema = materialFieldToJsonSchema(mergeCheckedFieldList, Boolean.TRUE);

        Map<String, Object> materialMap = new HashMap<>();
        materialMap.put("MATERIAL_LIST", JsonUtils.toJsonString(materialList));
        materialMap.put("FIELD_LIST", JsonUtils.toJsonString(fieldList));
        materialMap.put("CHECKED_FIELD_LIST", JsonUtils.toJsonString(checkedFieldList));
        materialMap.put("REQUIREMENT", requirement);
        materialMap.put("JSON_SCHEMA", JsonUtils.toJsonPrettyString(jsonSchema));

        // 将处理后的数据放入到应用变量中
        appMarketResponse.putStepVariable(stepId, materialMap);

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
     */
    @SuppressWarnings("all")
    @Override
    public void customMaterialGenerate(CreativeMaterialGenerationDTO request, SseEmitter sseEmitter) {
        AppValidate.notEmpty(request.getFieldList(), "所有字段定义列表不能为空");
        AppValidate.notEmpty(request.getCheckedFieldList(), "选中的字段定义列表不能为空");
        AppValidate.notBlank(request.getRequirement(), "素材生成要求不能为空");
        AppValidate.notNull(request.getGenerateCount(), "生成数量不能为空");

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
        List<MaterialFieldConfigDTO> fieldList = request.getFieldList();
        // 选中的字段定义列表
        List<String> checkedFieldList = request.getCheckedFieldList();
        // 素材生成要求
        String requirement = request.getRequirement();
        // 素材生成数量
        Integer generateCount = request.getGenerateCount();

        // 合并字段列表，使选中的字段配置完整
        List<MaterialFieldConfigDTO> mergeCheckedFieldList = mergeCheckedFieldList(checkedFieldList, fieldList);
        // 素材字段配置转换为 JSON Schema
        JsonSchema jsonSchema = materialFieldToJsonSchema(mergeCheckedFieldList, Boolean.TRUE);

        Map<String, Object> materialMap = new HashMap<>();
        materialMap.put("FIELD_LIST", JsonUtils.toJsonString(fieldList));
        materialMap.put("CHECKED_FIELD_LIST", JsonUtils.toJsonString(checkedFieldList));
        materialMap.put("REQUIREMENT", requirement);
        materialMap.put("GENERATE_COUNT", generateCount);
        materialMap.put("JSON_SCHEMA", JsonUtils.toJsonPrettyString(jsonSchema));

        // 将处理后的数据放入到应用变量中
        appMarketResponse.putStepVariable(stepId, materialMap);

        // 构造请求
        AppExecuteReqVO appExecuteRequest = new AppExecuteReqVO();
        appExecuteRequest.setAppUid(appMarketResponse.getUid());
        appExecuteRequest.setStepId(stepId);
        appExecuteRequest.setSseEmitter(sseEmitter);
        appExecuteRequest.setContinuous(Boolean.FALSE);
        appExecuteRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        appExecuteRequest.setUserId(SecurityFrameworkUtils.getLoginUserId());
        appExecuteRequest.setAppReqVO(AppConvert.INSTANCE.convertRequest(appMarketResponse));
        // 执行应用
        appService.asyncExecute(appExecuteRequest);
    }

    private CreativeMaterialDO getByUid(String uid) {
        CreativeMaterialDO materialDO = materialMapper.getByUid(uid);
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
    private List<MaterialFieldConfigDTO> mergeCheckedFieldList(List<String> checkedFieldList, List<MaterialFieldConfigDTO> fieldList) {
        Map<String, MaterialFieldConfigDTO> fieldMap = CollectionUtil.emptyIfNull(fieldList).stream()
                .collect(Collectors.toMap(MaterialFieldConfigDTO::getFieldName, Function.identity()));

        List<MaterialFieldConfigDTO> mergeCheckedFieldList = new ArrayList<>();
        for (String fieldName : checkedFieldList) {
            MaterialFieldConfigDTO field = fieldMap.get(fieldName);
            if (Objects.isNull(field)) {
                continue;
            }
            mergeCheckedFieldList.add(field);
        }
        return mergeCheckedFieldList;
    }

    /**
     * 素材字段配置转换为 JSON Schema
     *
     * @param fieldList 素材字段配置列表
     * @param isArray   是否为数组
     * @return JSON Schema 字符串
     */
    @SuppressWarnings("all")
    private static JsonSchema materialFieldToJsonSchema(List<MaterialFieldConfigDTO> fieldList, Boolean isArray) {
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
    private static ArraySchema materialFieldToArraySchema(List<MaterialFieldConfigDTO> fieldList) {
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
    private static ObjectSchema materialFieldToObjectSchema(List<MaterialFieldConfigDTO> fieldList) {
        // 创建 JSON Schema 对象
        ObjectSchema objectSchema = JsonSchemaUtils.generateJsonSchema(Object.class).asObjectSchema();
        objectSchema.setId("urn:jsonschema:material:object:" + IdUtil.fastSimpleUUID());
        objectSchema.setDescription("素材字段信息");
        objectSchema.setRequired(true);

        // 遍历字段配置，向 JSON Schema 对象中添加字段
        for (MaterialFieldConfigDTO materialField : CollectionUtil.emptyIfNull(fieldList)) {
            String type = materialField.getType();
            if (MaterialFieldTypeEnum.string.getTypeCode().equals(type) ||
                    MaterialFieldTypeEnum.image.getTypeCode().equals(type) ||
                    MaterialFieldTypeEnum.document.getTypeCode().equals(type) ||
                    MaterialFieldTypeEnum.textBox.getTypeCode().equals(type)) {

                // 字符串类型JsonSchema
                StringSchema stringSchema = JsonSchemaUtils.generateJsonSchema(String.class).asStringSchema();
                stringSchema.setRequired(materialField.isRequired());
                stringSchema.setDescription(materialField.getDesc());

                // 添加到对象中
                objectSchema.putProperty(materialField.getFieldName(), stringSchema);
            } else {
                throw new IllegalArgumentException("不支持的素材字段类型：" + type);
            }
        }

        return objectSchema;
    }

}
