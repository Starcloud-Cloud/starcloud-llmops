package com.starcloud.ops.business.app.domain.entity.chat.prompts;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.business.app.domain.entity.chat.DatesetEntity;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
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

    private String promptV1 = "Use the following CONTEXT as your learned knowledge:\n" +
            "[CONTEXT]\n" +
            "{context}\n" +
            "[END CONTEXT]\n" +
            "When answer to user:\n" +
            "- If you don't know, just say that you don't know.\n" +
            "- If you don't know when you are not sure, ask for clarification.\n" +
            "Avoid mentioning that you obtained the information from the context.\n";

    private String query;

    private List<DatesetEntity> datesetEntities;

    private String searchResult;

    public ContextPrompt(List<DatesetEntity> datesetEntities, String query) {
        this.query = query;
        this.datesetEntities = datesetEntities;
    }

    @Override
    protected String _buildPromptStr() {

        BaseVariable variable = BaseVariable.newString("context", this.searchResult);

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
    private String searchDataset(String query) {

        if (StrUtil.isNotBlank(query) && this.searchResult == null) {

            List<String> datasetUid = Optional.ofNullable(this.getDatesetEntities()).orElse(new ArrayList<>())
                    .stream().filter(DatesetEntity::getEnabled).map(DatesetEntity::getDatasetUid).collect(Collectors.toList());


            //@todo 需要 block 对象
            SimilarQueryRequest similarQueryRequest = new SimilarQueryRequest();
            similarQueryRequest.setQuery(query);
            similarQueryRequest.setK(3L);
            similarQueryRequest.setDatasetUid(datasetUid);
            //documentSegmentsService.similarQuery(similarQueryRequest);

            this.searchResult = null;

        }

        return this.searchResult;
    }


}
