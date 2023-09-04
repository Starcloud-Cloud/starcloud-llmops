package com.starcloud.ops.business.app.domain.handler.datasearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveData;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.llm.langchain.core.tools.SerpAPITool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 通过 Google搜索新闻，并只返回 URL 列表
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@Component
public class NewsSearchHandler extends BaseToolHandler<SearchEngineHandler.Request, SearchEngineHandler.Response> {

    private String userName = "互联网搜索新闻";

    private String userDescription = "可以自动联网查询新闻";

    private String name = "NewsSearchHandler";

    private String description = "Search News engines. Useful when you need to search for news on the Internet. The input should be a news related to search queries.";


    private static SerpAPITool serpAPITool = new SerpAPITool();


    @Override
    protected HandlerResponse<SearchEngineHandler.Response> _execute(HandlerContext<SearchEngineHandler.Request> context) {

        return run(context);
    }

    public static HandlerResponse<SearchEngineHandler.Response> run(HandlerContext<SearchEngineHandler.Request> context) {

        String query = context.getRequest().getQuery();

        InteractiveInfo interactiveInfo = InteractiveInfo.buildUrlCard("搜索新闻中[" + query + "]...").setInput(context.getRequest());
        context.sendCallbackInteractiveStart(interactiveInfo);

        SerpAPITool.Request request = new SerpAPITool.Request();
        request.setQ(query);
        request.setGl(SerpAPITool.GL);
        request.setHl(SerpAPITool.HL);


        List<SerpAPITool.SearchInfoDetail> searchInfoDetails = serpAPITool.runGetNews(request);


        HandlerResponse<SearchEngineHandler.Response> handlerResponse = new HandlerResponse();

        handlerResponse.setSuccess(true);

        List<InteractiveData> dataList = Optional.ofNullable(searchInfoDetails).orElse(new ArrayList<>()).stream().limit(3).map(detail -> {

            InteractiveData interactiveData = new InteractiveData();

            interactiveData.setTitle(detail.getTitle());
            interactiveData.setContent(detail.getContent());
            interactiveData.setUrl(detail.getLink());
            interactiveData.setTime(detail.getTime());

            return interactiveData;

        }).collect(Collectors.toList());

        handlerResponse.setOutput(new SearchEngineHandler.Response(dataList));
        handlerResponse.setExt(dataList);

        interactiveInfo.setData(dataList);
        interactiveInfo.setTips("查询完成");
        context.sendCallbackInteractiveEnd(interactiveInfo);

        return handlerResponse;
    }


    /**
     * 包装为文档结构
     */
    @Override
    protected List<MessageContentDocDTO> convertContentDoc(HandlerContext<SearchEngineHandler.Request> context, HandlerResponse<SearchEngineHandler.Response> handlerResponse) {

        //解析返回的内容 生成 MessageContentDocDTO

        List<InteractiveData> interactiveDataList = (List<InteractiveData>) handlerResponse.getExt();

        return Optional.ofNullable(interactiveDataList).orElse(new ArrayList<>()).stream().map((interactiveData) -> {

            MessageContentDocDTO messageContentDocDTO = new MessageContentDocDTO();

            messageContentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name());
            messageContentDocDTO.setTitle(interactiveData.getTitle());
            messageContentDocDTO.setContent(interactiveData.getContent());
            messageContentDocDTO.setUrl(interactiveData.getUrl());

            return messageContentDocDTO;

        }).collect(Collectors.toList());
    }


//    @Data
//    public static class Response {
//
//        private List<InteractiveData> news;
//
//        public Response(List<InteractiveData> images) {
//            this.news = images;
//        }
//    }

}
