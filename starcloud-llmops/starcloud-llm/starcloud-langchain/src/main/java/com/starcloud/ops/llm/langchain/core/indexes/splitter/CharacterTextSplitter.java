package com.starcloud.ops.llm.langchain.core.indexes.splitter;


import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class CharacterTextSplitter extends BasicTextSplitter {

    @Override
    protected Long lengthFunction(String text) {
        return Long.valueOf(text.length());
    }

    @Override
    protected List<String> split(String text, int chunkSize, List<String> separators) {
        List<String> result = new ArrayList<>();
        StringJoiner sj = new StringJoiner("|");
        for (String sep : separators) {
            sj.add(sep);
        }

        String[] arr = text.split(sj.toString()); // 使用正则表达式拆分字符串
        for (String s : arr) {
            if (s.length() <= chunkSize) {
                result.add(s);
            } else {
                int start = 0;
                while (start < s.length()) {
                    result.add(s.substring(start, Math.min(start + chunkSize, s.length())));
                    start += chunkSize;
                }
            }
        }
        return result;
    }
}
