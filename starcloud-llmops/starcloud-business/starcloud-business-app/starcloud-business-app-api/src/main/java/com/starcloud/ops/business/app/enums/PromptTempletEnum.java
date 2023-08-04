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

    ),

    HISTORY_SUMMARY("summary","Please summarize the main content of the following conversation in concise language,Ensure to use the language in the following content as the reply language. [content] %s [END content]"),



    RULE_GENERATE("","Given MY INTENDED AUDIENCES and HOPING TO SOLVE using a language model, please select the model prompt that best suits the input. \n" +
            "You will be provided with the prompt, variables, and an opening statement. \n" +
            "Only the content enclosed in curly braces, such as {{variable}}, in the prompt can be considered as a variable; \\\n" +
            "otherwise, it cannot exist as a variable in the variables.\n" +
            "If you believe revising the original input will result in a better response from the language model, you may \\\n" +
            "suggest revisions.\n" +
            "\n" +
            "<< FORMATTING >>\n" +
            "Return a JSON object formatted to look like, \\\n" +
            "no any other string out of JSON code snippet:\n" +
//            "```json\n" +
            "{{{{\n" +
            "    \"prompt\": string \\\\ generated prompt\n" +
            "    \"variables\": list of string \\\\ variables\n" +
            "    \"opening_statement\": string \\\\ an opening statement to guide users on how to ask questions with generated prompt \\\n" +
            "and fill in variables, with a welcome sentence, and keep TLDR.\n" +
            "}}}}\n" +
//            "```\n" +
            "\n" +
            "<< EXAMPLES >>\n" +
            "[EXAMPLE A]\n" +
//            "```json\n" +
            "{\n" +
            "  \"prompt\": \"Write a letter about love\",\n" +
            "  \"variables\": [],\n" +
            "  \"opening_statement\": \"Hi! I'm your love letter writer AI.\"\n" +
            "}\n" +
//            "```\n" +
            "\n" +
            "[EXAMPLE B]\n" +
//            "```json\n" +
            "{\n" +
            "  \"prompt\": \"Translate from {{lanA}} to {{lanB}}\",\n" +
            "  \"variables\": [\"lanA\", \"lanB\"],\n" +
            "  \"opening_statement\": \"Welcome to use translate app\"\n" +
            "}\n" +
//            "```\n" +
            "\n" +
            "[EXAMPLE C]\n" +
//            "```json\n" +
            "{\n" +
            "  \"prompt\": \"Write a story about {{topic}}\",\n" +
            "  \"variables\": [\"topic\"],\n" +
            "  \"opening_statement\": \"I'm your story writer\"\n" +
            "}\n" +
//            "```\n" +
            "\n" +
            "<< MY INTENDED AUDIENCES >>\n" +
            "{audiences} \n" +
            "\n" +
            "<< HOPING TO SOLVE >>\n" +
            "{hoping_to_solve} \n" +
            "\n" +
            "<< OUTPUT >>"),

    AUDIENCES("audiences","" ),

    HOPING_TO_SOLVE("hoping_to_solve","")

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
