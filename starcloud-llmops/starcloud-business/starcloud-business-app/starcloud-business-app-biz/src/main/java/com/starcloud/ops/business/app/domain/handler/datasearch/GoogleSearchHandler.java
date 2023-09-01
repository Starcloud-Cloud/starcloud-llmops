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
import kong.unirest.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * google 搜索内容，只返回 主要的结果摘要
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@Component
public class GoogleSearchHandler extends BaseToolHandler<SearchEngineHandler.Request, SearchEngineHandler.Response> {

    private String userName = "互联网搜索";

    private String userDescription = "可以自动联网查询实时信息，保证内容实时准确";

    private String name = "GoogleSearchHandler";

    private String description = "A search engine. Useful for when you need to answer questions about current events. Input should be a search query.";

    private static SerpAPITool serpAPITool = new SerpAPITool();

    @Override
    protected HandlerResponse<SearchEngineHandler.Response> _execute(HandlerContext<SearchEngineHandler.Request> context) {

        return run(context);
    }


    public static HandlerResponse<SearchEngineHandler.Response> run(HandlerContext<SearchEngineHandler.Request> context) {

        String query = context.getRequest().getQuery();

        SerpAPITool.Request request = new SerpAPITool.Request();
        request.setQ(query);
        request.setGl(SerpAPITool.GL);
        request.setHl(SerpAPITool.HL);

        InteractiveInfo interactiveInfo = InteractiveInfo.buildUrlCard("联网搜索中[" + query + "]...").setInput(context.getRequest());

        context.sendCallbackInteractiveStart(interactiveInfo);

        List<SerpAPITool.SearchInfoDetail> searchInfoDetails = serpAPITool.runGetInfo(request);

        //String content = serpAPITool.processResponseStr(searchInfoDetails);

        HandlerResponse<SearchEngineHandler.Response> handlerResponse = new HandlerResponse();

        handlerResponse.setSuccess(true);

        List<InteractiveData> dataList = processFilter(searchInfoDetails);

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
            messageContentDocDTO.setTime(interactiveData.getTime());

            return messageContentDocDTO;

        }).collect(Collectors.toList());
    }


    /**
     * 过滤掉无用的
     *
     * @param searchInfoDetails
     * @return
     */
    private static List<InteractiveData> processFilter(List<SerpAPITool.SearchInfoDetail> searchInfoDetails) {

        List<InteractiveData> interactiveDataList = new ArrayList<>();

        Map<String, List<SerpAPITool.SearchInfoDetail>> maps = Optional.ofNullable(searchInfoDetails).orElse(new ArrayList<>()).stream().collect(Collectors.groupingBy(SerpAPITool.SearchInfoDetail::getType));

        //为了结果丰富全面，每个结果类型取1个
        for (Map.Entry<String, List<SerpAPITool.SearchInfoDetail>> entry : maps.entrySet()) {

            String type = entry.getKey();
            //暂时支持取这2种结果
            if ("answerBox".equals(type) || "organic".equals(type)) {

                SerpAPITool.SearchInfoDetail searchInfoDetail = entry.getValue().get(0);
                if (searchInfoDetail != null) {

                    InteractiveData interactiveData = new InteractiveData();

                    interactiveData.setTitle(searchInfoDetail.getTitle());
                    interactiveData.setContent(searchInfoDetail.getContent());
                    interactiveData.setUrl(searchInfoDetail.getLink());
                    interactiveData.setTime(searchInfoDetail.getTime());

                    interactiveDataList.add(interactiveData);
                }
            }
        }

        return interactiveDataList;
    }

//
//    @Data
//    public static class Response {
//
//        private String content;
//
//        public Response(String content) {
//            this.content = content;
//        }
//    }

}
