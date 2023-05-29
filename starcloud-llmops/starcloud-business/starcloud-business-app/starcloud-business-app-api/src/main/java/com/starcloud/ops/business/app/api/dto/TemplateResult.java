package com.starcloud.ops.business.app.api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateResult<T> {

    private boolean status;

    private T data;

    private int code;

    private String message;

    private String error;

    public static <T> TemplateResult success(T t) {
        TemplateResult<T> result = new TemplateResult<>(true, t, 200, "query template is success", null);
        return result;
    }

    public static <T> TemplateResult success(T t, String msg) {
        TemplateResult<T> result = new TemplateResult<>(true, t, 200, msg, null);
        return result;
    }

    public static <T> TemplateResult success(int code, String msg) {
        TemplateResult<T> result = new TemplateResult<>(true, null, code, msg, null);
        return result;
    }


    public static <T> TemplateResult error(String msg) {
        TemplateResult<T> result = new TemplateResult<>();
        result.setError(msg);
        result.setStatus(false);
        return result;
    }
}
