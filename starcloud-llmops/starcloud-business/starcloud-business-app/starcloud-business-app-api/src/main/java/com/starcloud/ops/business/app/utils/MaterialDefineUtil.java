package com.starcloud.ops.business.app.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.starcloud.ops.business.app.api.app.vo.request.config.WorkflowConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.WorkflowStepWrapperReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableItemReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialFieldTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.*;

public class MaterialDefineUtil {

    private static final String MATERIAL_ACTION_HANDLER = "MaterialActionHandler";

    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z]+");

    /**
     * 获取素材定义配置
     *
     * @param appMarketResponse
     * @return
     */
    public static List<MaterialFieldConfigDTO> getMaterialConfig(AppMarketRespVO appMarketResponse) {
        String materialDefine = null;
        WorkflowStepWrapperRespVO stepWrapperRespVO = appMarketResponse.getStepByHandler(MATERIAL_ACTION_HANDLER);
        if (Objects.isNull(stepWrapperRespVO)) {
            throw exception(MATERIAL_STEP_NOT_EXIST);
        }
        VariableItemRespVO variable = stepWrapperRespVO.getVariable(CreativeConstants.MATERIAL_DEFINE);
        if (Objects.nonNull(variable)) {
            materialDefine = String.valueOf(variable.getValue());
        }

        if (StringUtils.isBlank(materialDefine)) {
            throw exception(NO_MATERIAL_DEFINE);
        }
        List<MaterialFieldConfigDTO> fieldConfigList = parseConfig(materialDefine);
        if (CollUtil.isEmpty(fieldConfigList)) {
            throw exception(NO_MATERIAL_DEFINE);
        }
        return fieldConfigList;
    }

    public static List<MaterialFieldConfigDTO> parseConfig(String materialFieldJson) {
        List<MaterialFieldConfigDTO> list = JSONUtil.parseArray(materialFieldJson).toList(MaterialFieldConfigDTO.class);
        list.stream().sorted(Comparator.comparingInt(MaterialFieldConfigDTO::getOrder)).collect(Collectors.toList());
        return list;
    }

    /**
     * 验证必填字段不为空
     *
     * @param appMarketResponse
     * @param materialObjList
     */
    public static void verifyMaterialData(AppMarketRespVO appMarketResponse, List<Map<String, Object>> materialObjList) {
        List<MaterialFieldConfigDTO> fieldConfigList = getMaterialConfig(appMarketResponse);
        if (CollUtil.isEmpty(fieldConfigList)) {
            throw exception(NO_MATERIAL_DEFINE);
        }
        verifyMaterialData(fieldConfigList, materialObjList);
    }

    /**
     * 移除非定义字段
     *
     * @param fieldConfigList
     * @param materialObjList
     */
    public static void cleanMaterialData(List<MaterialFieldConfigDTO> fieldConfigList, List<Map<String, Object>> materialObjList) {
        List<String> configHeader = fieldConfigList.stream().map(MaterialFieldConfigDTO::getFieldName).collect(Collectors.toList());
        Iterator<Map<String, Object>> iterator = materialObjList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> materialData = iterator.next();
            Iterator<Map.Entry<String, Object>> keyEntryIter = materialData.entrySet().iterator();
            boolean configHeaderIsNull = true;
            // 移除非定义字段
            while (keyEntryIter.hasNext()) {
                Map.Entry<String, Object> keyEntry = keyEntryIter.next();
                if (configHeader.contains(keyEntry.getKey())) {
                    Object value = keyEntry.getValue();
                    // 定义字段存在不为空的值
                    if (!(Objects.isNull(value) || StringUtils.isBlank(String.valueOf(value)))) {
                        configHeaderIsNull = false;
                    }
                } else {
                    // 移除非定义字段
                    keyEntryIter.remove();
                }
            }
            // value全部为空移除
            if (configHeaderIsNull) {
                iterator.remove();
            }
        }


    }

    /**
     * 验证非图片必填数据
     *
     * @param fieldConfigList
     * @param materialObjList
     */
    public static void verifyMaterialData(List<MaterialFieldConfigDTO> fieldConfigList, List<Map<String, Object>> materialObjList) {
        List<MaterialFieldConfigDTO> requiredFieldList = fieldConfigList.stream()
                .filter(fieldConfig -> fieldConfig.isRequired() && !MaterialFieldTypeEnum.image.getCode().equalsIgnoreCase(fieldConfig.getType()))
                .collect(Collectors.toList());
        for (Map<String, Object> materialObj : materialObjList) {
            for (MaterialFieldConfigDTO requiredField : requiredFieldList) {
                if (Objects.isNull(materialObj.get(requiredField.getFieldName()))) {
                    throw exception(NO_REQUIRED_FILED, requiredField.getDesc());
                }
            }
        }
    }

    /**
     * 验证表头中是否有必填字段
     *
     * @param excelHeader
     * @param materialConfig
     */
    public static void verifyExcelHeader(List<Object> excelHeader, List<MaterialFieldConfigDTO> materialConfig) {
        List<String> requireHeader = materialConfig.stream().filter(MaterialFieldConfigDTO::isRequired).map(MaterialFieldConfigDTO::getDesc).collect(Collectors.toList());
        List<String> header = excelHeader.stream().map(String::valueOf).collect(Collectors.toList());
        Collection<String> subtract = CollUtil.subtract(requireHeader, header);
        if (CollUtil.isNotEmpty(subtract)) {
            throw exception(EXCEL_HEADER_REQUIRED_FILED, subtract);
        }
    }

    /**
     * 验证是否有重复字段code  重复字段名 code必须为英文字母
     *
     * @param materialConfigList
     */
    public static void verifyMaterialField(List<MaterialFieldConfigDTO> materialConfigList) {

        List<MaterialFieldConfigDTO> fieldName = materialConfigList.stream().filter(config -> PATTERN.matcher(config.getFieldName()).matches()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(fieldName)) {
            throw exception(FILED_NAME_ERROR, fieldName);
        }

        List<String> duplicateFieldDesc = materialConfigList.stream().collect(Collectors.groupingBy(MaterialFieldConfigDTO::getDesc, Collectors.counting()))
                .entrySet().stream().filter(entry -> entry.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toList());

        List<String> duplicateFieldName = materialConfigList.stream().collect(Collectors.groupingBy(MaterialFieldConfigDTO::getFieldName, Collectors.counting()))
                .entrySet().stream().filter(entry -> entry.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(duplicateFieldDesc)) {
            throw exception(DUPLICATE_FILED_DESC, duplicateFieldDesc);
        }

        if (CollUtil.isNotEmpty(duplicateFieldName)) {
            throw exception(DUPLICATE_FILED_NAME, duplicateFieldName);
        }
    }

    /**
     * 校验素材字段类型
     *
     * @param materialConfigList
     */
    public static void verifyMaterialFieldType(List<MaterialFieldConfigDTO> materialConfigList) {
        List<MaterialFieldConfigDTO> mistakeDefine = materialConfigList.stream().filter(config -> {
            return !MaterialFieldTypeEnum.TYPE_ENUM_MAP.containsKey(config.getType());
        }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(mistakeDefine)) {
            throw exception(FILED_TYPE_MISTAKE, mistakeDefine);
        }
    }


    /**
     * 应用中存在上传素材步骤，
     * 验证素材定义是否存在
     * 验证定义中是否有重复字段code 和 重复字段名
     * 字段类型是否存在
     *
     * @param workflowConfig
     */
    public static void verifyAppConfig(WorkflowConfigReqVO workflowConfig) {
        if (Objects.isNull(workflowConfig)) {
            return;
        }
        if (CollUtil.isEmpty(workflowConfig.getSteps())) {
            return;
        }
        for (WorkflowStepWrapperReqVO step : workflowConfig.getSteps()) {
            if (Objects.isNull(step.getFlowStep())) {
                continue;
            }

            if (MATERIAL_ACTION_HANDLER.equalsIgnoreCase(step.getFlowStep().getHandler())) {

                List<VariableItemReqVO> variableItemReqs = Optional.ofNullable(step.getVariable()).map(VariableReqVO::getVariables).orElse(new ArrayList<>());
                Optional<VariableItemReqVO> materialDefineVariable = variableItemReqs.stream()
                        .filter(iterm -> CreativeConstants.MATERIAL_DEFINE.equalsIgnoreCase(iterm.getField()) && iterm.getValue() != null)
                        .findFirst();
                if (!materialDefineVariable.isPresent()) {
                    return;
                }
                Object materialDefine = materialDefineVariable.get().getValue();
                List<MaterialFieldConfigDTO> materialFieldConfigList = parseConfig(JSONUtil.toJsonStr(materialDefine));
                if (CollUtil.isEmpty(materialFieldConfigList)) {
                    return;
                }
                verifyMaterialField(materialFieldConfigList);
                verifyMaterialFieldType(materialFieldConfigList);
                return;
            }
        }
    }

    /**
     * excel别名
     *
     * @param reader
     * @param materialConfigList
     */
    public static void addHeaderAlias(ExcelReader reader, List<MaterialFieldConfigDTO> materialConfigList) {
        for (MaterialFieldConfigDTO materialFieldConfig : materialConfigList) {
            reader.addHeaderAlias(materialFieldConfig.getDesc(), materialFieldConfig.getFieldName());
        }
    }

    public static List<Map<String, Object>> parseData(String materialListStr) {
        return JSONObject.parseObject(materialListStr, new TypeReference<List<Map<String, Object>>>() {
        });
    }


}
