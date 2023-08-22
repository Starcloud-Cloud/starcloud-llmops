package com.starcloud.ops.business.app.domain.entity.chat.prompts;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.domain.entity.chat.DatesetEntity;
import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import com.starcloud.ops.business.dataset.pojo.request.MatchQueryRequest;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
import com.starcloud.ops.business.dataset.pojo.response.MatchQueryVO;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 上下文 prompt
 */
@Slf4j
@Data
public class ContextPrompt extends BasePromptConfig {

    private static DocumentSegmentsService documentSegmentsService = SpringUtil.getBean(DocumentSegmentsService.class);

    private String promptV1 = "The content in [CONTEXT] is multi-line, and each line represents the structure of the document block. It contains the serial number, the corresponding document ID and the document block information in JSON format, and the JSON format contains fields `blockId` block ID, `content` block content, `sourceName` block source name, `sourceUrl` block source url.\n" +
            "[block example]:\n" +
            "1. Xxxxx {“blockId”: “”, “content”: “”, “sourceName”: “”, “sourceUrl”: “”}\n" +
            "2. Xxxxx {“blockId”: “”, “content”: “”, “sourceName”: “”, “sourceUrl”: “”}\n" +
            "....\n" +
            "[end of example]\n" +
            "Use the following [CONTEXT] as your learned knowledge:\n" +
            "[CONTEXT]\n" +
            "{context}\n" +
            "[END CONTEXT]\n" +
            "Note When answer to user:\n" +
            "- If you don't know, just say that you don't know.\n" +
            "- If you don't know when you are not sure, ask for clarification.\n" +
            "- Avoid mentioning that you obtained the information from the context.\n" +
            "- If the content of the answer refers to the content of the block in CONTEXT, you need to add the serial number of the referenced block at the end of the relevant sentence, like this \"{1}\" with braces.\n\n";

    private String query;

    private List<DatesetEntity> datesetEntities;

    private MatchQueryVO searchResult;

    public ContextPrompt(List<DatesetEntity> datesetEntities, String query) {
        this.query = query;
        this.datesetEntities = datesetEntities;
    }

    @Override
    protected String _buildPromptStr() {

        String contentLines = this.parseContentLines(this.searchResult);

        BaseVariable variable = BaseVariable.newString("context", contentLines);

        PromptTemplate template = new PromptTemplate(this.promptV1);

        return template.format(Arrays.asList(variable));
    }

    @Override
    protected Boolean _isEnable() {
        this.searchResult = this.searchDataset(this.query);

        if (this.searchResult != null) {
            return true;
        }

        return false;
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

            if (matchQueryVO != null && CollectionUtil.isNotEmpty(matchQueryVO.getRecords())) {
                this.searchResult = matchQueryVO;
            }
        }

        return this.searchResult;
    }

    private String parseContentLines(MatchQueryVO matchQueryVO) {

        List<String> blockLines = new ArrayList<>();
        for (int i = 0; i < searchResult.getRecords().size(); i++) {

            RecordDTO recordDTO = searchResult.getRecords().get(i);
            PromptBlockDTO promptBlockDTO = new PromptBlockDTO();

            promptBlockDTO.setContent(recordDTO.getContent());
            promptBlockDTO.setBlockId(recordDTO.getId());

            //文档详情
            recordDTO.getDocumentId();


            blockLines.add((i + 1) + ". " + recordDTO.getDocumentId() + ": " + JSONUtil.toJsonStr(promptBlockDTO));
        }

        return StrUtil.join("\n\n", blockLines);
    }


    @Data
    public static class PromptBlockDTO {

        /**
         * 文档块ID
         */
        private String blockId;

        /**
         * 文档块内容
         */
        private String content;


        /**
         * 块数据来源的名称
         */
        private String sourceName;

        /**
         * 来源的地址
         */
        private String sourceUrl;


    }


}
