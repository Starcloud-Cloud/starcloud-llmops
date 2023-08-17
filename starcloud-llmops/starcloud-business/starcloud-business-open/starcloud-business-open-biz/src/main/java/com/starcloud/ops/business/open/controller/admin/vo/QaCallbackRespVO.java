package com.starcloud.ops.business.open.controller.admin.vo;

import lombok.Data;

@Data
public class QaCallbackRespVO {

    private Integer code;

    private String message;

    private Data data;

    @lombok.Data
    public static class Info {
        private String text;

    }


    @lombok.Data
    public static class Data {
        private Integer type;

        private Info info;

    }

    public static QaCallbackRespVO success(String text) {
        QaCallbackRespVO response = new QaCallbackRespVO();
        response.setCode(0);
        response.setMessage("success");
        Data data = new Data();
        Info info = new Info();
        info.setText(text);
        data.setType(5000);
        data.setInfo(info);
        response.setData(data);
        return response;
    }
}
