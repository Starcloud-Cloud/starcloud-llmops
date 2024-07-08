package com.starcloud.ops.framework.common.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.Locale;

/**
 * 一般下拉框时使用
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "选项对象, 一般用于下拉框，用于描述 label 和 value")
public class Option implements Serializable {

    private static final long serialVersionUID = 178678534536774534L;

    /**
     * label：属性名，用于展示
     */
    @Schema(description = "属性名，用于展示")
    private String label;

    /**
     * value：属性值，用于传递
     */
    @Schema(description = "属性值，用于传递")
    private Object value;

    /**
     * 描述，用于展示，对该选项的描述
     */
    @Schema(description = "描述，用于展示，对该选项的描述")
    private String description;

    /**
     * 权限
     */
    @Schema(description = "权限")
    private String permissions;

    /**
     * 创建一个选项
     *
     * @param label label
     * @param value value
     * @return LabelValue 对象
     */
    public static Option of(String label, Object value) {
        Option option = new Option();
        option.setLabel(label);
        option.setValue(value);
        return option;
    }

    /**
     * 创建一个选项
     *
     * @param code    选项值
     * @param label   选项中文名
     * @param labelEn 选项英文名
     * @return Option 对象
     */
    public static Option of(Object code, String label, String labelEn) {
        return of(code, label, labelEn, null);
    }

    /**
     * 创建一个选项
     *
     * @param code        选项值
     * @param label       选项中文名
     * @param labelEn     选项英文名
     * @param description 选项描述
     * @return Option 对象
     */
    public static Option of(Object code, String label, String labelEn, String description) {
        Option option = new Option();
        option.setValue(code);
        Locale locale = LocaleContextHolder.getLocale();
        if (Locale.CHINA.equals(locale)) {
            option.setLabel(label);
        } else {
            option.setLabel(labelEn);
        }
        option.setDescription(description);
        return option;
    }

}
