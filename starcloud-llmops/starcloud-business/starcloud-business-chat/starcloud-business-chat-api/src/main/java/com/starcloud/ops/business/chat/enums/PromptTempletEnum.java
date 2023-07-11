package com.starcloud.ops.business.chat.enums;

/**
 * @author starcloud
 */

public enum PromptTempletEnum {

    DATASET_CONTEXT("context","Use the following CONTEXT as your learned knowledge:\n" +
            "[CONTEXT]\n" +
            "{context}\n" +
            "[END CONTEXT]\n" +
            "When answer to user:\n" +
            "- If you don't know, just say that you don't know.\n" +
            "- If you don't know when you are not sure, ask for clarification. \n" +
            "Avoid mentioning that you obtained the information from the context. \n"),

    HISTORY_TEMP("history","{history}"),

    INPUT_TEMP("input","Human: {input}\nAI: ")
    ;

    private String key;

    private String temp;

    PromptTempletEnum(String key, String temp) {
        this.key = key;
        this.temp = temp;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
