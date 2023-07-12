package com.starcloud.ops.business.app.enums;

/**
 * @author starcloud
 */

public enum PromptTempletEnum {

    DATASET_CONTEXT("context", "Use the following CONTEXT as your learned knowledge:\n" +
            "[CONTEXT]\n" +
            "{context}\n" +
            "[END CONTEXT]\n" +
            "When answer to user:\n" +
            "- If you don't know, just say that you don't know.\n" +
            "- If you don't know when you are not sure, ask for clarification. \n" +
            "Avoid mentioning that you obtained the information from the context. \n"),

    HISTORY_TEMP("history", "{history}"),

    INPUT_TEMP("input", "Human: {input}\nAI: "),

    SUGGESTED_QUESTIONS("history", "{history}\n" +
            "Please help me predict the three most likely questions that human would ask, " +
            "and keeping each question under 20 characters.\n" +
            "The output must be in JSON format following the specified schema:\n" +
            "[\"question1\",\"question2\",\"question3\"]\n"

    );

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
