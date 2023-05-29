package com.starcloud.ops.llm.langchain.core.prompt.base.variable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseVariable {

    private VariableTypeEnum type;

    private String field;

    private Object defaultValue;

    private Object value;

    private Map<String, String> options;

    public static BaseVariable newString(String field, String value) {
        return BaseVariable.builder().type(VariableTypeEnum.STR).field(field).value(value).build();
    }

    public static BaseVariable newString(String field) {
        return BaseVariable.builder().type(VariableTypeEnum.STR).field(field).build();
    }

    public static BaseVariable newBoolean(String field) {
        return BaseVariable.builder().type(VariableTypeEnum.BOOLEAN).field(field).build();
    }

    public static BaseVariable newInt(String field) {
        return BaseVariable.builder().type(VariableTypeEnum.INT).field(field).build();
    }

    public static BaseVariable newArray(String field) {
        return BaseVariable.builder().type(VariableTypeEnum.ARRAY).field(field).build();
    }

    public static BaseVariable of(BaseVariable variable) {

        return new BaseVariable(
                variable.getType(),
                variable.getField(),
                variable.getDefaultValue(),
                variable.getValue(),
                variable.getOptions()
        );
    }

    /**
     * type
     */
    public enum VariableTypeEnum {

        INT,

        STR,

        ARRAY,

        BOOLEAN;
    }
}
