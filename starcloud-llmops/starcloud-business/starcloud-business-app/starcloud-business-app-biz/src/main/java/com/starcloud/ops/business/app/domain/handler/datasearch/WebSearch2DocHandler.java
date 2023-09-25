package com.starcloud.ops.business.app.domain.handler.datasearch;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveData;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataDetailsInfoVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceSendMessage;
import com.starcloud.ops.business.dataset.pojo.dto.UserBaseDTO;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.llm.langchain.core.memory.summary.SummarizerMixin;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatGeneration;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 页面内容,网络文件内容爬取，并创建对应索引和总结
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@Component
public class WebSearch2DocHandler extends BaseToolHandler<WebSearch2DocHandler.Request, WebSearch2DocHandler.Response> {

    private static DatasetSourceDataService datasetSourceDataService = SpringUtil.getBean(DatasetSourceDataService.class);

    private static UserBenefitsService benefitsService = SpringUtil.getBean(UserBenefitsService.class);

    private String userName = "网页和文档分析";

    private String userDescription = "可访问网络上公开的网页，文档内容。可基于内容完成摘要，问答等。不支持扫描件";

    private String name = "WebSearch2DocHandler";

    private String description = "A portal to the internet. Use this when you need to get specific content from a website. Input should be a  url (i.e. https://www.google.com). The output should be a json string with two keys: \"content\" and\" docId\". The value of \"content\" is a answer of the content of the website, and the value of\" docId\" is the ID of the website to point to.";

    private int summarySubSize = 300;

    private String usage = "1.帮我看下 https://www.hangzhou2022.cn/fw/emwtjd/202308/t20230825_70460.shtml 说了什么？ \n" +
            "2.https://www.hangzhou2022.cn/xwzx/jdxw/ttxw/202308/t20230824_70312.shtml 总结下里面的内容";


    private String icon = "FindInPage";


    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {

        String url = context.getRequest().getUrl();

        InteractiveInfo interactiveInfo = InteractiveInfo.buildUrlCard("分析链接内容中[" + url + "]...").setToolHandler(this).setInput(context.getRequest());
        context.sendCallbackInteractiveStart(interactiveInfo);

        HandlerResponse<Response> handlerResponse = new HandlerResponse();
        handlerResponse.setSuccess(false);
        handlerResponse.setMessage(JsonUtils.toJsonString(context.getRequest()));


        UploadUrlReqVO uploadUrlReqVO = new UploadUrlReqVO();

        uploadUrlReqVO.setCleanSync(true);
        uploadUrlReqVO.setSplitSync(false);
        uploadUrlReqVO.setIndexSync(false);

        uploadUrlReqVO.setSessionId(context.getConversationUid());
        uploadUrlReqVO.setUrls(Arrays.asList(url));
        uploadUrlReqVO.setAppId(context.getAppUid());
        // TODO 添加创建人或者游客

        UserBaseDTO baseDBHandleDTO = new UserBaseDTO();
        baseDBHandleDTO.setCreator(context.getUserId());
        baseDBHandleDTO.setEndUser(context.getEndUser());

        List<SourceDataUploadDTO> sourceDataUploadDTOS = datasetSourceDataService.uploadUrlsSourceDataBySession(uploadUrlReqVO, baseDBHandleDTO);
        SourceDataUploadDTO sourceDataUploadDTO = Optional.ofNullable(sourceDataUploadDTOS).orElse(new ArrayList<>()).stream().findFirst().get();

        if (!sourceDataUploadDTO.getStatus()) {
            log.error("WebSearch2DocHandler uploadUrlsSourceDataBySession is fail:{}, {}", url, sourceDataUploadDTO.getErrMsg());

            throw new RuntimeException("URL解析失败");
        }


        //@todo 总结的执行过程需要输出到前端

        // 查询内容
        DatasetSourceDataDetailsInfoVO detailsInfoVO = datasetSourceDataService.getSourceDataById(sourceDataUploadDTO.getSourceDataId(), true);
        String content = detailsInfoVO.getSummaryStatus() && StrUtil.isNotBlank(detailsInfoVO.getSummary()) ? detailsInfoVO.getSummary() : detailsInfoVO.getDescription();
        handlerResponse.setSuccess(true);

        Response result = new Response();
        // 先截取
        result.setTitle(detailsInfoVO.getName());
        result.setContent(content);
        result.setDocId(detailsInfoVO.getId());
        handlerResponse.setOutput(result);


        InteractiveData interactiveData = new InteractiveData();

        interactiveData.setTitle(detailsInfoVO.getName());
        interactiveData.setContent(result.getContent());
        interactiveData.setUrl(url);
        interactiveData.setTime(DateUtil.now());

        List<InteractiveData> dataList = Arrays.asList(interactiveData);

        // handlerResponse.setExt(dataList);

        interactiveInfo.setData(dataList);
        interactiveInfo.setTips("分析链接完成");
        context.sendCallbackInteractiveEnd(interactiveInfo);

        //总结执行
        if (StrUtil.isNotBlank(detailsInfoVO.getCleanContent())) {

            //未开启
            if (Boolean.TRUE.equals(context.getRequest().getNeedSummary())) {

                String query = context.getRequest().getQuery();

                InteractiveInfo info = InteractiveInfo.buildUrlCard("分析链接内容并生成回答[" + query + "]...").setToolHandler(this).setInput(context.getRequest());
                context.sendCallbackInteractiveStart(info);

                String summary = this.processSummary(context, detailsInfoVO.getCleanContent());
                if (StrUtil.isNotBlank(summary)) {
                    result.setContent(summary);

                    //提示内容改为总结的
                    interactiveData.setContent(summary);
                    List<InteractiveData> summaryList = Arrays.asList(interactiveData);

                    info.setData(summaryList);
                    info.setTips("生成回答完毕");
                }

                context.sendCallbackInteractiveEnd(info);
            }
        }

        return handlerResponse;
    }


    /**
     * @todo 临时方案，需要执行 流程, 不然没地方记录消耗
     * <p>
     * 执行总结流程，不影响主流程
     */
    protected String processSummary(HandlerContext<Request> context, String content) {

        log.info("WebSearch2DocHandler processSummary start");
        String summary = null;

        BaseLLMResult baseLLMResult = SummarizerMixin.summaryContentCall(content, context.getRequest().getQuery(), 500);

        //失败不影响主流程
        if (baseLLMResult != null) {

            summary = baseLLMResult.getText();
            BaseLLMUsage llmUsage = baseLLMResult.getUsage();
            List<ChatGeneration<ChatCompletionResult>> completionResults = baseLLMResult.getGenerations();
            String llmModel = Optional.ofNullable(completionResults).orElse(new ArrayList<>()).stream().findFirst().map(ChatGeneration::getGenerationInfo).map(ChatCompletionResult::getModel).orElse("");

            ModelTypeEnum modelType = TokenCalculator.fromName(llmModel);
            Long messageTokens = llmUsage.getPromptTokens();
            Long answerTokens = llmUsage.getCompletionTokens();
            //总价
            BigDecimal totalPrice = TokenCalculator.getTextPrice(messageTokens, modelType, true).add(TokenCalculator.getTextPrice(answerTokens, modelType, false));


            //临时的
            DatasetSourceDataDO sourceDataDO = new DatasetSourceDataDO();
            //记录做统计用
            sourceDataDO.setSummary(summary);
            sourceDataDO.setSummaryStatus(1);
            sourceDataDO.setSummaryModel(llmModel);
            sourceDataDO.setSummaryTokens(llmUsage.getTotalTokens());
            sourceDataDO.setSummaryTotalPrice(totalPrice);

            //先记录到权益上
            //@todo 无法获取到messageId, 只能用个临时的标识处理
            String outId = "web2doc_summary_" + context.getAppUid();
            benefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), llmUsage.getTotalTokens(), context.getUserId(), outId);


            log.info("WebSearch2DocHandler processSummary end: {}", JsonUtils.toJsonString(sourceDataDO));
        }
        //自动切换LLM进行总结了

        return summary;
    }

    /**
     * 包装为 下午文 文档结构
     * 默认实现，工具类型返回
     */
    @Override
    public List<MessageContentDocDTO> convertContentDoc(HandlerContext<Request> context, HandlerResponse<Response> handlerResponse) {

        // 解析返回的内容 生成 MessageContentDocDTO
        List<MessageContentDocDTO> messageContentDocDTOList = new ArrayList<>();

        MessageContentDocDTO messageContentDocDTO = new MessageContentDocDTO();

        messageContentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name());

        messageContentDocDTO.setTime(LocalDateTimeUtil.now().toString());
        messageContentDocDTO.setContent(handlerResponse.getOutput().getContent());
        messageContentDocDTO.setId(handlerResponse.getOutput().getDocId());

        messageContentDocDTOList.add(messageContentDocDTO);

        return messageContentDocDTOList;
    }


    @Data
    public static class Request implements Serializable {

        @JsonProperty(required = true)
        @JsonPropertyDescription("a website url")
        private String url;

        @JsonProperty(required = true)
        @JsonPropertyDescription("summarize the url content. This value is true when the question being answered requires the contents of the url, The default is false")
        private Boolean needSummary;

        @JsonProperty(required = true)
        @JsonPropertyDescription("Questions about the content, When you have a question, you need to summarize the content")
        private String query;

    }


    @Data
    public static class Response implements Serializable {

        private String title;

        private String content;

        private Long docId;

    }

}
