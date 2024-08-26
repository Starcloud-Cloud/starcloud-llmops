package com.starcloud.ops.business.app.api.app.vo.response;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.app.vo.response.config.ChatConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ImageConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.verification.Verification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "应用返回 VO 对象")
public class AppRespVO implements Serializable {

    private static final long serialVersionUID = 4803486484547919894L;

    /**
     * 应用 UID, 每个应用的唯一标识
     */
    @Schema(description = "应用 UID, 每个应用的唯一标识")
    private String uid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    @Schema(description = "应用模型：CHAT：聊天式应用，COMPLETION：生成式应用")
    private String model;

    /**
     * 名称拼音
     */
    @Schema(description = "名称拼音")
    private String spell;

    /**
     * 名称拼音简拼
     */
    @Schema(description = "名称拼音简拼")
    private String spellSimple;

    /**
     * 应用类型：MYSELF：我的应用，DOWNLOAD：已下载应用
     */
    @Schema(description = "应用类型：MYSELF：我的应用，DOWNLOAD：已下载应用")
    private String type;

    /**
     * 应用来源类型：表示应用的是从那个平台创建，或者下载的。
     */
    @Schema(description = "应用来源类型：表示应用的是从那个平台创建，或者下载的。")
    private String source;

    /**
     * 应用排序，越小越靠前
     */
    @Schema(description = "应用排序")
    private Long sort;

    /**
     * 应用类别
     */
    @Schema(description = "应用类别")
    private String category;

    /**
     * 应用标签，多个以逗号分割
     */
    @Schema(description = "应用标签，多个以逗号分割")
    private List<String> tags;

    /**
     * 应用场景，多个以逗号分割
     */
    @Schema(description = "应用类别，多个以逗号分割")
    private List<String> scenes;

    /**
     * 应用图片，多个以逗号分割
     */
    @Schema(description = "应用图片，多个以逗号分割")
    private List<String> images;

    /**
     * 应用图标
     */
    @Schema(description = "应用图标")
    private String icon;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    @Schema(description = "应用详细配置信息, 步骤，变量，场景等")
    private WorkflowConfigRespVO workflowConfig;

    /**
     * 应用聊天配置
     */
    @Schema(description = "应用聊天配置")
    private ChatConfigRespVO chatConfig;

    /**
     * 应用生成图片配置
     */
    @Schema(description = "生成图片配置")
    private ImageConfigRespVO imageConfig;

    /**
     * 应用步骤图标、多个以逗号分割
     */
    @Schema(description = "应用步骤图标、多个以逗号分割")
    private List<String> actionIcons;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 应用示例
     */
    @Schema(description = "应用示例")
    private String example;

    /**
     * 应用演示
     */
    @Schema(description = "应用演示")
    private String demo;

    /**
     * 应用发布成功后，应用市场 uid-version
     */
    @Schema(description = "应用发布成功后，应用市场 uid-version")
    private String publishUid;

    /**
     * 应用安装成功后，应用市场 uid-version
     */
    @Schema(description = "应用安装成功后，应用市场 uid-version")
    private String installUid;

    /**
     * 应用创建者
     */
    @Schema(description = "应用创建者")
    private String creator;

    /**
     * 应用创建者名称
     */
    private String creatorName;

    /**
     * 应用修改者
     */
    @Schema(description = "应用修改者")
    private String updater;

    /**
     * 应用修改者名称
     */
    @Schema(description = "应用修改者名称")
    private String updaterName;

    /**
     * 应用创建时间
     */
    @Schema(description = "应用创建时间")
    private LocalDateTime createTime;

    /**
     * 应用更新时间
     */
    @Schema(description = "应用更新时间")
    private LocalDateTime updateTime;

    /**
     * 最后一次上传到应用市场时间
     */
    @Schema(description = "最后一次上传到应用市场时间")
    private LocalDateTime lastPublish;

    /**
     * 租户ID
     */
    @Schema(description = "租户Id")
    private Long tenantId;

    /**
     * 应用状态
     */
    @Schema(description = "验证消息")
    private List<Verification> verificationList;

    /*
     * 补充步骤默认变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void supplementStepVariable(Map<String, VariableRespVO> variableRespVOMap) {
        if (Objects.isNull(workflowConfig) || CollectionUtil.isEmpty(variableRespVOMap)) {
            return;
        }
        workflowConfig.supplementStepVariable(variableRespVOMap);
    }

    /**
     * 获取步骤
     *
     * @param handler 步骤处理器
     * @return 步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapperRespVO getStepByHandler(String handler) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getStepByHandler(handler);
    }

    /**
     * 根据 handler 获取步骤
     *
     * @param clazz 步骤handler类
     * @return 步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapperRespVO getStepByHandler(Class<?> clazz) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getStepByHandler(clazz);
    }

    /**
     * 应用配置设置
     *
     * @param handler 处理去
     * @param wrapper 步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void setStepByHandler(String handler, WorkflowStepWrapperRespVO wrapper) {
        if (Objects.isNull(workflowConfig)) {
            return;
        }
        workflowConfig.setStepByHandler(handler, wrapper);
    }

    /**
     * 应用配置设置
     *
     * @param clazz   类名
     * @param wrapper 步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void setStepByHandler(Class<?> clazz, WorkflowStepWrapperRespVO wrapper) {
        if (Objects.isNull(workflowConfig)) {
            return;
        }
        workflowConfig.setStepByHandler(clazz, wrapper);
    }

    /**
     * 获取步骤变量
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return VariableItemRespVO
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO getVariableItem(String stepId, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getVariableItem(stepId, field);
    }

    /**
     * 根据变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getVariable(String stepId, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getVariable(stepId, field);
    }

    /**
     * 根据变量的{@code field}获取变量的值，并且将值转换为字符串，找不到时返回空字符串
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getVariableToString(String stepId, String field) {
        if (Objects.isNull(workflowConfig)) {
            return StringUtils.EMPTY;
        }
        return workflowConfig.getVariableToString(stepId, field);
    }


    /**
     * 将变量为{@code field}的值设置为{@code value}
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @param value  变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(String stepId, String field, Object value) {
        if (Objects.isNull(workflowConfig)) {
            return;
        }
        workflowConfig.putVariable(stepId, field, value);
    }

    /**
     * 根据模型变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return VariableItemRespVO
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO getModelVariableItem(String stepId, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getModelVariableItem(stepId, field);
    }

    /**
     * 根据模型变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getModelVariable(String stepId, String field) {
        if (Objects.isNull(workflowConfig)) {
            return null;
        }
        return workflowConfig.getModelVariable(stepId, field);
    }

    /**
     * 根据模型变量的{@code field}获取变量的值，并且将值转换为字符串，找不到时返回空字符串
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getModelVariableToString(String stepId, String field) {
        if (Objects.isNull(workflowConfig)) {
            return StringUtils.EMPTY;
        }
        return workflowConfig.getModelVariableToString(stepId, field);
    }

    /**
     * 将模型变量为{@code field}的值设置为{@code value}
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @param value  变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putModelVariable(String stepId, String field, Object value) {
        if (Objects.isNull(workflowConfig)) {
            return;
        }
        workflowConfig.putModelVariable(stepId, field, value);
    }

}
