package com.starcloud.ops.business.app.domain.entity.chat.prompts;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.DatesetEntity;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.entity.chat.WebSearchConfigEntity;
import com.starcloud.ops.business.app.domain.entity.skill.HandlerSkill;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.datasearch.SearchEngineHandler;
import com.starcloud.ops.business.app.domain.handler.datasearch.TiYuBaiduHandler;
import com.starcloud.ops.business.app.service.chat.momory.MessageContentDocHistory;
import com.starcloud.ops.business.app.service.chat.momory.MessageContentDocMemory;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.business.app.util.PromptUtil;
import com.starcloud.ops.business.app.util.SseResultUtil;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataBasicInfoVO;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.pojo.request.MatchByDataSetIdRequest;
import com.starcloud.ops.business.dataset.pojo.response.MatchQueryVO;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 上下文 prompt
 */
@Slf4j
@Data
public class ContextPrompt extends BasePromptConfig {

    private static DocumentSegmentsService documentSegmentsService = SpringUtil.getBean(DocumentSegmentsService.class);

    private static DatasetSourceDataService datasetSourceDataService = SpringUtil.getBean(DatasetSourceDataService.class);


    private String promptV1 = "The content in [CONTEXT] is multi-line, and each line represents the structure of the document block. It contains the `{N}` and the document block information in JSON format, and the JSON format contains fields `blockId` block ID, `content` block content.\n" +
            "[Block EXAMPLE]:\n" +
            "1. {\"docId\":\"20\",\"blockId\":\"12\",\"content\":\"如何销售图书 定价 Back 定价 亚马逊开店成本\"}\n" +
            "2. {\"docId\":\"25\",\"blockId\":\"33\",\"content\":\"在亚马逊，超过一半的实际商品销售总额来自独立的第三方卖家\"}\n" +
            "......\n" +
            "[END]\n" +
            "Use the following [CONTEXT] as your learned knowledge:\n" +
            "[CONTEXT]\n" +
            "{context}\n" +
            "{contextDoc}\n" +
            "[END CONTEXT]\n" +
            "Note When answer to user:\n" +
            "- If you don't know, just say that you don't know.\n" +
            "- If you don't know when you are not sure, ask for clarification.\n" +
            "- Avoid mentioning that you obtained the information from the context.\n" +
            "- If the content of the answer refers to the content of the block in CONTEXT, you need to add the `{N}` of the referenced block at the end of the relevant sentence, like this `{1}` with braces.\n\n";

    private String promptV2 = "Context Interpreting:\n" +
            "The content in [CONTEXT] is multi-line, and each line represents the structure of the document block. It contains the Serial number and the Document block information in JSON format.\n" +
            "Document Block Interpreting:\n" +
            "- n: The content of the serial number.\n" +
            "- id: Document ID, identifying the document where the content was found.\n" +
            "- type: The source type of the content. Contains a `WEB` from WEB content, the `FILE` from the file content, `TOOL` from the result of the code execution.\n" +
            "- title: The title of the content.\n" +
            "- url: From the network address of the web content.\n" +
            "- content: Extracted partial content.\n" +
            "- toolName: The name of the Tool, which has a value when the type is `TOOL`.\n" +

            "[EXAMPLE]:\n" +
            "{\"n\": 1, \"id\":\"20\",\"type\":\"WEB\",\"url\": \"https://sell.amazon.com/learn/inventory-management\", \"title\": \"2023跨境电商注册开店_怎么开网店_跨境新手指南_Amazon亚马逊\", \"content\":\"如何销售图书 定价 Back 定价 亚马逊开店成本\"}\n" +
            "{\"n\": 2, \"id\":\"25\",\"type\":\"FILE\", \"title\": \"新手指南： 如何在亚马逊开店\", \"content\":\"在亚马逊，超过一半的实际商品销售总额来自独立的第三方卖家\"}\n" +
            "......\n" +
            "[END EXAMPLE]\n\n" +

            "Use the following [CONTEXT] as your learned knowledge:\n" +
            "[CONTEXT]\n" +
            "{contextDoc}\n" +
            "[END CONTEXT]\n" +
            "Note When answer:\n" +
            "- If you don't know, just say that you don't know!!!\n" +
            "- If you don't know when you are not sure, ask for clarification!!!\n" +
            "- Avoid mentioning that you obtained the information from the context.\n" +
            "- If the content of the answer refers to the content of the block in [CONTEXT], you need to add the `{n}` of the referenced block at the end of the relevant sentence, like this `{1}` with braces.\n";


    private ChatRequestVO chatRequestVO;

    private String query;

    private ChatConfigEntity chatConfig;

    private List<DatesetEntity> datesetEntities;

    /**
     * 文档到搜索结果
     */
    private MatchQueryVO searchResult;

    /**
     * Google搜索标记
     */
    private Boolean googleSearchStatus;


    /**
     * 上下文文档历史
     */
    private MessageContentDocMemory messageContentDocMemory;

    /**
     * handler调用上下文
     */
    private HandlerContext handlerContext;


    public ContextPrompt(ChatConfigEntity chatConfig, ChatRequestVO chatRequestVO, MessageContentDocMemory messageContentDocMemory, HandlerContext handlerContext) {
        this.chatRequestVO = chatRequestVO;
        this.query = chatRequestVO.getQuery();
        this.chatConfig = chatConfig;
        this.datesetEntities = chatConfig.getDatesetEntities();
        this.messageContentDocMemory = messageContentDocMemory;
        this.handlerContext = handlerContext;
    }


    @Override
    protected Boolean _isEnable() {

        if (this.canSearchDataset()) {
            this.searchResult = this.searchDataset(this.query);
        }

        //文档搜索过并不为空
        if (this.searchResult != null && CollectionUtil.isNotEmpty(this.searchResult.getRecords())) {
            return true;
        }

        //文档查询不为空 就不联网查询了
        if (this.canSearchWeb()) {
            this.googleSearchStatus = this.googleSearch(this.query);
        }

        //网络搜索过并为true
        if (Boolean.TRUE.equals(this.googleSearchStatus)) {
            return true;
        }


        return false;
    }


    private Boolean canSearchDataset() {
        return false;
    }

    private Boolean canSearchWeb() {
        return Optional.ofNullable(this.chatConfig.getWebSearchConfig()).map(WebSearchConfigEntity::getEnabled).orElse(false);
    }


    /**
     * 发送所有文档内容到前端，让前端转换为。实现"内容来源"功能
     * 1，搜索到的文档（通过 向量搜索出来的文档块内容）
     * 2，上下文内容（根据当前联网内容获取到的描述内容，LLM通过上下文自行选出的）
     */
    public void sendContextInteractive(List<MessageContentDocDTO> messageContentDocDTOS) {

        if (CollectionUtil.isNotEmpty(messageContentDocDTOS)) {

            InteractiveInfo interactiveInfo = InteractiveInfo.buildMessageContent(messageContentDocDTOS);

            ChatRequestVO request = this.getChatRequestVO();
            SseResultUtil.builder().sseEmitter(request.getSseEmitter()).conversationUid(request.getConversationUid()).build().sendCallbackInteractive("context", interactiveInfo);
        }
    }


    @Override
    protected PromptTemplate _buildPrompt() {


        //@todo 处理 上下文中动态增加的文档历史
        BaseVariable contextDoc = BaseVariable.newFun("contextDoc", () -> {

            List<MessageContentDocDTO> sortResult = this.loadMessageContentDoc();

            //发送到前端
            this.sendContextInteractive(sortResult);

            return PromptUtil.parseDocContentLines(sortResult);
        });

        return PromptTemplate.fromTemplate(() -> {

            if (this.isEnable()) {
                return this.promptV2;
            }
            return null;
        }, Arrays.asList(contextDoc));

    }


    /**
     * 加载所有 上下文的文档内容
     * 1，文档搜索
     * 2，工具执行结果
     * <p>
     *
     * @todo 后续优化流程
     * * 1，搜索当前会话 和 机器人的 数据集内容，向量搜索（会有还未向量的）
     * * 2，把还未向量的文档+描述 ，直接叠加到 上下文中
     * * 3，最后 放到 prompt中
     */
    private List<MessageContentDocDTO> loadMessageContentDoc() {

        List<MessageContentDocDTO> sortResult = new ArrayList<>();
        try {
            //数据集搜索
            List<MessageContentDocDTO> searchResult = this.parseContent(this.searchResult);
            sortResult.addAll(searchResult);

            log.info("ContextPrompt loadMessageContentDoc dataSet result:{}", JsonUtils.toJsonString(searchResult));

            //直接查询当前上下文内容
            MessageContentDocHistory contentDocHistory = this.getMessageContentDocMemory().reloadHistory();

            //@todo 对于重复的文档内容，需要过滤掉
            List<MessageContentDocDTO> docsHistory = Optional.ofNullable(contentDocHistory.getDocs()).orElse(new ArrayList<>()).stream().filter(docDTO -> {
                //@todo 如果内容为空可能是只有标题的情况，主要是作为文档去显示提示意义不大
                return StrUtil.isNotBlank(docDTO.getContent()) && !MessageContentDocDTO.MessageContentDocTypeEnum.TOOL.equals(docDTO.getType());
                //数据太多，只能先取前5条
            }).limit(3).collect(Collectors.toList());

            sortResult.addAll(docsHistory);

            log.info("ContextPrompt loadMessageContentDoc ContentDoc result:{}", JsonUtils.toJsonString(docsHistory));

        } catch (Exception e) {

            //查询异常，就放弃上下文了
            log.error("loadMessageContentDoc is error: {}", e.getMessage(), e);
        }


        return sortResult;
    }


    /**
     * Google内容搜索
     * 调用 handler
     *
     * @param query
     * @return
     */
    protected Boolean googleSearch(String query) {

        if (StrUtil.isNotBlank(query) && this.googleSearchStatus == null) {

            HandlerSkill handlerSkill = HandlerSkill.of("GoogleSearchHandler");
            handlerSkill.setMessageContentDocMemory(this.getMessageContentDocMemory());

            //只增加但不保存 上下文
            handlerSkill.setHistoryStrategy(true, false);

            SearchEngineHandler.Request request = new SearchEngineHandler.Request();
            request.setSize(2);
            request.setType("content");
            request.setQuery(query);
            this.handlerContext.setRequest(request);

            //执行并增加结果到上下文
            HandlerResponse handlerResponse = handlerSkill.execute(this.handlerContext);

            log.info("ContextPrompt googleSearch status: {}, {}: {}", handlerResponse.getSuccess(), query, JsonUtils.toJsonString(handlerResponse.getOutput()));

            return handlerResponse.getSuccess();
        }

        return this.googleSearchStatus;
    }


    /**
     * Google内容搜索
     * 调用 handler
     *
     * @param query
     * @return
     */
    protected Boolean tiyuSearch(String query) {

        HandlerSkill handlerSkill = HandlerSkill.of("TiYuBaiduHandler");
        handlerSkill.setMessageContentDocMemory(this.getMessageContentDocMemory());

        //只增加但不保存 上下文
        handlerSkill.setHistoryStrategy(true, false);

        TiYuBaiduHandler.Request request = new TiYuBaiduHandler.Request();
        request.setSize(4);
        request.setQuery(query);
        this.handlerContext.setRequest(request);

        //执行并增加结果到上下文
        HandlerResponse handlerResponse = handlerSkill.execute(this.handlerContext);

        log.info("ContextPrompt tiyuSearch status: {}, {}: {}", handlerResponse.getSuccess(), query, JsonUtils.toJsonString(handlerResponse.getOutput()));

        return handlerResponse.getSuccess();
    }


    /**
     * 数据集查询文档内容
     *
     * @param query
     * @return
     */
    private MatchQueryVO searchDataset(String query) {

        if (StrUtil.isNotBlank(query) && this.searchResult == null) {

            try {

                //可接收其他数据集的传入
                List<String> datasetUid = Optional.ofNullable(this.getDatesetEntities()).orElse(new ArrayList<>())
                        .stream().filter(DatesetEntity::getEnabled).map(DatesetEntity::getDatasetUid).collect(Collectors.toList());

                //@todo 需要 block 对象
                MatchByDataSetIdRequest matchQueryRequest = new MatchByDataSetIdRequest();
                matchQueryRequest.setText(query);
                matchQueryRequest.setK(3L);
                matchQueryRequest.setAppId(datasetUid);
                matchQueryRequest.setMinScore(0.72d);
                matchQueryRequest.setUserId(chatRequestVO.getUserId());

                MatchQueryVO matchQueryVO = documentSegmentsService.matchQuery(matchQueryRequest);

                log.info("ContextPrompt searchDataset: {}, {}", JsonUtils.toJsonString(matchQueryRequest), JsonUtils.toJsonString(matchQueryVO));

                return matchQueryVO;

            } catch (Exception e) {

                log.error("ContextPrompt searchDataset is error: {}", e.getMessage(), e);

                //如果搜索有问题，就当没有搜索结果
                return MatchQueryVO.builder().records(new ArrayList<>()).build();
            }
        }

        return this.searchResult;
    }

    private List<MessageContentDocDTO> parseContent(MatchQueryVO matchQueryVO) {

        List<Long> docIds = Optional.ofNullable(matchQueryVO).map(MatchQueryVO::getRecords).orElse(new ArrayList<>()).stream().map(d -> Long.valueOf(d.getDocumentId())).collect(Collectors.toList());

        List<DatasetSourceDataBasicInfoVO> docs = datasetSourceDataService.getSourceDataListData(docIds);

        List<MessageContentDocDTO> messageContentDocDTOList = Optional.ofNullable(matchQueryVO).map(MatchQueryVO::getRecords).orElse(new ArrayList<>()).stream().map(recordDTO -> {

            DatasetSourceDataBasicInfoVO doc = Optional.ofNullable(docs).orElse(new ArrayList<>()).stream().filter(docVo -> docVo.getId().equals(Long.valueOf(recordDTO.getDocumentId()))).findFirst().orElse(null);

            if (doc != null) {
                MessageContentDocDTO contentDocDTO = new MessageContentDocDTO();

                contentDocDTO.setId(Long.valueOf(recordDTO.getDocumentId()));

                DataSourceDataTypeEnum sourceDataTypeEnum = DataSourceDataTypeEnum.valueOf(doc.getDataType());

                if (sourceDataTypeEnum.equals(DataSourceDataTypeEnum.HTML)) {
                    contentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.WEB.name());
                } else {
                    contentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.FILE.name());
                }

                contentDocDTO.setTitle(doc.getName());
                contentDocDTO.setUrl(doc.getAddress());
                contentDocDTO.setContent(recordDTO.getContent());
                contentDocDTO.setTime(doc.getCreateTime().toString());

                //扩展信息
                Map ext = new HashMap() {{
                    put("position", recordDTO.getPosition());
                    put("score", recordDTO.getScore());
                }};
                contentDocDTO.setExt(ext);

                return contentDocDTO;
            }

            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());


        return messageContentDocDTOList;
    }


}
