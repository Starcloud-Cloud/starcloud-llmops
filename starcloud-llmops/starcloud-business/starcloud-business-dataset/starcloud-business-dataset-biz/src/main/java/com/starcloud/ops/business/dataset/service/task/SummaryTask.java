package com.starcloud.ops.business.dataset.service.task;

import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Component
public class SummaryTask {

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 32,
            60, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(100), new ThreadFactory() {
        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, "SummaryTask");
        }
    }, new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.warn("SummaryTask Thread pool is full");
            throw new RuntimeException("SummaryTask Thread pool is full");
        }
    });

    public void execute(SummaryEntity entity) {

        threadPoolExecutor.execute(() -> {
            try {
                // openai总结
                ChatOpenAI openAi = new ChatOpenAI();
                List<BaseMessage> messages = new ArrayList<>();
                HumanMessage humanMessage = new HumanMessage(entity.getPrompt());
                messages.add(humanMessage);
                ChatResult<ChatCompletionResult> openaiResult = openAi._generate(messages, null, null, null);
                // 结果放入map
                entity.getResultMap().put(entity.getIndex(), openaiResult.getText());
                entity.getCountDownLatch().countDown();
            } catch (Exception e) {
                log.info("summary error:", e);
                entity.getCountDownLatch().countDown();
            }
        });
    }

}
