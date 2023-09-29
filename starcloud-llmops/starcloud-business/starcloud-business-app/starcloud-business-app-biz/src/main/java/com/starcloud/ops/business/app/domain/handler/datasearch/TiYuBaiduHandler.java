package com.starcloud.ops.business.app.domain.handler.datasearch;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 百度的体育新闻搜索
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@Component
public class TiYuBaiduHandler extends BaseToolHandler<TiYuBaiduHandler.Request, TiYuBaiduHandler.Response> {

    private String userName = "体育新闻";

    private String userDescription = "可以自动联网查询实时的体育新闻，保证内容实时准确";

    private String name = "TiYuBaiduHandler";

    private String description = "A search engine. Useful for when you need to answer questions about current sports events. Input should be a search query.";

    private String usage = "1.今天亚运会有什么比赛\n" +
            "2.亚运会男足最近比分多少";


    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {

        return run(context);
    }


    public static HandlerResponse<Response> run(HandlerContext<Request> context) {

        String query = context.getRequest().getQuery();

        InteractiveInfo interactiveInfo = InteractiveInfo.buildImgCard("联网搜索体育新闻中[" + query + "]...").setInput(context.getRequest());

        context.sendCallbackInteractiveStart(interactiveInfo);

        HashMap params = new HashMap();
        params.put("pn", 1);
        params.put("word", query);

        String result2 = HttpUtil.get("https://tiyu.baidu.com/api/realtime", params);

        HttpRequest httpRequest = HttpUtil.createGet("https://tiyu.baidu.com/api/realtime");
        httpRequest.setUrl(UrlBuilder.of("https://tiyu.baidu.com/api/realtime").setQuery(UrlQuery.of(params)));

        HttpResponse httpResponse = httpRequest.execute();

        JSONObject result = JSONUtil.toBean(httpResponse.body(), JSONObject.class);

        List<InteractiveData> dataList = new ArrayList<>();

        result.getJSONObject("data").getJSONArray("data").toList(JSONObject.class).stream().limit(context.getRequest().getSize()).forEach(data -> {

            InteractiveData interactiveData = new InteractiveData();

            List<String> imgs = data.getBeanList("img", String.class);

            if (CollectionUtil.isNotEmpty(imgs)) {
                interactiveData.setImageUrl(imgs.get(0));
            }

            interactiveData.setTitle(data.getStr("title"));
            interactiveData.setUrl(data.getStr("link"));
            interactiveData.setTime(DateUtil.formatDateTime(DateUtil.date(Long.valueOf(data.getStr("factorTime")) * 1000).toJdkDate()));

            dataList.add(interactiveData);
        });


        //String content = serpAPITool.processResponseStr(searchInfoDetails);

        HandlerResponse<Response> handlerResponse = new HandlerResponse();

        handlerResponse.setSuccess(true);
        handlerResponse.setOutput(new Response(dataList));
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
    public List<MessageContentDocDTO> convertContentDoc(HandlerContext<Request> context, HandlerResponse<Response> handlerResponse) {

        //解析返回的内容 生成 MessageContentDocDTO

        List<InteractiveData> interactiveDataList = (List<InteractiveData>) handlerResponse.getExt();

        return Optional.ofNullable(interactiveDataList).orElse(new ArrayList<>()).stream().map((interactiveData) -> {

            //Google返回的推荐结果，没有URL，内容较少，所以也不存在上下文中，直接放在message结果中即可。
            if (StrUtil.isBlank(interactiveData.getUrl())) {
                return null;
            }
            MessageContentDocDTO messageContentDocDTO = new MessageContentDocDTO();

            messageContentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name());
            messageContentDocDTO.setTitle(interactiveData.getTitle());
            messageContentDocDTO.setContent(interactiveData.getContent());
            messageContentDocDTO.setUrl(interactiveData.getUrl());

            if (StrUtil.isNotBlank(interactiveData.getTime())) {
                messageContentDocDTO.setTime(interactiveData.getTime());
            }

            return messageContentDocDTO;

        }).filter(Objects::nonNull).collect(Collectors.toList());
    }


    @Data
    public static class Request {

        private Integer size = 4;

        @JsonProperty(required = true)
        @JsonPropertyDescription("Parameter defines the query you want to search for the sports event.")
        private String query;

    }

    @Data
    public static class Response {

        private List<InteractiveData> response;

        public Response(List<InteractiveData> response) {
            this.response = response;
        }


    }

}
