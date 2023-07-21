package com.starcloud.ops.business.dataset.service.task;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class SummaryEntity {

    private ConcurrentHashMap<Integer, String> resultMap;

    private CountDownLatch countDownLatch;

    private String text;

    private Integer index;

    private Integer summarySize;


    public SummaryEntity(ConcurrentHashMap<Integer, String> resultMap, CountDownLatch countDownLatch) {

        this.resultMap = resultMap;
        this.countDownLatch = countDownLatch;
    }


    public ConcurrentHashMap<Integer, String> getResultMap() {
        return resultMap;
    }

    public void setResultMap(ConcurrentHashMap<Integer, String> resultMap) {
        this.resultMap = resultMap;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getSummarySize() {
        return summarySize;
    }

    public void setSummarySize(Integer summarySize) {
        this.summarySize = summarySize;
    }

    public String getPrompt() {
        return String.format("summarize the following content in %s words,Ensure to use the language in the following content as the reply language. [content] %s [END content]",
                summarySize, text);
    }
}
