package cn.iocoder.yudao.server.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.app.vo.request.action.ActionResponseReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.action.WorkflowStepReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.WorkflowConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.WorkflowStepWrapperReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableItemReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableReqVO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppStepTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.framework.common.api.dto.Option;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
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

    private static final String TARGET_URL = "jdbc:mysql://rm-uf665hm6p41e4tuk89o.mysql.rds.aliyuncs.com:3306/cn-pro-llmops?useSSL=false&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true";
    private static final String TARGET_USERNAME = "llmops";
    private static final String TARGET_PASSWORD = "Hellostarcloud2022";


    public static void main(String[] args) throws SQLException {

        // 1.链接源数据库
        Connection sourceConnection = DriverManager.getConnection(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
        sourceConnection.setAutoCommit(false);
        sourceConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        // 1.1 查询源数据库的数据
        ResultSet resultSet = sourceConnection
                .prepareStatement("select id, name, icon, topics, tags, image_urls, scenes, config, description, prompt_details from ss_template_info where id = 1011")
                .executeQuery();
        // 解析数据
        List<AppDO> appDOList = new LinkedList<>();
        System.out.println("================开始解析数据=====================");
        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            System.out.println(id);
            String name = resultSet.getString("name");
            String icon = resultSet.getString("icon");
            String topics = resultSet.getString("topics");
            String tags = resultSet.getString("tags");
            String imageUrls = resultSet.getString("image_urls");
            String scenes = resultSet.getString("scenes");
            String config = resultSet.getString("config");
            String description = resultSet.getString("description");
            String promptDetails = resultSet.getString("prompt_details");

            AppDO appDO = new AppDO();
            appDO.setUid(AppUtils.generateUid(AppConstants.APP_PREFIX));
            appDO.setName(name);
            appDO.setModel(AppModelEnum.COMPLETION.name());
            appDO.setType(AppTypeEnum.MYSELF.name());
            appDO.setSource(AppSourceEnum.WEB.name());
            appDO.setTags(AppUtils.join(AppUtils.split(tags)));
            appDO.setCategories(buildCategoryList(topics));
            appDO.setScenes("WEB_ADMIN,WEB_MARKET");
            appDO.setImages(buildImages(appDO.getCategories()));
            appDO.setIcon(buildAppIcon(appDO.getCategories()));
            appDO.setDescription(description);
            appDO.setPublishUid(null);
            appDO.setInstallUid(null);
            appDO.setDeleted(Boolean.FALSE);
            appDO.setCreator("1");
            appDO.setUpdater("1");
            appDO.setCreateTime(LocalDateTime.now());
            appDO.setUpdateTime(LocalDateTime.now());
            appDO.setLastPublish(null);
            appDO.setTenantId(1L);
            appDO.setExample(promptDetails);

            appDO.setConfig(buildConfig(config));

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
        PreparedStatement preparedStatement = targetConnection.prepareStatement("insert into llm_app (uid, name, model, type, source, tags, categories, scenes, images, icon, description, deleted, creator, updater, create_time, update_time, tenant_id, config, example) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
            preparedStatement.setBoolean(12, appDO.getDeleted());
            preparedStatement.setString(13, appDO.getCreator());
            preparedStatement.setString(14, appDO.getUpdater());
            preparedStatement.setTimestamp(15, Timestamp.valueOf(appDO.getCreateTime()));
            preparedStatement.setTimestamp(16, Timestamp.valueOf(appDO.getUpdateTime()));
            preparedStatement.setLong(17, appDO.getTenantId());
            preparedStatement.setString(18, appDO.getConfig());
            preparedStatement.setString(19, appDO.getExample());
            preparedStatement.addBatch();
        }

        preparedStatement.executeBatch();
        targetConnection.commit();
        preparedStatement.close();
        targetConnection.close();
        System.out.println("================数据插入完成=====================");


    }

    public static String buildCategoryList(String categories) {

        if (StringUtils.isNotBlank(categories)) {
            categories = categories.trim();
            if ("Ads".equals(categories)) {
                categories = "ADVERTISING";
            }
            if ("Amazon,Listing创建和优化".equals(categories)) {
                categories = "AMAZON";
            }
            if ("Amazon,产品分析和推广".equals(categories)) {
                categories = "AMAZON";
            }
            if ("Amazon,店铺管理和售后".equals(categories)) {
                categories = "AMAZON";
            }
            if ("Article,Blog".equals(categories)) {
                categories = "SEO_WRITING";
            }
            if ("Blog".equals(categories)) {
                categories = "SEO_WRITING";
            }
            if ("Business".equals(categories)) {
                categories = "SEO_WRITING";
            }
            if ("Email".equals(categories)) {
                categories = "EMAIL";
            }
            if ("Fun".equals(categories)) {
                categories = "FUN";
            }
            if ("Image".equals(categories)) {
                categories = "IMAGE";
            }
            if ("Listing创建和优化".equals(categories)) {
                categories = "AMAZON";
            }
            if ("Listing创建和优化,Amazon".equals(categories)) {
                categories = "AMAZON";
            }
            if ("Listing创建和优化,产品分析和推广".equals(categories)) {
                categories = "AMAZON";
            }
            if ("Listing创建和优化,店铺管理和售后,产品分析和推广".equals(categories)) {
                categories = "AMAZON";
            }
            if ("Marketing".equals(categories)) {
                categories = "SEO_WRITING";
            }
            if ("Resume".equals(categories)) {
                categories = "RESUME";
            }
            if ("Role Play".equals(categories)) {
                categories = "SEO_WRITING";
            }
            if ("Other".equals(categories)) {
                categories = "SEO_WRITING";
            }
            if ("Social Media".equals(categories)) {
                categories = "SOCIAL_MEDIA";
            }
            if ("Social Media,社媒文案".equals(categories)) {
                categories = "SOCIAL_MEDIA";
            }
            if ("Website".equals(categories)) {
                categories = "WEBSITE";
            }
            if ("Writing".equals(categories)) {
                categories = "SEO_WRITING";
            }
            if ("中文写作".equals(categories)) {
                categories = "DAILY_USE";
            }
            if ("中文写作,有趣好玩".equals(categories)) {
                categories = "DAILY_USE";
            }
            if ("产品分析和推广".equals(categories)) {
                categories = "AMAZON";
            }
            if ("产品分析和推广,Amazon".equals(categories)) {
                categories = "AMAZON";
            }
            if ("有趣好玩".equals(categories)) {
                categories = "FUN";
            }
            if ("独立站".equals(categories)) {
                categories = "WEBSITE";
            }
            if ("社媒文案".equals(categories)) {
                categories = "SOCIAL_MEDIA";
            }
            if ("邮件营销".equals(categories)) {
                categories = "EMAIL";
            }
            if ("Custom".equals(categories)) {
                categories = "SEO_WRITING";
            }
            return categories;
        }
        return "SEO_WRITING";
    }

    public static String buildImages(String category) {
        if ("RESUME".equals(category)) {
            return "https://download.hotsalecloud.com/mofaai/images/category/resume.jpg";
        }
        if ("FUN".equals(category)) {
            return "https://download.hotsalecloud.com/mofaai/images/category/fun.jpg";
        }
        if ("IMAGE".equals(category)) {
            return "https://download.hotsalecloud.com/mofaai/images/category/image.jpg";
        }
        if ("DAILY_USE".equals(category)) {
            return "https://download.hotsalecloud.com/mofaai/images/category/daily-use.jpg";
        }
        if ("SEO_WRITING".equals(category)) {
            return "https://download.hotsalecloud.com/mofaai/images/category/seo.jpg";
        }
        if ("OFFICE_ASSISTANT".equals(category)) {
            return "https://download.hotsalecloud.com/mofaai/images/category/office-assistant.jpg";
        }
        if ("ADVERTISING".equals(category)) {
            return "https://download.hotsalecloud.com/mofaai/images/category/advertising.jpg";
        }
        if ("EMAIL".equals(category)) {
            return "https://download.hotsalecloud.com/mofaai/images/category/email.jpg";
        }
        if ("SOCIAL_MEDIA".equals(category)) {
            return "https://download.hotsalecloud.com/mofaai/images/category/social-media.jpg";
        }
        if ("WEBSITE".equals(category)) {
            return "https://download.hotsalecloud.com/mofaai/images/category/website.jpg";
        }
        if ("AMAZON".equals(category)) {
            return "https://download.hotsalecloud.com/mofaai/images/category/amazon.jpg";
        }
        return "https://download.hotsalecloud.com/mofaai/images/category/seo.jpg";
    }

    public static String buildAppIcon(String category) {
        if ("RESUME".equals(category)) {
            return "resume";
        }
        if ("FUN".equals(category)) {
            return "fun";
        }
        if ("IMAGE".equals(category)) {
            return "image";
        }
        if ("DAILY_USE".equals(category)) {
            return "daily-use";
        }
        if ("SEO_WRITING".equals(category)) {
            return "seo";
        }
        if ("OFFICE_ASSISTANT".equals(category)) {
            return "office-assistant";
        }
        if ("ADVERTISING".equals(category)) {
            return "advertising";
        }
        if ("EMAIL".equals(category)) {
            return "email";
        }
        if ("SOCIAL_MEDIA".equals(category)) {
            return "social-media";
        }
        if ("WEBSITE".equals(category)) {
            return "website";
        }
        if ("AMAZON".equals(category)) {
            return "amazon";
        }
        return "seo";
    }


    public static String buildConfig(String config) {
        JSONObject jsonObject = JSONObject.parseObject(config);

        WorkflowConfigReqVO workflowConfig = new WorkflowConfigReqVO();

        workflowConfig.setSteps(buildStepWrapperList(jsonObject.getJSONArray("steps"), jsonObject.getJSONArray("variables")));
        workflowConfig.setVariable(null);
        return JSONObject.toJSONString(workflowConfig);
    }

    public static List<WorkflowStepWrapperReqVO> buildStepWrapperList(JSONArray stepWrappers, JSONArray variables) {
        List<WorkflowStepWrapperReqVO> stepWrapperList = new ArrayList<>();
        if (stepWrappers != null && stepWrappers.size() > 0) {
            for (int i = 0; i < stepWrappers.size(); i++) {

                JSONObject object = stepWrappers.getJSONObject(i);
                WorkflowStepWrapperReqVO stepWrapperDTO = buildStepWrapper(object);

                if (i == 0 || variables.size() != 0) {
                    VariableReqVO variable = stepWrapperDTO.getVariable();
                    if (variable == null) {
                        variable = new VariableReqVO();
                    }
                    List<VariableItemReqVO> variableVariables = variable.getVariables();
                    if (variableVariables == null) {
                        variableVariables = new ArrayList<>();
                    }

                    VariableReqVO variableReqVO = buildVariable(variables);
                    List<VariableItemReqVO> variableReqVOVariables = variableReqVO.getVariables();

                    List<VariableItemReqVO> collect = variableReqVOVariables.stream()
                            .filter(item -> !"max_tokens".equals(item.getField()) && !"temperature".equals(item.getField()))
                            .collect(Collectors.toList());

                    variableVariables.addAll(CollectionUtil.emptyIfNull(collect));
                    variable.setVariables(variableVariables);
                    stepWrapperDTO.setVariable(variable);
                }
                stepWrapperList.add(stepWrapperDTO);
            }
        }
        return stepWrapperList;
    }

    public static WorkflowStepWrapperReqVO buildStepWrapper(JSONObject object) {
        WorkflowStepWrapperReqVO stepWrapper = new WorkflowStepWrapperReqVO();
        stepWrapper.setName(object.getString("label"));
        stepWrapper.setField(object.getString("field"));
        stepWrapper.setButtonLabel(object.getString("buttonLabel"));
        // 步骤
        stepWrapper.setFlowStep(buildStep(object.getJSONObject("stepModule")));
        // 变量
        stepWrapper.setVariable(buildVariable(object.getJSONArray("variables")));
        stepWrapper.setDescription(object.getString("desc"));
        return stepWrapper;
    }

    public static WorkflowStepReqVO buildStep(JSONObject object) {
        WorkflowStepReqVO workflowStep = new WorkflowStepReqVO();
        workflowStep.setName(object.getString("name"));
        workflowStep.setType(AppStepTypeEnum.WORKFLOW.name());
        workflowStep.setIsAuto(object.getBoolean("isAuto"));
        workflowStep.setIsCanEditStep(object.getBoolean("isCanAddStep"));
        workflowStep.setVersion(AppConstants.DEFAULT_VERSION);
        workflowStep.setTags(CollectionUtil.emptyIfNull(object.getJSONArray("tags")).stream().map(Object::toString).collect(Collectors.toList()));
        workflowStep.setScenes(Arrays.asList("WEB_ADMIN", "WEB_MARKET"));
        workflowStep.setVariable(buildVariable(object.getJSONArray("variables")));
        workflowStep.setIcon(buildIcon(object.getString("icon")));
        workflowStep.setHandler(buildHandler(workflowStep.getIcon()));
        workflowStep.setResponse(buildResponse(object.getJSONObject("response")));
        workflowStep.setDescription(object.getString("desc"));
        return workflowStep;
    }

    public static ActionResponseReqVO buildResponse(JSONObject object) {
        ActionResponseReqVO response = new ActionResponseReqVO();
        response.setSuccess(object.getInteger("status") == 1);
        response.setErrorCode(object.getString("errorCode"));
        response.setMessage(object.getString("message"));
        response.setType(getResponseType(object.getString("type")));
        response.setStyle(getResponseStyle(object.getString("style")));
        Boolean isShow = object.getBoolean("isShow");
        if (isShow == null) {
            isShow = false;
        }
        response.setIsShow(isShow);

        JSONObject processParams = object.getJSONObject("processParams");
        if (processParams != null) {
            JSONObject request = processParams.getJSONObject("request");
            JSONObject response1 = processParams.getJSONObject("response");
            JSONObject usage = response1.getJSONObject("usage");
            JSONObject modelPrice = processParams.getJSONObject("modelPrice");
            JSONArray messages = request.getJSONArray("messages");
            if (CollectionUtil.isNotEmpty(messages)) {
                JSONObject message = messages.getJSONObject(0);
                if (message != null) {
                    response.setMessage(message.getString("content"));
                }
            }
            Integer n = request.getInteger("n");
            if (n == null) {
                n = 1;
            }
            if (n == 1) {
                String value = object.getString("value");
                if (value != null) {
                    response.setAnswer(value);
                }
            } else if (n > 1) {
                JSONArray value = request.getJSONArray("value");
                if (CollectionUtil.isNotEmpty(value)) {
                    response.setAnswer(JSON.toJSONString(value));
                }
            }

            response.setMessageTokens(response1.getLong("promptTokens"));
            response.setAnswerTokens(response1.getLong("completionTokens"));
            BigDecimal price = modelPrice.getBigDecimal("price");
            if (price == null) {
                price = BigDecimal.ZERO;
            }

            response.setMessageUnitPrice(price);
            response.setAnswerUnitPrice(price);
            if (usage != null) {
                Long promptTokens = usage.getLong("promptTokens");
                Long completionTokens = usage.getLong("completionTokens");

                System.out.println(price);
                System.out.println(promptTokens);
                System.out.println(completionTokens);
                System.out.println();
                if (promptTokens == null) {
                    promptTokens = usage.getLong("prompt_tokens");
                    if (promptTokens == null) {
                        promptTokens = 0L;
                    }
                }
                if (completionTokens == null) {
                    completionTokens = usage.getLong("completion_tokens");
                    if (completionTokens == null) {
                        completionTokens = 0L;
                    }
                }
                response.setTotalPrice(price.multiply(new BigDecimal(promptTokens.toString()).add(new BigDecimal(completionTokens.toString()))));
                response.setTotalTokens(promptTokens + completionTokens);
            }

        }


        return response;
    }

    public static VariableReqVO buildVariable(JSONArray variables) {
        VariableReqVO variableRequest = new VariableReqVO();
        List<VariableItemReqVO> variableList = new ArrayList<>();
        if (variables != null && variables.size() > 0) {
            for (int i = 0; i < variables.size(); i++) {
                JSONObject object = variables.getJSONObject(i);
                VariableItemReqVO variableItemDTO = buildVariableItem(object);
                variableList.add(variableItemDTO);
            }
        }
        variableRequest.setVariables(variableList);
        return variableRequest;
    }

    public static VariableItemReqVO buildVariableItem(JSONObject object) {
        VariableItemReqVO variableItem = new VariableItemReqVO();
        variableItem.setLabel(object.getString("label"));
        variableItem.setField(object.getString("field"));
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setGroup(getVariableGroup(object.getString("group")));
        variableItem.setStyle(getVariableStyle(object.getString("style")));
        variableItem.setOrder(object.getInteger("order"));
        variableItem.setDefaultValue(object.get("default"));
        variableItem.setValue(object.get("value"));
        Boolean isShow = object.getBoolean("is_show");
        if (isShow == null) {
            isShow = false;
        }
        variableItem.setIsShow(isShow);
        Boolean isPoint = object.getBoolean("is_point");
        if (isPoint == null) {
            isPoint = false;
        }
        variableItem.setIsPoint(isPoint);
        variableItem.setDescription(object.getString("desc"));
        if (AppVariableStyleEnum.SELECT.name().equals(variableItem.getStyle())) {
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
                variableItem.setOptions(optionList);
            }
        }
        return variableItem;
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

    public static String buildHandler(String icon) {
        if ("open-ai".equals(icon)) {
            return "OpenAIChatActionHandler";
        }
        return "OpenAIChatActionHandler";
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
