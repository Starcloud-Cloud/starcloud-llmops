package com.starcloud.ops.business.app;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.app.dto.action.ActionResponseDTO;
import com.starcloud.ops.business.app.api.app.dto.action.WorkflowStepDTO;
import com.starcloud.ops.business.app.api.app.dto.config.WorkStepStepWrapperDTO;
import com.starcloud.ops.business.app.api.app.dto.config.WorkflowConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppStepSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppStepTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.framework.common.api.dto.Option;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 将 mdc 中的数据迁移到 llm 中
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public class AppDataMigration {

    private static final String SOURCE_URL = "jdbc:mysql://spider-db-test.caajcixnemh0.us-east-1.rds.amazonaws.com:3306/ry-vue?useSSL=false&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true";
    private static final String SOURCE_USERNAME = "dev";
    private static final String SOURCE_PASSWORD = "devseastar2002";

    private static final String TARGET_URL = "jdbc:mysql://rm-uf665hm6p41e4tuk89o.mysql.rds.aliyuncs.com:3306/ruoyi-vue-pro?useSSL=false&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true";
    private static final String TARGET_USERNAME = "starcloud_dev";
    private static final String TARGET_PASSWORD = "Hellostarcloud2022";


    public static void main(String[] args) throws SQLException {

        // 1.链接源数据库
        Connection sourceConnection = DriverManager.getConnection(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
        sourceConnection.setAutoCommit(false);
        sourceConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        // 1.1 查询源数据库的数据
        ResultSet resultSet = sourceConnection
                .prepareStatement("select name, icon, topics, tags, image_urls, scenes, config, description from ss_template_info")
                .executeQuery();
        // 解析数据
        List<AppDO> appDOList = new LinkedList<>();
        System.out.println("================开始解析数据=====================");
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            String icon = resultSet.getString("icon");
            String topics = resultSet.getString("topics");
            String tags = resultSet.getString("tags");
            String imageUrls = resultSet.getString("image_urls");
            String scenes = resultSet.getString("scenes");
            String config = resultSet.getString("config");
            String description = resultSet.getString("description");
            JSONObject jsonConfig = JSONObject.parseObject(config);

            AppDO appDO = new AppDO();
            appDO.setUid(IdUtil.fastSimpleUUID());
            appDO.setName(name);
            appDO.setModel(AppModelEnum.COMPLETION.name());
            appDO.setType(AppTypeEnum.MYSELF.name());
            appDO.setSource(AppSourceEnum.WEB.name());
            appDO.setTags(AppUtils.join(AppUtils.split(tags)));
            appDO.setCategories(AppUtils.join(AppUtils.split(topics)));
            appDO.setScenes("WEB_ADMIN,WEB_MARKET");
            appDO.setImages(null);
            appDO.setIcon(icon);
            appDO.setDescription(description);
//            appDO.setStatus(StateEnum.ENABLE.getCode());
            appDO.setDeleted(Boolean.FALSE);
            appDO.setCreator("1");
            appDO.setUpdater("1");
            appDO.setCreateTime(LocalDateTime.now());
            appDO.setUpdateTime(LocalDateTime.now());
            appDO.setTenantId(1L);

            WorkflowConfigDTO appConfigDTO = new WorkflowConfigDTO();
            List<WorkStepStepWrapperDTO> steps = buildStepWrapperList(jsonConfig.getJSONArray("steps"));
            appConfigDTO.setSteps(steps);
//            appConfigDTO.setVariables(buildVariableList(jsonConfig.getJSONArray("variables")));
            appDO.setConfig(JSONObject.toJSONString(appConfigDTO));
//            appDO.setStepIcons(buildSteIcons(steps));

            appDOList.add(appDO);
        }

        System.out.println("================数据解析完成=====================");
        // 1.2 关闭链接
        resultSet.close();
        sourceConnection.close();

        System.out.println("================开始插入数据=====================");
        // 2.链接目标数据库
        Connection targetConnection = DriverManager.getConnection(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD);
        targetConnection.setAutoCommit(false);
        targetConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        // 2.1 批量插入数据
        PreparedStatement preparedStatement = targetConnection.prepareStatement("insert into llm_app (uid, name, model, type, source, tags, categories, scenes, images, icon, description, status, deleted, creator, updater, create_time, update_time, tenant_id, config, step_icons) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        for (AppDO appDO : appDOList) {
            preparedStatement.setString(1, appDO.getUid());
            preparedStatement.setString(2, appDO.getName());
            preparedStatement.setString(3, appDO.getModel());
            preparedStatement.setString(4, appDO.getType());
            preparedStatement.setString(5, appDO.getSource());
            preparedStatement.setString(6, appDO.getTags());
            preparedStatement.setString(7, appDO.getCategories());
            preparedStatement.setString(8, appDO.getScenes());
            preparedStatement.setString(9, appDO.getImages());
            preparedStatement.setString(10, appDO.getIcon());
            preparedStatement.setString(11, appDO.getDescription());
//            preparedStatement.setInt(12, appDO.getStatus());
            preparedStatement.setBoolean(13, appDO.getDeleted());
            preparedStatement.setString(14, appDO.getCreator());
            preparedStatement.setString(15, appDO.getUpdater());
            preparedStatement.setTimestamp(16, Timestamp.valueOf(appDO.getCreateTime()));
            preparedStatement.setTimestamp(17, Timestamp.valueOf(appDO.getUpdateTime()));
            preparedStatement.setLong(18, appDO.getTenantId());
            preparedStatement.setString(19, appDO.getConfig());
//            preparedStatement.setString(20, appDO.getStepIcons());
            preparedStatement.addBatch();
        }

        preparedStatement.executeBatch();
        targetConnection.commit();
        preparedStatement.close();
        targetConnection.close();
        System.out.println("================数据插入完成=====================");


    }

    public static String buildSteIcons(List<WorkStepStepWrapperDTO> stepWrappers) {
        return "";
    }

    public static List<WorkStepStepWrapperDTO> buildStepWrapperList(JSONArray stepWrappers) {
        List<WorkStepStepWrapperDTO> stepWrapperList = new ArrayList<>();
        if (stepWrappers != null && stepWrappers.size() > 0) {
            for (int i = 0; i < stepWrappers.size(); i++) {
                JSONObject object = stepWrappers.getJSONObject(i);
                WorkStepStepWrapperDTO stepWrapperDTO = buildStepWrapper(object);
                stepWrapperList.add(stepWrapperDTO);
            }
        }
        return stepWrapperList;
    }

    public static WorkStepStepWrapperDTO buildStepWrapper(JSONObject object) {
        WorkStepStepWrapperDTO stepWrapperDTO = new WorkStepStepWrapperDTO();
        stepWrapperDTO.setName(object.getString("label"));
        stepWrapperDTO.setField(object.getString("field"));
        stepWrapperDTO.setButtonLabel(object.getString("buttonLabel"));
        // 步骤
       // stepWrapperDTO.setStep(buildStep(object.getJSONObject("stepModule")));
        // 变量
       // stepWrapperDTO.setVariables(buildVariableList(object.getJSONArray("variables")));
        stepWrapperDTO.setDescription(object.getString("desc"));
        return stepWrapperDTO;
    }

    public static WorkflowStepDTO buildStep(JSONObject object) {
        WorkflowStepDTO workflowStepDTO = new WorkflowStepDTO();
        workflowStepDTO.setName(object.getString("name"));
        workflowStepDTO.setType(getStepType(object.getString("type")));
       // workflowStepDTO.setSource(getStepSource(object.getString("source")));
        workflowStepDTO.setIsAuto(object.getBoolean("isAuto"));
        workflowStepDTO.setIsCanEditStep(object.getBoolean("isCanAddStep"));
        workflowStepDTO.setVersion(AppConstants.DEFAULT_VERSION);
        workflowStepDTO.setTags(CollectionUtil.emptyIfNull(object.getJSONArray("tags")).stream().map(Object::toString).collect(Collectors.toList()));
        workflowStepDTO.setScenes(Arrays.asList("WEB_ADMIN", "WEB_MARKET"));
        //workflowStepDTO.setVariables(buildVariableList(object.getJSONArray("variables")));
        workflowStepDTO.setIcon(buildIcon(object.getString("icon")));
        workflowStepDTO.setResponse(buildResponse(object.getJSONObject("response")));
        workflowStepDTO.setDescription(object.getString("desc"));
        return workflowStepDTO;
    }

    public static String buildIcon(String icon) {
        if (StringUtils.isBlank(icon)) {
            return "open-ai";
        }
        String[] split = icon.split("/");
        if (split.length == 0) {
            return "open-ai";
        }
        icon = split[split.length - 1];

        String[] split1 = icon.split("\\.");
        if (split1.length == 0) {
            return "open-ai";
        }
        icon = split1[0];

        if ("openai".equalsIgnoreCase(icon)) {
            return "open-ai";
        }

        if ("post".equals(icon)) {
            return "post";
        }

        if ("ppt".equals(icon)) {
            return "ppt";
        }

        if ("report".equals(icon)) {
            return "report";
        }

        if ("search_product".equals(icon)) {
            return "search-product";
        }

        if ("stability".equals(icon)) {
            return "stability";
        }

        if ("url".equals(icon)) {
            return "url";
        }


        return icon.trim();
    }

    public static ActionResponseDTO buildResponse(JSONObject object) {
        ActionResponseDTO response = new ActionResponseDTO();
        response.setSuccess(object.getInteger("status") == 1);
        response.setErrorCode(object.getString("errorCode"));
        response.setMessage(object.getString("message"));
        response.setType(getResponseType(object.getString("type")));
        response.setStyle(getResponseStyle(object.getString("style")));
        response.setIsShow(object.getBoolean("isShow"));
//        response.setData(object.get("value"));
//
//        JSONObject processParams = object.getJSONObject("processParams");
//        if (processParams != null) {
//            ProcessRequest processRequest = new ProcessRequest();
//            processRequest.setMark(processParams.getString("mark"));
//            processRequest.setSuccess(processParams.getBoolean("status"));
//            processRequest.setRequest(processParams.get("request"));
//            processRequest.setResponse(processParams.get("response"));
//
//            JSONObject modelPrice = processParams.getJSONObject("modelPrice");
//            if (modelPrice != null) {
//                ModelPrice mp = new ModelPrice();
//                mp.setModel(modelPrice.getString("model"));
//                mp.setPrice(modelPrice.getBigDecimal("price"));
//                mp.setTotalPrice(modelPrice.getBigDecimal("total_price"));
//                mp.setTokenUsage(modelPrice.getInteger("token_usage"));
//                mp.setUserId(1L);
//                mp.setUserName("admin");
//                mp.setUserType("USER");
//                processRequest.setModelPrice(mp);
//            }
//            response.setProcessRequest(processRequest);
//        }


        return response;
    }

    public static List<VariableItemDTO> buildVariableList(JSONArray variables) {
        List<VariableItemDTO> variableList = new ArrayList<>();
        if (variables != null && variables.size() > 0) {
            for (int i = 0; i < variables.size(); i++) {
                JSONObject object = variables.getJSONObject(i);
                VariableItemDTO variableItemDTO = buildVariable(object);
                variableList.add(variableItemDTO);
            }
        }
        return variableList;
    }

    public static VariableItemDTO buildVariable(JSONObject object) {
        VariableItemDTO variableItemDTO = new VariableItemDTO();
        variableItemDTO.setLabel(object.getString("label"));
        variableItemDTO.setField(object.getString("field"));
        variableItemDTO.setDefaultValue(object.get("default"));
        variableItemDTO.setValue(object.get("value"));
        variableItemDTO.setType(AppVariableTypeEnum.TEXT.name());
        variableItemDTO.setGroup(getVariableGroup(object.getString("group")));
        variableItemDTO.setStyle(getVariableStyle(object.getString("style")));
        variableItemDTO.setIsShow(object.getBoolean("is_show"));
        variableItemDTO.setIsPoint(object.getBoolean("is_point"));
        variableItemDTO.setOrder(object.getInteger("order"));
        variableItemDTO.setDescription(object.getString("desc"));
        if (AppVariableStyleEnum.SELECT.name().equals(variableItemDTO.getStyle())) {
            JSONArray options = object.getJSONArray("options");
            if (CollectionUtil.isNotEmpty(options)) {
                List<Option> optionList = new ArrayList<>();
                for (int i = 0; i < options.size(); i++) {
                    JSONObject option = options.getJSONObject(i);
                    Option optionDTO = new Option();
                    optionDTO.setLabel(option.getString("label"));
                    optionDTO.setValue(option.get("value"));
                    optionList.add(optionDTO);
                }
                variableItemDTO.setOptions(optionList);
            }
        }

        return variableItemDTO;
    }

    public static List<String> buildSceneList(JSONArray scenes) {
        List<String> sceneList = new ArrayList<>();
        if (scenes != null && scenes.size() > 0) {
            for (int i = 0; i < scenes.size(); i++) {
                JSONObject scene = scenes.getJSONObject(i);
                sceneList.add(scene.getString("key"));
            }
        }
        return sceneList;
    }

    public static String getStepType(String type) {
        if (StringUtils.isBlank(type)) {
            return AppStepTypeEnum.COMMON.name();
        }
        if ("common".equals(type)) {
            return AppStepTypeEnum.COMMON.name();
        }
        if ("adapter".equals(type)) {
            return AppStepTypeEnum.ADAPTER.name();
        }
        return AppStepTypeEnum.COMMON.name();
    }

    public static String getStepSource(String source) {
        if (StringUtils.isBlank(source)) {
            return AppStepSourceEnum.NATIVE.name();
        }
        if ("native".equals(source)) {
            return AppStepSourceEnum.NATIVE.name();
        }
        if ("extend".equals(source)) {
            return AppStepSourceEnum.EXTEND.name();
        }
        return AppStepSourceEnum.NATIVE.name();
    }

    public static String getResponseType(String type) {
        if (StringUtils.isBlank(type)) {
            return AppStepResponseTypeEnum.TEXT.name();
        }
        if ("text".equals(type)) {
            return AppStepResponseTypeEnum.TEXT.name();
        }
        if ("array".equals(type)) {
            return AppStepResponseTypeEnum.ARRAY.name();
        }
        if ("redirect".equals(type)) {
            return AppStepResponseTypeEnum.REDIRECT.name();
        }
        if ("copy".equals(type)) {
            return AppStepResponseTypeEnum.COPY.name();
        }
        if ("dialog".equals(type)) {
            return AppStepResponseTypeEnum.DIALOG.name();
        }
        return AppStepResponseTypeEnum.TEXT.name();
    }

    public static String getResponseStyle(String style) {
        if (StringUtils.isBlank(style)) {
            return AppStepResponseStyleEnum.TEXTAREA.name();
        }
        if ("text".equals(style)) {
            return AppStepResponseStyleEnum.TEXTAREA.name();
        }
        if ("input".equals(style)) {
            return AppStepResponseStyleEnum.INPUT.name();
        }
        if ("img".equals(style)) {
            return AppStepResponseStyleEnum.IMAGE.name();
        }
        if ("base64".equals(style)) {
            return AppStepResponseStyleEnum.BASE64.name();
        }
        if ("button".equals(style)) {
            return AppStepResponseStyleEnum.BUTTON.name();
        }
        if ("product".equals(style)) {
            return AppStepResponseStyleEnum.PRODUCT.name();
        }
        return AppStepResponseStyleEnum.TEXTAREA.name();
    }

    public static String getVariableStyle(String style) {
        if (StringUtils.isBlank(style)) {
            return AppVariableStyleEnum.TEXTAREA.name();
        }
        if ("text".equals(style)) {
            return AppVariableStyleEnum.TEXTAREA.name();
        }
        if ("input".equals(style)) {
            return AppVariableStyleEnum.INPUT.name();
        }
        if ("select".equals(style)) {
            return AppVariableStyleEnum.SELECT.name();
        }
        return AppVariableStyleEnum.TEXTAREA.name();
    }

    public static String getVariableGroup(String group) {
        if (StringUtils.isBlank(group)) {
            return AppVariableGroupEnum.PARAMS.name();
        }
        if ("sys".equals(group)) {
            return AppVariableGroupEnum.SYSTEM.name();
        }
        if ("params".equals(group)) {
            return AppVariableGroupEnum.PARAMS.name();
        }
        if ("model".equals(group)) {
            return AppVariableGroupEnum.MODEL.name();
        }
        return AppVariableGroupEnum.PARAMS.name();
    }

}
