package com.starcloud.ops.llm.langchain.core.indexes.splitter;


public class CharacterTextSplitter extends BasicTextSplitter {

    @Override
    protected Long lengthFunction(String text) {
        return Long.valueOf(text.length());
    }

}
