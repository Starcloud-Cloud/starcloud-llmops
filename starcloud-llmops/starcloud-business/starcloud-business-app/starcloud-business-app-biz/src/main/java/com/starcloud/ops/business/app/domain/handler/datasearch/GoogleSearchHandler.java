package com.starcloud.ops.business.app.domain.handler.datasearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
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
public class GoogleSearchHandler extends BaseToolHandler<GoogleSearchHandler.Request, GoogleSearchHandler.Response> {

    private String userName = "互联网搜索";

    private String userDescription = "可以自动联网查询实时信息，保证内容实时准确";

    private String name = "GoogleSearchHandler";

    private String description = "A search engine. Useful for when you need to answer questions about current events. Input should be a search query.";

    private static SerpAPITool serpAPITool = new SerpAPITool();

    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {

        String query = context.getRequest().getQ();

        SerpAPITool.Request request = new SerpAPITool.Request();
        request.setQ(query);

        InteractiveInfo interactiveInfo = InteractiveInfo.buildTips("联网查询[" + query + "]中...").setInput(context.getRequest());

        context.sendCallbackInteractiveStart(interactiveInfo);


        List<SerpAPITool.SearchInfoDetail> searchInfoDetails = serpAPITool.runGetInfo(request);

        String content = serpAPITool.processResponseStr(searchInfoDetails);

        HandlerResponse<Response> handlerResponse = new HandlerResponse();

        handlerResponse.setSuccess(true);
        handlerResponse.setOutput(new Response(content));

        handlerResponse.setExt(searchInfoDetails);


        interactiveInfo.setData(searchInfoDetails);
        interactiveInfo.setTips("查询完成");
        context.sendCallbackInteractiveEnd(interactiveInfo);

        return handlerResponse;
    }


    /**
     * 包装为文档结构
     */
    @Override
    protected List<MessageContentDocDTO> convertContentDoc(HandlerContext<Request> context, HandlerResponse<Response> handlerResponse) {

        //解析返回的内容 生成 MessageContentDocDTO
        List<MessageContentDocDTO> messageContentDocDTOList = new ArrayList<>();

        Request request = context.getRequest();
        List<SerpAPITool.SearchInfoDetail> searchInfoDetails = (List<SerpAPITool.SearchInfoDetail>) handlerResponse.getExt();

        Map<String, List<SerpAPITool.SearchInfoDetail>> maps = Optional.ofNullable(searchInfoDetails).orElse(new ArrayList<>()).stream().collect(Collectors.groupingBy(SerpAPITool.SearchInfoDetail::getType));

        //为了结果丰富全面，每个结果类型取1个
        for (Map.Entry<String, List<SerpAPITool.SearchInfoDetail>> entry : maps.entrySet()) {

            String type = entry.getKey();
            //暂时支持取这2种结果
            if ("answerBox".equals(type) || "organic".equals(type)) {

                SerpAPITool.SearchInfoDetail searchInfoDetail = entry.getValue().get(0);
                if (searchInfoDetail != null) {
                    MessageContentDocDTO messageContentDocDTO = new MessageContentDocDTO();

                    messageContentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name());
                    messageContentDocDTO.setUrl(searchInfoDetail.getLink());
                    messageContentDocDTO.setTime(searchInfoDetail.getTime());
                    messageContentDocDTO.setTitle(searchInfoDetail.getTitle());
                    messageContentDocDTO.setContent(searchInfoDetail.getContent());

                    //@todo 2Map
                    messageContentDocDTO.setExt(searchInfoDetail);

                    messageContentDocDTOList.add(messageContentDocDTO);
                }
            }
        }

        return messageContentDocDTOList;
    }


    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("Parameter defines the query you want to search.")
        private String q;

    }


    @Data
    public static class Response {

        private String content;

        public Response(String content) {
            this.content = content;
        }
    }

}
