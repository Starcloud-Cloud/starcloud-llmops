package com.starcloud.ops.business.app.domain.handler.datasearch;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataDetailsInfoVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    private DatasetSourceDataService datasetSourceDataService = SpringUtil.getBean(DatasetSourceDataService.class);

    private String userName = "网页和文档分析";

    private String userDescription = "可访问网络上公开的网页，文档内容。可基于内容完成摘要，问答等。仅支持10M以内文档，不支持扫描件";

    private String name = "WebSearch2DocHandler";

    private String description = "A portal to the internet. Use this when you need to get specific content from a website. Input should be a  url (i.e. https://www.google.com). The output should be a json string with two keys: \"content\" and\" docKey\". The value of \"content\" is a summary of the content of the website, and the value of\" docKey\" is the tag of the website to point to.";

    private int summarySubSize = 300;

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


        UploadUrlReqVO uploadUrlReqVO = new UploadUrlReqVO();
        uploadUrlReqVO.setSync(true);
        uploadUrlReqVO.setUrls(Arrays.asList(url));
        uploadUrlReqVO.setDatasetId(datasetId);

        SplitRule splitRule = new SplitRule();
        splitRule.setAutomatic(true);
        splitRule.setRemoveExtraSpaces(true);

        List<SourceDataUploadDTO> sourceDataUploadDTOS = datasetSourceDataService.uploadUrlsSourceData(uploadUrlReqVO);
        SourceDataUploadDTO sourceDataUploadDTO = Optional.ofNullable(sourceDataUploadDTOS).orElse(new ArrayList<>()).stream().findFirst().get();

        if (!sourceDataUploadDTO.getStatus()) {
            log.error("WebSearch2DocHandler uploadUrlsSourceData is fail:{}, {}", url, sourceDataUploadDTO.getErrMsg());

            throw new RuntimeException("URL解析失败");
        }

        //查询内容
        DatasetSourceDataDetailsInfoVO detailsInfoVO = datasetSourceDataService.getSourceDataListData(datasetId, true);
        String summary = StrUtil.isNotBlank(detailsInfoVO.getSummary()) ? detailsInfoVO.getSummary() : detailsInfoVO.getDescription();

        summary = StrUtil.subPre(summary, summarySubSize);

        //先截取
        result.setSummary(summary);
        result.setDocKey(detailsInfoVO.getUid());

        handlerResponse.setSuccess(true);
        handlerResponse.setAnswer(summary);
        handlerResponse.setOutput(result);

        context.sendCallbackInteractiveEnd(interactiveInfo);

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
