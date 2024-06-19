package com.starcloud.ops.business.app.api.market.vo.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.config.ChatConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ImageConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用市场对象响应实体VO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用市场对象响应实体VO")
public class AppMarketRespVO implements Serializable {

    private static final long serialVersionUID = 4430780734779852216L;

    /**
     * 市场应用 UID
     */
    @Schema(description = "市场应用 UID")
    private String uid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用名称拼音
     */
    @Schema(description = "应用名称拼音")
    private String spell;

    /**
     * 应用名称拼音简拼
     */
    @Schema(description = "应用名称拼音简拼")
    private String spellSimple;

    /**
     * 应用类型
     */
    @Schema(description = "应用类型")
    private String type;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    @Schema(description = "应用模型：CHAT：聊天式应用，COMPLETION：生成式应用")
    private String model;

    /**
     * 应用版本，默认版本 1
     */
    @Schema(description = "应用版本，默认版本 1")
    private Integer version;

    /**
     * 应用语言
     */
    @Schema(description = "应用语言")
    private String language;

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

    private String source;

    /**
     * 应用标签，多个以逗号分割
     */
    @Schema(description = "应用标签，多个以逗号分割")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> tags;

    /**
     * 应用场景，多个以逗号分割
     */
    @Schema(description = "应用场景，多个以逗号分割")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> scenes;

    /**
     * 应用图片，多个以逗号分割
     */
    @Schema(description = "应用图片，多个以逗号分割")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> images;

    /**
     * 应用图标
     */
    @Schema(description = "应用图标")
    private String icon;

    /**
     * 应用是否是免费的
     */
    @Schema(description = "应用是否是免费的")
    private Boolean free;

    /**
     * 应用收费数
     */
    @Schema(description = "应用收费数")
    private BigDecimal cost;

    /**
     * 使用数量
     */
    @Schema(description = "使用数量")
    private Integer usageCount;

    /**
     * 应用点赞数量
     */
    @Schema(description = "应用点赞数量")
    private Integer likeCount;

    /**
     * 应用查看数量
     */
    @Schema(description = "应用查看数量")
    private Integer viewCount;

    /**
     * 应用安装数量
     */
    @Schema(description = "应用安装数量")
    private Integer installCount;

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
     * 应用图片配置
     */
    @Schema(description = "应用图片配置")
    private ImageConfigRespVO imageConfig;

    /**
     * 应用步骤数量
     */
    @Schema(description = "应用步骤数量")
    private Integer stepCount;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 应用example
     */
    @Schema(description = "应用example")
    private String example;

    /**
     * 应用演示
     */
    @Schema(description = "应用演示")
    private String demo;

    /**
     * 创建时间
     * 单元测试的时候没有初始化，所以这里加了jackson的注解
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String creator;

    /**
     * 更新者，
     */
    @Schema(description = "更新者")
    private String updater;

    /**
     * 是否收藏
     */
    @Schema(description = "是否收藏")
    private Boolean isFavorite;

    /**
     * 设置应用变量值
     *
     * @param stepId      步骤ID
     * @param variableMap 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putStepVariable(String stepId, Map<String, Object> variableMap) {
        if (workflowConfig != null) {
            workflowConfig.putVariable(stepId, variableMap);
        }
    }

    /**
     * 获取应用变量值
     *
     * @param stepId       步骤ID
     * @param variableName 变量名称
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO getStepVariable(String stepId, String variableName) {
        if (workflowConfig != null) {
            return workflowConfig.getStepVariable(stepId, variableName);
        }
        return null;
    }

    /**
     * 获取应用变量值
     *
     * @param stepId       步骤ID
     * @param variableName 变量名称
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getStepVariableValue(String stepId, String variableName) {
        if (workflowConfig != null) {
            return workflowConfig.getStepVariableValue(stepId, variableName);
        }
        return null;
    }

    /**
     * 设置应用变量值
     *
     * @param stepId      步骤ID
     * @param variableMap 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putStepModelVariable(String stepId, Map<String, Object> variableMap) {
        if (workflowConfig != null) {
            workflowConfig.putStepModelVariable(stepId, variableMap);
        }
    }

    /**
     * 获取应用变量值
     *
     * @param stepId       步骤ID
     * @param variableName 变量名称
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getStepModelVariableValue(String stepId, String variableName) {
        if (workflowConfig != null) {
            return workflowConfig.getStepModelVariableValue(stepId, variableName);
        }
        return null;
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
        return workflowConfig.getStepByHandler(handler);
    }

    /**
     * 合并应用
     *
     * @param appInformation 应用信息
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void merge(AppMarketRespVO appInformation) {
        // 只进行合并应用配置，其余的字段不进行替换
        this.workflowConfig.merge(appInformation.getWorkflowConfig());
    }
}
