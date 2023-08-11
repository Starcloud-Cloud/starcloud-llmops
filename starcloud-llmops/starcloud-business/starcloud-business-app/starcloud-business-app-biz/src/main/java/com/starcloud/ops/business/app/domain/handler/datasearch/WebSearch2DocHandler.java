package com.starcloud.ops.business.app.domain.handler.datasearch;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataDetailsInfoVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import com.starcloud.ops.llm.langchain.core.tools.RequestsGetTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 页面内容爬取，并创建对应索引和总结
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
public class WebSearch2DocHandler extends BaseHandler<WebSearch2DocHandler.Request, WebSearch2DocHandler.Response> {

    private String name = "WebSearch2DocHandler";

    private String description = "A portal to the internet. Use this when you need to get specific content from a website. Input should be a  url (i.e. https://www.google.com). The output should be a json string with two keys: \"content\" and\" docKey\". The value of \"content\" is a summary of the content of the website, and the value of\" docKey\" is the tag of the website to point to.";

    private static RequestsGetTool requestsGetTool = new RequestsGetTool();

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {

        String url = context.getRequest().getUrl();

        //@todo 通过上下文获取当前可能配置的 tools 执行 tips
        InteractiveInfo interactiveInfo = InteractiveInfo.buildUrlCard(url).setTips("AI分析链接内容");

        context.sendCallbackInteractiveStart(interactiveInfo);

        String datasetId = context.getAppUid();


        HandlerResponse<Response> handlerResponse = new HandlerResponse();
        handlerResponse.setSuccess(false);
        handlerResponse.setMessage(url);

        Response result = new Response();

        try {
            UploadUrlReqVO uploadUrlReqVO = new UploadUrlReqVO();
            uploadUrlReqVO.setSync(true);
            uploadUrlReqVO.setUrls(Arrays.asList(url));
            uploadUrlReqVO.setDatasetId(datasetId);

            SplitRule splitRule = new SplitRule();
            splitRule.setAutomatic(true);
            uploadUrlReqVO.setSplitRule(splitRule);


            //List<SourceDataUploadDTO> sourceDataUploadDTOS = datasetSourceDataService.uploadUrlsSourceData(uploadUrlReqVO);


            //DatasetSourceDataDetailsInfoVO detailsInfoVO = datasetSourceDataService.getSourceDataDetailsInfo(datasetId, true);

            DatasetSourceDataDetailsInfoVO detailsInfoVO = new DatasetSourceDataDetailsInfoVO();

            detailsInfoVO.setSummaryContent("我是页面：" + url + "的总结.这是一个关于美食的页面内容");
            detailsInfoVO.setUid("doc-key-abcadda");
            //@todo 如果没有返回怎么办

            result.setSummary(detailsInfoVO.getSummaryContent());
            result.setDocKey(detailsInfoVO.getUid());

            handlerResponse.setSuccess(true);
            handlerResponse.setAnswer(result.getSummary());
            handlerResponse.setOutput(result);

            context.sendCallbackInteractiveEnd(interactiveInfo);

        } catch (Exception e) {

            handlerResponse.setErrorCode("0");
            handlerResponse.setErrorMsg(e.getMessage());

            interactiveInfo.setStatus(1);
            interactiveInfo.setSuccess(false);
            interactiveInfo.setErrorMsg(e.getMessage());

            context.sendCallbackInteractiveEnd(interactiveInfo);

            log.error("WebSearch2DocHandler process is fail: {}", e.getMessage(), e);
        }

        return handlerResponse;
    }


    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("a website url")
        private String url;

    }


    @Data
    public static class Response {

        private String summary;

        private String docKey;

    }

}
