package com.starcloud.ops.business.app.domain.entity.chat.prompts;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.chat.DatesetEntity;
import com.starcloud.ops.business.app.service.chat.momory.MessageContentDocHistory;
import com.starcloud.ops.business.app.service.chat.momory.MessageContentDocMemory;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.business.app.util.PromptUtil;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataBasicInfoVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataDetailsInfoVO;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import com.starcloud.ops.business.dataset.pojo.request.MatchQueryRequest;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
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
            "The content in [CONTEXT] is multi-line, and each line represents the structure of the document block. It contains the Serial number  and the Document block information in JSON format.\n" +
            "[json Interpreting]\n" +
            "- id: Document ID, identifying the document where the content was found.\n" +
            "- type: The source type of the content. Contains a `WEB` from WEB content, the `FILE` from the file content, `TOOL` from the result of the code execution.\n" +
            "- title: The title of the content.\n" +
            "- url: From the network address of the web content.\n" +
            "- content: Extracted partial content.\n" +
            "- toolName: The name of the Tool, which has a value when the type is `TOOL`.\n\n" +
            "[document block example]:\n" +
            "1. {\"id\":\"20\",\"type\":\"WEB\",\"url\": \"https://sell.amazon.com/learn/inventory-management\", \"title\": \"2023跨境电商注册开店_怎么开网店_跨境新手指南_Amazon亚马逊\", \"content\":\"如何销售图书 定价 Back 定价 亚马逊开店成本\"}\n" +
            "2. {\"id\":\"25\",\"type\":\"FILE\", \"title\": \"新手指南： 如何在亚马逊开店\", \"content\":\"在亚马逊，超过一半的实际商品销售总额来自独立的第三方卖家\"}\n" +
            "......\n" +
            "[end]\n\n" +
            "Use the following [CONTEXT] as your learned knowledge:\n" +
            "[CONTEXT]\n" +
            "{context}\n" +
            "{contextDoc}\n" +
            "[END CONTEXT]\n\n" +
            "Please Note:\n" +
            "- If you don't know, just say that you don't know.\n" +
            "- If you don't know when you are not sure, ask for clarification.\n" +
            "- Avoid mentioning that you obtained the information from the context.\n" +
            "- If the content of the answer refers to the content of the block in CONTEXT, you need to add the `{Serial number}` of the referenced block at the end of the relevant sentence, like this `{1}` with braces.\n\n";

    private String query;

    private List<DatesetEntity> datesetEntities;

    private MatchQueryVO searchResult;

    /**
     * 上下文文档历史
     */
    private MessageContentDocMemory messageContentDocMemory;


    public ContextPrompt(List<DatesetEntity> datesetEntities, String query, MessageContentDocMemory messageContentDocMemory) {
        this.query = query;
        this.datesetEntities = datesetEntities;
        this.messageContentDocMemory = messageContentDocMemory;
    }


    @Override
    protected Boolean _isEnable() {
        this.searchResult = this.searchDataset(this.query);

        //直接搜索 或 上下文文档
        if (this.getMessageContentDocMemory().hasHistory() || this.searchResult != null) {
            return true;
        }

        return false;
    }


    @Override
    protected PromptTemplate _buildPrompt() {


        //@todo 处理 上下文中动态增加的文档历史
        BaseVariable contextDoc = BaseVariable.newFun("contextDoc", () -> {

            if (this.getMessageContentDocMemory().hasHistory()) {

                MessageContentDocHistory contentDocHistory = this.getMessageContentDocMemory().getHistory();

                return PromptUtil.parseDocContentLines(contentDocHistory.getDocs());
            }

            return "";
        });

        String contentLines = this.parseContentLines(this.searchResult);


        BaseVariable variable = BaseVariable.newString("context", contentLines);

        return PromptTemplate.fromTemplate(() -> {

            if (this.isEnable()) {
                return this.promptV2;
            }
            return null;
        }, Arrays.asList(variable, contextDoc));

    }


    /**
     * 搜索对话上下文 文档
     */
    private void searchMessageContentDoc() {
        //从 Memory 中查询


    }

    /**
     * 数据集查询文档内容
     *
     * @param query
     * @return
     */
    private MatchQueryVO searchDataset(String query) {

        if (StrUtil.isNotBlank(query) && this.searchResult == null) {

            //可接收其他数据集的传入
            List<String> datasetUid = Optional.ofNullable(this.getDatesetEntities()).orElse(new ArrayList<>())
                    .stream().filter(DatesetEntity::getEnabled).map(DatesetEntity::getDatasetUid).collect(Collectors.toList());

            //@todo 需要 block 对象
            MatchQueryRequest matchQueryRequest = new MatchQueryRequest();
            matchQueryRequest.setText(query);
            matchQueryRequest.setK(3L);
            matchQueryRequest.setDatasetUid(datasetUid);
            MatchQueryVO matchQueryVO = documentSegmentsService.matchQuery(matchQueryRequest);

            //过滤掉 分数低的 < 0.7  文档搜索相似度阈值
            if (matchQueryVO != null && CollectionUtil.isNotEmpty(matchQueryVO.getRecords())) {
                this.searchResult = matchQueryVO;
            }
        }

        return this.searchResult;
    }

    private String parseContentLines(MatchQueryVO matchQueryVO) {

        List<Long> docIds = Optional.ofNullable(matchQueryVO).map(MatchQueryVO::getRecords).orElse(new ArrayList<>()).stream().map(d -> Long.valueOf(d.getId())).collect(Collectors.toList());

        List<DatasetSourceDataBasicInfoVO> docs = datasetSourceDataService.getSourceDataListData(docIds);


        List<MessageContentDocDTO> promptDocBlocks = Optional.ofNullable(matchQueryVO).map(MatchQueryVO::getRecords).orElse(new ArrayList<>()).stream().map(recordDTO -> {

            DatasetSourceDataBasicInfoVO doc = Optional.ofNullable(docs).orElse(new ArrayList<>()).stream().filter(docVo -> docVo.getId().equals(Long.valueOf(recordDTO.getId()))).findFirst().orElse(null);

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
                contentDocDTO.setContent(doc.getDescription());
                //contentDocDTO.setTime(doc.getCreateTime());

                return contentDocDTO;
            }

            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return PromptUtil.parseDocContentLines(promptDocBlocks);
    }


}
