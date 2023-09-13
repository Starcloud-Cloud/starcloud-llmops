package com.starcloud.ops.business.app.domain.handler.datasearch;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonValue;
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
public class SearchEngineHandler extends BaseToolHandler<SearchEngineHandler.Request, SearchEngineHandler.Response> {

    private String userName = "互联网搜索";

    private String userDescription = "可以自动联网查询实时信息，查询内容,新闻和图片";

    private String name = "SearchEngineHandler";

    private String description = "A search engine. Useful for when you need to answer questions about current events. Input should be a search query.";


    /**
     * 工具名称
     */
    private String toolName = "Internet Content Search";

    /**
     * 工具描述
     */
    private String toolDescription = "The Internet Content Search tool is designed to assist you in searching for specific content within various online documents. This tool supports searching for information, news, and images. To use the tool, provide a search query (query) and specify the type of search (type). The available search types are: image (for image-based searches), content (for general content searches), and news (for news-related searches).";

    /**
     * 使用方法
     */
    private String toolInstructions = "1. Enter the search query: Type in the query you wish to search for in the input box.\n" +
            "2. Specify search type: Choose the appropriate search type from the options: \"image\", \"content\", or \"news\".";


    /**
     * 结果解释
     */
    private String interpretingResults = "- title: The content title or image title.\n" +
            "- imageUrl: The address of the image.\n" +
            "- url: The address of the network content.\n" +
            "- content: Part of the network content interception part.\n" +
            "- time: The generation of content.";


    /**
     * 示例输入
     */
    private String exampleInput = "query: \"Artificial Intelligence\"\n" +
            "type: \"news\"";


    /**
     * 示例输出
     */
    private String exampleOutput;


    /**
     * 注意
     */
    private String note = "- The provided tool helps you search for content across the internet.\n" +
            "- The output is presented in a JSON format, containing an array of content information.\n" +
            "- Each content information object includes the following keys: \"title\", \"imageUrl\" (available for \"image\" search type), \"url\", \"content\", and \"time\"";


    private static SerpAPITool serpAPITool = new SerpAPITool();

    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {

        String query = context.getRequest().getQuery();
        String type = context.getRequest().getType();

        if ("image".equals(type)) {

            log.info("SearchEngineHandler start: {}", context);
            return ImageSearchHandler.run(context);

        } else if ("news".equals(type)) {
            log.info("SearchEngineHandler start: {}", context);
            return NewsSearchHandler.run(context);

        } else {
            log.info("SearchEngineHandler start: {}", context);
            return GoogleSearchHandler.run(context);
        }

    }


    /**
     * 包装为文档结构
     */
    @Override
    protected List<MessageContentDocDTO> convertContentDoc(HandlerContext<Request> context, HandlerResponse<Response> handlerResponse) {

        //解析返回的内容 生成 MessageContentDocDTO

        List<InteractiveData> interactiveDataList = (List<InteractiveData>) handlerResponse.getExt();

        return Optional.ofNullable(interactiveDataList).orElse(new ArrayList<>()).stream().map((interactiveData) -> {

            MessageContentDocDTO messageContentDocDTO = new MessageContentDocDTO();

            messageContentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name());
            messageContentDocDTO.setTitle(interactiveData.getTitle());
            messageContentDocDTO.setContent(interactiveData.getContent());
            messageContentDocDTO.setUrl(interactiveData.getUrl());
            messageContentDocDTO.setTime(LocalDateTimeUtil.parse(interactiveData.getTime()));

            return messageContentDocDTO;

        }).collect(Collectors.toList());
    }


    /**
     * 过滤掉无用的
     *
     * @param searchInfoDetails
     * @return
     */
    private List<InteractiveData> processFilter(List<SerpAPITool.SearchInfoDetail> searchInfoDetails) {

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


    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("Parameter defines the query you want to search.")
        private String query;


        @JsonProperty(required = true)
        @JsonPropertyDescription("Specify search type: \"image\", \"content\", or \"news\".")
        private String type;

    }


    @Data
    public static class Response {

        private List<InteractiveData> response;

        public Response(List<InteractiveData> response) {
            this.response = response;
        }
    }

}
