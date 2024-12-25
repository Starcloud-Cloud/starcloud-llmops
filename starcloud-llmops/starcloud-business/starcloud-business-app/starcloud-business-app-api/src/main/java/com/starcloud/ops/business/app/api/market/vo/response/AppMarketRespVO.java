package com.starcloud.ops.business.app.api.market.vo.response;

import cn.hutool.core.collection.CollectionUtil;
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
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     * 主键ID
     */
    private Long id;

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
     * 应用插件列表
     */
    @Schema(description = "插件列表")
    private List<String> pluginList;

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
     * 补充步骤默认变量
     * @param supplementStepWrapperMap 补充步骤包装 map
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void supplementStepVariable(Map<String, WorkflowStepWrapperRespVO> supplementStepWrapperMap) {
        if (Objects.isNull(workflowConfig) || CollectionUtil.isEmpty(supplementStepWrapperMap)) {
            return;
        }
        workflowConfig.supplementStepVariable(supplementStepWrapperMap);
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
     * 设置开始节点的参数
     * "模拟开始节点"
     * @todo 需要把传入的参数和已保存的默认值 做合并处理
     * @param value
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putStartVariable(Object value) {
        if (Objects.isNull(workflowConfig)) {
            return;
        }

        //配置的jsonSchema还在
        workflowConfig.getVariable().setData(value);
        /**
         * 下游获取参数逻辑
         * 1，下游节点获取字段，优先从已配置的字段中获取。也需要处理 占位符替换问题 （这时候就要支持 开始节点.XXX 结构了）
         * 2，如果找不到，就从 开始节点获取，这么做是为了兼容（因为现在我们也不好直接真加个 "开始节点"）。 从头开始执行就等于 coze执行 （coze 节点调试执行，等于手动填入节点参数去执行）, 后续的节点参数必须是有绑定的（最少要绑定到开始节点的入参KEY，所以本来是不会支持主动解析 开始节点参数到节点字段的映射关系的）
         */
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
