package com.starcloud.ops.business.app.service.chat.impl;

import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.vo.response.RuleGenerateRespVO;
import com.starcloud.ops.business.app.api.chat.RuleGenerateRequest;
import com.starcloud.ops.business.app.enums.PromptTempletEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.service.chat.RuleGenerateService;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.CHAT_ROLE_GENERATE_ERROR;

/**
 * @author starcloud
 */
@Service
@Slf4j
public class RuleGenerateServiceImpl implements RuleGenerateService {


    @Override
    public RuleGenerateRespVO generateRule(RuleGenerateRequest request) {
        String messageTemp = PromptTempletEnum.RULE_GENERATE.getTemp();
        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Collections.singletonList(
                        HumanMessagePromptTemplate.fromTemplate(messageTemp)
                )
        );
        ChatOpenAI chatOpenAi = new ChatOpenAI();
        List<BaseVariable> variables = preGenerateVariables(request);
        PromptValue promptValue = chatPromptTemplate.formatPrompt(variables);
        BaseLLMResult<ChatCompletionResult> result = chatOpenAi.generatePrompt(Collections.singletonList(promptValue));
        try {
            return JSON.parseObject(result.getText(), RuleGenerateRespVO.class);
        } catch (Exception e) {
            log.warn("Parsing text {} of rule config generator raised following error: ", result.getText(), e);
            throw exception(CHAT_ROLE_GENERATE_ERROR);
        }
    }


    private List<BaseVariable> preGenerateVariables(RuleGenerateRequest request) {
        List<BaseVariable> variables = new ArrayList<>();
        variables.add(BaseVariable.builder()
                .field(PromptTempletEnum.AUDIENCES.getKey())
                .value(request.getAudiences())
                .build());

        variables.add(BaseVariable.builder()
                .field(PromptTempletEnum.HOPING_TO_SOLVE.getKey())
                .value(request.getHopingToSolve())
                .build());
        return variables;
    }
}
