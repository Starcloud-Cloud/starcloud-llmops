package com.starcloud.ops.business.user.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.app.vo.request.action.ActionResponseReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.action.WorkflowStepReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.WorkflowConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.WorkflowStepWrapperReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableItemReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableReqVO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.*;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsRecordDO;
import com.starcloud.ops.business.user.enums.level.AdminUserLevelBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsStatusEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 旧数据迁移
 *
 * @author AlanCusack
 */
public class AppDataMigration {

    private static final String SOURCE_URL = "jdbc:mysql://rm-uf665hm6p41e4tuk89o.mysql.rds.aliyuncs.com:3306/cn-pro-llmops?useSSL=false&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true";
    private static final String SOURCE_USERNAME = "llmops";
    private static final String SOURCE_PASSWORD = "Hellostarcloud2022";

    private static final String TARGET_URL = "jdbc:mysql://rm-uf665hm6p41e4tuk89o.mysql.rds.aliyuncs.com:3306/cn-pro-llmops?useSSL=false&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true";
    private static final String TARGET_USERNAME = "llmops";
    private static final String TARGET_PASSWORD = "Hellostarcloud2022";
    private static final List<String> levelList = Arrays.asList("5", "6", "7", "8", "48", "49", "55", "53", "54");


    public static void main(String[] args) throws SQLException {


        // 1.链接源数据库
        Connection sourceConnection = DriverManager.getConnection(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
        sourceConnection.setAutoCommit(false);
        sourceConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        // 1.1 查询源数据库的数据
        ResultSet resultSet = sourceConnection
                .prepareStatement("SELECT * FROM llm_user_benefits limit 100 offset 7100;")
                .executeQuery();
        // 解析数据
        List<AdminUserRightsDO> rightsDOS = new LinkedList<>();
        List<AdminUserLevelDO> userLevelDOS = new LinkedList<>();
        List<AdminUserRightsRecordDO> rightsRecordDOS = new LinkedList<>();
        System.out.println("================开始解析数据=====================");
        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            String userId = resultSet.getString("user_id");
            String strategyId = resultSet.getString("strategy_id");
            String imageRemaining = resultSet.getString("image_remaining");
            String computationalPowerRemaining = resultSet.getString("computational_power_remaining");
            String imageCountInit = resultSet.getString("image_count_init");
            String computationalPowerInit = resultSet.getString("computational_power_init");
            Timestamp effectiveTime = resultSet.getTimestamp("effective_time");
            Timestamp expirationTime = resultSet.getTimestamp("expiration_time");
            String enabled = resultSet.getString("enabled");
            String deleted = resultSet.getString("deleted");
            Timestamp createTime = resultSet.getTimestamp("create_time");
            Timestamp updateTime = resultSet.getTimestamp("update_time");

            //  1.0 设置用户权益数据
            AdminUserRightsDO rightsDO = new AdminUserRightsDO();
            rightsDO.setId(id);
            rightsDO.setUserId(Long.valueOf(userId));
            rightsDO.setBizId(userId);
            rightsDO.setBizType(buildRightsBizType(strategyId));
            rightsDO.setTitle(buildRightsTitle(strategyId));
            rightsDO.setDescription(buildRightsDesc(strategyId));
            rightsDO.setMagicBean(Integer.valueOf(computationalPowerRemaining));
            rightsDO.setMagicImage(Integer.valueOf(imageRemaining));
            rightsDO.setMagicBeanInit(Integer.valueOf(computationalPowerInit));
            rightsDO.setMagicImageInit(Integer.valueOf(imageCountInit));
            rightsDO.setUserLevelId(null);

            rightsDO.setValidStartTime(LocalDateTimeUtil.of(effectiveTime));
            rightsDO.setValidEndTime(LocalDateTimeUtil.of(expirationTime));
            rightsDO.setStatus(AdminUserRightsStatusEnum.NORMAL.getType());
            rightsDO.setDeleted(buildDelete(deleted));
            rightsDO.setCreator(userId);
            rightsDO.setUpdater(userId);
            rightsDO.setCreateTime(LocalDateTimeUtil.of(createTime));
            rightsDO.setUpdateTime(LocalDateTimeUtil.of(updateTime));


            Long rightId = 0L;
            // 增加用户权益
            if (rightsDO != null) {

                System.out.println("================开始插入权益数据=====================");
                // 2.链接目标数据库
                Connection targetConnection = DriverManager.getConnection(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD);
                targetConnection.setAutoCommit(false);
                targetConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                // 2.1 批量插入数据
                PreparedStatement preparedStatementRights = targetConnection.prepareStatement(
                        "INSERT INTO system_user_rights (`user_id`, `biz_id`, `biz_type`, `title`, `description`, `magic_bean`, `magic_image`, `magic_bean_init`, `magic_image_init`, `user_level_id`, `valid_start_time`, `valid_end_time`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);

                preparedStatementRights.setLong(1, rightsDO.getUserId());
                preparedStatementRights.setString(2, rightsDO.getBizId());
                preparedStatementRights.setInt(3, rightsDO.getBizType());
                preparedStatementRights.setString(4, rightsDO.getTitle());
                preparedStatementRights.setString(5, rightsDO.getDescription());
                preparedStatementRights.setInt(6, rightsDO.getMagicBean());
                preparedStatementRights.setInt(7, rightsDO.getMagicImage());
                preparedStatementRights.setInt(8, rightsDO.getMagicBeanInit());
                preparedStatementRights.setInt(9, rightsDO.getMagicImageInit());
                preparedStatementRights.setLong(10, -1);
                preparedStatementRights.setTimestamp(11, Timestamp.valueOf(rightsDO.getValidStartTime()));
                preparedStatementRights.setTimestamp(12, Timestamp.valueOf(rightsDO.getValidEndTime()));
                preparedStatementRights.setInt(13, AdminUserRightsStatusEnum.NORMAL.getType());
                preparedStatementRights.setString(14, rightsDO.getCreator());
                preparedStatementRights.setTimestamp(15, Timestamp.valueOf(rightsDO.getCreateTime()));
                preparedStatementRights.setString(16, rightsDO.getUpdater());
                preparedStatementRights.setTimestamp(17, Timestamp.valueOf(rightsDO.getUpdateTime()));
                preparedStatementRights.setBoolean(18, rightsDO.getDeleted());
                preparedStatementRights.setString(19, "2");

                // 权益日志插入
                preparedStatementRights.executeUpdate();
                targetConnection.commit();
                ResultSet rightsGeneratedKeys = preparedStatementRights.getGeneratedKeys();
                if (rightsGeneratedKeys.next()) {
                    Long lastInsertId = rightsGeneratedKeys.getLong(1);
                    System.out.println("Last inserted ID: " + lastInsertId);
                    rightId = lastInsertId;
                }

                preparedStatementRights.close();
                targetConnection.close();
                System.out.println("================权益数据插入完成=====================");


            }


            // 2.0 判断是否增加用户等级
            if (levelList.contains(strategyId)) {
                // 2.1 设置用户等级逻辑
                AdminUserLevelDO levelDO = new AdminUserLevelDO();
                levelDO.setUserId(Long.valueOf(userId));
                levelDO.setBizId("0");
                levelDO.setBizType(AdminUserLevelBizTypeEnum.ORDER_GIVE.getType());
                levelDO.setLevelId(buildUserLevelId(strategyId));
                levelDO.setLevelName(buildUserLevelName(strategyId));
                levelDO.setStatus(AdminUserRightsStatusEnum.NORMAL.getType());
                levelDO.setValidStartTime(effectiveTime.toLocalDateTime());
                levelDO.setValidEndTime(expirationTime.toLocalDateTime());
                levelDO.setRemark("数据迁移");
                levelDO.setDescription("数据迁移");

                levelDO.setDeleted(buildDelete(deleted));
                levelDO.setCreator(userId);
                levelDO.setUpdater(userId);
                levelDO.setCreateTime(LocalDateTimeUtil.of(createTime));
                levelDO.setUpdateTime(LocalDateTimeUtil.of(updateTime));

                System.out.println("================开始插入用户等级数据=====================");
                // 2.链接目标数据库
                Connection targetConnection = DriverManager.getConnection(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD);
                targetConnection.setAutoCommit(false);
                targetConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                // 2.1 批量插入数据
                PreparedStatement userLevelStatement = targetConnection.prepareStatement(
                        "INSERT INTO system_user_level ( `user_id`, `level_id`, `level_name`, `biz_id`, `biz_type`, `status`, `valid_start_time`, `valid_end_time`, `remark`, `description`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

                userLevelStatement.setLong(1, levelDO.getUserId());
                userLevelStatement.setLong(2, levelDO.getLevelId());
                userLevelStatement.setString(3, levelDO.getLevelName());
                userLevelStatement.setString(4, levelDO.getBizId());
                userLevelStatement.setInt(5, levelDO.getBizType());
                userLevelStatement.setInt(6, levelDO.getStatus());
                userLevelStatement.setTimestamp(7, Timestamp.valueOf(levelDO.getValidStartTime()));
                userLevelStatement.setTimestamp(8, Timestamp.valueOf(levelDO.getValidEndTime()));
                userLevelStatement.setString(9, levelDO.getRemark());
                userLevelStatement.setString(10, levelDO.getDescription());
                userLevelStatement.setString(11, levelDO.getCreator());
                userLevelStatement.setTimestamp(12, Timestamp.valueOf(levelDO.getCreateTime()));
                userLevelStatement.setString(13, levelDO.getUpdater());
                userLevelStatement.setTimestamp(14, Timestamp.valueOf(levelDO.getUpdateTime()));
                userLevelStatement.setBoolean(15, levelDO.getDeleted());
                userLevelStatement.setString(16, "2");
                userLevelStatement.addBatch();

                // 权益日志插入
                userLevelStatement.executeUpdate();
                targetConnection.commit();
//                ResultSet rightsGeneratedKeys = userLevelStatement.getGeneratedKeys();
                userLevelStatement.close();
                targetConnection.close();
                System.out.println("================用户等级数据插入完成=====================");
            }

            // 2.2 设置用户权益日志
            PreparedStatement preparedStatement = sourceConnection
                    .prepareStatement("SELECT * FROM llm_user_benefits_usage_log WHERE user_id =? AND benefits_ids =?;");
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, String.valueOf(id));

            ResultSet userBenefitsUsageLogS = preparedStatement.executeQuery();

            while (userBenefitsUsageLogS.next()) {
                // 判断权益类型 仅仅处理 图片和魔法豆的数据
                String logUserId = userBenefitsUsageLogS.getString("user_id");
                String action = userBenefitsUsageLogS.getString("action");
                String benefitsType = userBenefitsUsageLogS.getString("benefits_type");
                String amount = userBenefitsUsageLogS.getString("amount");
                String outId = userBenefitsUsageLogS.getString("out_id");
                String benefitsIds = userBenefitsUsageLogS.getString("benefits_ids");
                String usageTime = userBenefitsUsageLogS.getString("usage_time");
                Timestamp logCreateTime = userBenefitsUsageLogS.getTimestamp("create_time");
                Timestamp logUpdateTime = userBenefitsUsageLogS.getTimestamp("update_time");

                AdminUserRightsRecordDO rightsRecordDO = new AdminUserRightsRecordDO();
                rightsRecordDO.setUserId(Long.valueOf(logUserId));
                rightsRecordDO.setBizCode(String.valueOf(rightId));
                rightsRecordDO.setBizId(outId != null ? outId : String.valueOf(rightId));
                rightsRecordDO.setBizType(buildRightsBizType(strategyId));
                rightsRecordDO.setTitle(buildRightsTitle(strategyId));
                rightsRecordDO.setDescription(buildRightsDesc(strategyId));
                rightsRecordDO.setRightsType(buildRightsType(benefitsType));
                rightsRecordDO.setRightsAmount(buildLogAmount(amount, action));
                rightsRecordDO.setDeleted(false);
                rightsRecordDO.setCreator(userId);
                rightsRecordDO.setUpdater(userId);
                rightsRecordDO.setCreateTime(LocalDateTimeUtil.of(logCreateTime));
                rightsRecordDO.setUpdateTime(LocalDateTimeUtil.of(logUpdateTime));
                rightsRecordDOS.add(rightsRecordDO);
            }
            if (CollUtil.isNotEmpty(rightsRecordDOS)) {

                System.out.println("================开始插入权益日志数据=====================");
                // 2.链接目标数据库
                Connection targetConnection = DriverManager.getConnection(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD);
                targetConnection.setAutoCommit(false);
                targetConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                // 2.1 批量插入数据
                PreparedStatement preparedStatementLog = targetConnection.prepareStatement("INSERT INTO system_user_rights_record ( " +
                        "user_id, biz_code, biz_id, biz_type, title, description, rights_type, rights_amount, creator, create_time, updater, update_time, deleted, tenant_id )values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );");
                for (AdminUserRightsRecordDO rightsRecordDO : rightsRecordDOS) {
                    preparedStatementLog.setLong(1, rightsRecordDO.getUserId());
                    preparedStatementLog.setString(2, rightsRecordDO.getBizCode());
                    preparedStatementLog.setString(3, rightsRecordDO.getBizId());
                    preparedStatementLog.setInt(4, rightsRecordDO.getBizType());
                    preparedStatementLog.setString(5, rightsRecordDO.getTitle());
                    preparedStatementLog.setString(6, rightsRecordDO.getDescription());
                    preparedStatementLog.setInt(7, rightsRecordDO.getRightsType());
                    preparedStatementLog.setInt(8, rightsRecordDO.getRightsAmount());
                    preparedStatementLog.setString(9, rightsRecordDO.getCreator());
                    preparedStatementLog.setTimestamp(10, Timestamp.valueOf(rightsRecordDO.getCreateTime()));
                    preparedStatementLog.setString(11, rightsRecordDO.getUpdater());
                    preparedStatementLog.setTimestamp(12, Timestamp.valueOf(rightsRecordDO.getUpdateTime()));
                    preparedStatementLog.setBoolean(13, rightsRecordDO.getDeleted());
                    preparedStatementLog.setString(14, "2");
                    preparedStatementLog.addBatch();
                }
                // 权益日志插入
                preparedStatementLog.executeBatch();
                targetConnection.commit();
                preparedStatementLog.close();
                targetConnection.close();
                System.out.println("================权益日志数据插入完成=====================");

            }


        }


    }

    private static String buildUserLevelName(String strategyId) {
        if (StrUtil.isNotBlank(strategyId)) {
            switch (strategyId) {
                // 高级
                case "5":
                case "6":
                    return "高级版";
                // 团队
                case "7":
                case "8":
                    return "团队版";

                // 基础
                case "48":
                case "49":
                    return "基础版";
                // 矩阵
                case "53":
                case "54":
                    return "矩阵版";
                // 体验
                default:
                case "55":
                    return "体验版";
            }
        }
        return "体验版";
    }

    private static Long buildUserLevelId(String strategyId) {
        if (StrUtil.isNotBlank(strategyId)) {
            switch (strategyId) {
                // 高级
                case "5":
                case "6":
                    return 4L;
                // 团队
                case "7":
                case "8":
                    return 5L;

                // 基础
                case "48":
                case "49":
                    return 3L;
                // 矩阵
                case "53":
                case "54":
                    return 6L;
                // 体验
                default:
                case "55":
                    return 2L;
            }
        }
        return 0L;
    }

    /**
     * 权益业务类型
     *
     * @param strategyId
     * @return
     */
    public static Integer buildRightsBizType(String strategyId) {
        if (StrUtil.isNotBlank(strategyId)) {
            switch (strategyId) {
                case "1":
                    return AdminUserRightsBizTypeEnum.REGISTER.getType();
                case "2":
                    return AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getType();
                case "3":
                    return AdminUserRightsBizTypeEnum.SIGN.getType();
                case "4":
                    return AdminUserRightsBizTypeEnum.USER_INVITE.getType();
                case "5":
                case "6":
                case "7":
                case "8":
                case "48":
                case "49":
                case "53":
                case "54":
                case "55":
                    return AdminUserRightsBizTypeEnum.ORDER_GIVE.getType();
                default:
                    return AdminUserRightsBizTypeEnum.REDEEM_CODE.getType();
            }
        }
        return 0;
    }

    /**
     * 权益 title
     *
     * @param strategyId
     * @return
     */
    public static String buildRightsTitle(String strategyId) {
        if (StrUtil.isNotBlank(strategyId)) {
            switch (strategyId) {
                case "1":
                    return AdminUserRightsBizTypeEnum.REGISTER.getName();
                case "2":
                    return AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getName();
                case "3":
                    return AdminUserRightsBizTypeEnum.SIGN.getName();
                case "4":
                    return AdminUserRightsBizTypeEnum.USER_INVITE.getName();
                case "5":
                case "6":
                case "7":
                case "8":
                case "48":
                case "49":
                case "53":
                case "54":
                case "55":
                    return AdminUserRightsBizTypeEnum.ORDER_GIVE.getName();
                default:
                    return AdminUserRightsBizTypeEnum.REDEEM_CODE.getName();
            }
        }
        return AdminUserRightsBizTypeEnum.ADMIN_ADD.getName();
    }

    public static String buildRightsDesc(String strategyId) {
        if (StrUtil.isNotBlank(strategyId)) {
            switch (strategyId) {
                case "1":
                    return AdminUserRightsBizTypeEnum.REGISTER.getDescription();
                case "2":
                    return AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getDescription();
                case "3":
                    return AdminUserRightsBizTypeEnum.SIGN.getDescription();
                case "4":
                    return AdminUserRightsBizTypeEnum.USER_INVITE.getDescription();
                case "5":
                case "6":
                case "7":
                case "8":
                case "48":
                case "49":
                case "53":
                case "54":
                case "55":
                    return AdminUserRightsBizTypeEnum.ORDER_GIVE.getDescription();
                default:
                    return AdminUserRightsBizTypeEnum.REDEEM_CODE.getDescription();
            }
        }
        return AdminUserRightsBizTypeEnum.ADMIN_ADD.getDescription();
    }

    public static Integer buildRightsType(String rightsType) {
        switch (rightsType) {
            case "IMAGE":
                return AdminUserRightsTypeEnum.MAGIC_IMAGE.getType();
            case "COMPUTATIONAL_POWER":
                return AdminUserRightsTypeEnum.MAGIC_BEAN.getType();
        }

        return -1;
    }

    public static Boolean buildDelete(String delete) {
        switch (delete) {
            case "1":
                return true;
            case "0":
            default:
                return false;
        }
    }

    public static Integer buildLogAmount(String amount, String action) {
        switch (action) {
            case "ADD":
                return Integer.parseInt(amount);
            case "USED":
            default:
                return -Integer.parseInt(amount);
        }
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
        if (stepWrappers != null && !stepWrappers.isEmpty()) {
            for (int i = 0; i < stepWrappers.size(); i++) {

                JSONObject object = stepWrappers.getJSONObject(i);
                WorkflowStepWrapperReqVO stepWrapperDTO = buildStepWrapper(object);

                if (i == 0 || !variables.isEmpty()) {
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
        if (variables != null && !variables.isEmpty()) {
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
