package com.starcloud.ops.business.app.model.poster;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class PosterVariableDTO implements java.io.Serializable {

    private static final long serialVersionUID = -6116842128671853185L;

    /**
     * 变量唯一值标识
     */
    private String uuid;

    /**
     * 变量 label
     */
    private String label;

    /**
     * 变量 field
     */
    private String field;

    /**
     * 变量类型
     */
    private String type;

    /**
     * 变量分组
     */
    private String group;

    /**
     * 变量样式
     */
    private String style;

    /**
     * 变量排序
     */
    private Integer order;

    /**
     * 变量默认值
     */
    private Object defaultValue;

    /**
     * 变量值
     */
    private Object value;

    /**
     * 变量是否显示
     */
    private Boolean isShow;

    /**
     * 变量是否为点位
     */
    private Boolean isPoint;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 变量要求，用于AI生成时候的要求
     */
    private String requirement;

    /**
     * 变量选项, 变量类型为 SELECT 时使用
     */
    private List<Option> options;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 如果变量值为 null 则返回空字符串
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String emptyIfNullValue() {
        if (this.value == null) {
            return "";
        }
        if (this.value instanceof String) {
            if (StringUtils.isBlank(String.valueOf(this.value)) || "null".equalsIgnoreCase(String.valueOf(this.value))) {
                return "";
            }
        }
        return String.valueOf(this.value);
    }

    /**
     * 校验
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {
        AppValidate.notBlank(this.field, "缺少系统必填项！(" + this.label + ")变量field不能为空！请联系管理员！");
    }
}
