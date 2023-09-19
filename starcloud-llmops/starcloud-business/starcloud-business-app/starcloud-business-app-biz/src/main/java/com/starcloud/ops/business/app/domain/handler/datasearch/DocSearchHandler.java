package com.starcloud.ops.business.app.domain.handler.datasearch;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.util.PromptUtil;
import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import com.starcloud.ops.business.dataset.pojo.request.MatchByDocIdRequest;
import com.starcloud.ops.business.dataset.pojo.response.MatchQueryVO;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 根据上传的文档向量搜索内容
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@Component
public class DocSearchHandler extends BaseToolHandler<DocSearchHandler.Request, DocSearchHandler.Response> {

    private DatasetSourceDataService datasetSourceDataService = SpringUtil.getBean(DatasetSourceDataService.class);

    private DocumentSegmentsService documentSegmentsService = SpringUtil.getBean(DocumentSegmentsService.class);

    private String userName = "文档内容搜索";

    private String userDescription = "可以对已经上传后的文档进行自然语言的搜索";

    private String name = "DocSearchHandler";


    private String icon = "ContentPasteSearch";

    /**
     * 工具名称
     */
    private String toolName = "Document Content Search";


    /**
     * 工具描述
     */
    private String toolDescription = "This tool is designed to help you quickly search for desired content within a large collection of documents.\n";

    /**
     * 使用方法
     */
    private String toolInstructions = "1. Enter the search query: Type in the content query you wish to search for in the input box.\n" +
            "2. Input context document ID: Provide a known document ID to assist the tool in understanding your query better.\n";


    /**
     * 结果解释
     * "The tool will return a JSON string array, where each array item contains four key-value pairs:\n" +
     */
    private String interpretingResults =
            "- \"docId\": Document ID, identifying the document where the content was found.\n" +
                    "- \"blockId\": Block ID, identifying the document block containing the matching content.\n" +
                    "- \"position\": Position of the document block within the document, aiding in locating the matching content.\n" +
                    "- \"content\": Extracted partial content, showcasing the content of the matching document block.\n";


    /**
     * 示例输入
     */
    private String exampleInput = "query: \"About Product Features\"\n" +
            "Document ID collection: 12\n";


    /**
     * 示例输出
     */
    private String exampleOutput = "[\n" +
            "  {\n" +
            "    \"docId\": 12,\n" +
            "    \"blockId\": \"block234\",\n" +
            "    \"position\": 125,\n" +
            "    \"content\": \"This article discusses the product features and advantages.\"\n" +
            "  }\n" +
            "  // More matching items...\n" +
            "]";


    /**
     * 注意
     */
    private String note =
            "- Context document ID collection enhances search precision and accuracy.\n" +
                    "- Adjust the search query and document collection as needed to meet your information requirements.";


    /**
     * 查询文档内容相似度的阈值
     */
    private Double maxScore = 0.7d;


    @Override
    public Boolean isAddHistory() {
        return false;
    }

    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {

        String query = context.getRequest().getQuery();

        //@todo 通过上下文获取当前可能配置的 tools 执行 tips
        InteractiveInfo interactiveInfo = InteractiveInfo.buildTips("内容搜索中[" + query + "]...").setShowType("docs").setToolHandler(this).setInput(context.getRequest());

        context.sendCallbackInteractiveStart(interactiveInfo);


        //查询出文档列表
        List<RecordDTO> records = this.searchDocs(context.getRequest(), context.getUserId());


        HandlerResponse<Response> handlerResponse = new HandlerResponse();
        handlerResponse.setSuccess(false);
        handlerResponse.setMessage(JSONUtil.toJsonStr(context.getRequest()));

        Response result = new Response();

        List<PromptUtil.PromptDocBlock> docBlocks = Optional.ofNullable(records).orElse(new ArrayList<>()).stream().map(recordDTO -> {

            PromptUtil.PromptDocBlock promptDocBlock = new PromptUtil.PromptDocBlock();
            promptDocBlock.setDocId(Long.valueOf(recordDTO.getDocumentId()));
            promptDocBlock.setBlockId(recordDTO.getSegmentId());
            promptDocBlock.setPosition(recordDTO.getPosition());
            promptDocBlock.setContent(recordDTO.getContent());

            return promptDocBlock;

        }).collect(Collectors.toList());
        result.setDocs(docBlocks);

        handlerResponse.setSuccess(true);
        handlerResponse.setOutput(result);

        interactiveInfo.setData(docBlocks);
        context.sendCallbackInteractiveEnd(interactiveInfo);

        return handlerResponse;
    }

    private List<RecordDTO> searchDocs(Request request, Long userId) {

        //数据集，可能只要文档ID即可
        MatchByDocIdRequest matchByDocIdRequest = new MatchByDocIdRequest();
        matchByDocIdRequest.setDocId(Arrays.asList(request.getDocId()));
        matchByDocIdRequest.setText(request.getQuery());
        matchByDocIdRequest.setK(3L);
        matchByDocIdRequest.setUserId(userId);

        //@todo 文档ID列表

        MatchQueryVO matchQueryVO = documentSegmentsService.matchQuery(matchByDocIdRequest);


        //过滤掉 分数低的 < 0.7  文档搜索相似度阈值
        //this.maxScore;
        if (matchQueryVO != null && CollectionUtil.isNotEmpty(matchQueryVO.getRecords())) {

        }

        return matchQueryVO.getRecords();
    }

    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("documents id")
        private Long docId;

        @JsonProperty(required = true)
        @JsonPropertyDescription("search documents query")
        private String query;

        @JsonProperty(required = false)
        @JsonPropertyDescription("Content search or complete content fetching.")
        private String type;

    }


    @Data
    public static class Response implements Serializable {

        private List<PromptUtil.PromptDocBlock> docs;
    }

}
