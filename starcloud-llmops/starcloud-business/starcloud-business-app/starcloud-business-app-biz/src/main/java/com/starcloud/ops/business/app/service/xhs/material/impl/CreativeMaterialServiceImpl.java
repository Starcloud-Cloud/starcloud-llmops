package com.starcloud.ops.business.app.service.xhs.material.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
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
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialFieldTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialService;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_NOT_EXIST;

@Slf4j
@Service
public class CreativeMaterialServiceImpl implements CreativeMaterialService {

    @Resource
    private CreativeMaterialMapper materialMapper;

    @Resource
    private AppMarketService appMarketService;

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
    @Override
    public Object materialGenerate(CreativeMaterialGenerationDTO request) {
        AppValidate.notEmpty(request.getMaterialList(), "素材列表不能为空");
        AppValidate.notEmpty(request.getSelectedFieldList(), "选中的字段定义列表不能为空");
        AppValidate.notBlank(request.getMaterialRequirement(), "素材生成要求不能为空");

        List<AbstractCreativeMaterialDTO> materialList = request.getMaterialList();
        String jsonSchema = materialFieldToJsonSchema(request.getSelectedFieldList(), Boolean.TRUE);
        String materialRequirement = request.getMaterialRequirement();

        Map<String, Object> materialMap = new HashMap<>();
        materialMap.put("MATERIAL_LIST", materialList);
        materialMap.put("JSON_SCHEMA", jsonSchema);
        materialMap.put("MATERIAL_REQUIREMENT", materialRequirement);

        // 根据标签查询生成素材的应用信息
        AppMarketListQuery query = new AppMarketListQuery();
        query.setTags(Arrays.asList("小红书", "Material"));
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
        appMarketResponse.putStepVariable(stepId, materialMap);

        // 构造请求
        AppExecuteReqVO appExecuteRequest = new AppExecuteReqVO();
        appExecuteRequest.setAppUid(appMarketResponse.getUid());
        appExecuteRequest.setStepId(stepId);
        appExecuteRequest.setContinuous(Boolean.TRUE);
        appExecuteRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        appExecuteRequest.setUserId(SecurityFrameworkUtils.getLoginUserId());
        appExecuteRequest.setAppReqVO(AppConvert.INSTANCE.convertRequest(appMarketResponse));
        // 获取应用Entity
        AppMarketEntity appMarketEntity = (AppMarketEntity) AppFactory.factory(appExecuteRequest);
        // 执行应用
        AppExecuteRespVO executeResponse = appMarketEntity.execute(appExecuteRequest);
        if (!executeResponse.getSuccess()) {
            throw new IllegalArgumentException("生成素材失败：" + executeResponse.getResultDesc());
        }

        return executeResponse.getResult();
    }

    private CreativeMaterialDO getByUid(String uid) {
        CreativeMaterialDO materialDO = materialMapper.getByUid(uid);
        if (Objects.isNull(materialDO)) {
            throw exception(MATERIAL_NOT_EXIST, uid);
        }
        return materialDO;
    }

    /**
     * 素材字段配置转换为 JSON Schema
     *
     * @param fieldList 素材字段配置列表
     * @param isArray   是否为数组
     * @return JSON Schema 字符串
     */
    @SuppressWarnings("all")
    private static String materialFieldToJsonSchema(List<MaterialFieldConfigDTO> fieldList, Boolean isArray) {
        if (CollectionUtil.isEmpty(fieldList)) {
            throw new IllegalArgumentException("素材字段配置列表不能为空！");
        }
        // 创建 JSON Schema 对象
        JsonSchema jsonSchema = JsonSchemaUtils.generateJsonSchema(Object.class);
        jsonSchema.asObjectSchema().setDescription("素材字段配置");
        jsonSchema.asObjectSchema().setRequired(true);

        if (isArray) {
            JsonSchema arraySchema = JsonSchemaUtils.generateJsonSchema(List.class);
            JsonSchema itemsSchema = JsonSchemaUtils.generateJsonSchema(Object.class);
            for (MaterialFieldConfigDTO materialField : fieldList) {
                String type = materialField.getType();
                // 字符串类型
                if (MaterialFieldTypeEnum.string.getTypeCode().equals(type) ||
                        MaterialFieldTypeEnum.image.getTypeCode().equals(type) ||
                        MaterialFieldTypeEnum.document.getTypeCode().equals(type) ||
                        MaterialFieldTypeEnum.textBox.getTypeCode().equals(type)) {
                    JsonSchema propertySchema = JsonSchemaUtils.generateJsonSchema(String.class);
                    propertySchema.setDescription(materialField.getDesc());
                    propertySchema.setRequired(materialField.isRequired());
                    itemsSchema.asObjectSchema().putProperty(materialField.getFieldName(), propertySchema);
                } else {
                    throw new IllegalArgumentException("不支持的素材字段类型：" + type);
                }
            }
            arraySchema.asArraySchema().setItemsSchema(itemsSchema);
            jsonSchema.asObjectSchema().putProperty("materialList", arraySchema);
        } else {
            for (MaterialFieldConfigDTO materialField : fieldList) {
                String type = materialField.getType();
                // 字符串类型
                if (MaterialFieldTypeEnum.string.getTypeCode().equals(type) ||
                        MaterialFieldTypeEnum.image.getTypeCode().equals(type) ||
                        MaterialFieldTypeEnum.document.getTypeCode().equals(type) ||
                        MaterialFieldTypeEnum.textBox.getTypeCode().equals(type)) {
                    JsonSchema propertySchema = JsonSchemaUtils.generateJsonSchema(String.class);
                    propertySchema.setDescription(materialField.getDesc());
                    propertySchema.setRequired(materialField.isRequired());
                    jsonSchema.asObjectSchema().putProperty(materialField.getFieldName(), propertySchema);
                } else {
                    throw new IllegalArgumentException("不支持的素材字段类型：" + type);
                }
            }
        }
        // 转换为 JSON 字符串
        return JsonUtils.toJsonPrettyString(jsonSchema);
    }

}
