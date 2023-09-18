package com.starcloud.ops.business.dataset.dal.es;


import cn.hutool.extra.spring.SpringUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.starcloud.ops.llm.langchain.core.model.llm.document.DocumentSegmentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
public class ElasticsearchRepository {

    public static DocumentSegmentDTO getByHash(String segmentHash) {
        ElasticsearchClient client = SpringUtil.getBean(ElasticsearchClient.class);
        String indexName = SpringUtil.getProperty("starcloud.elasticsearch.index.name");

        SearchRequest searchRequest = SearchRequest.of(s -> s.index(indexName).query(q -> q.term(t -> t.field("segmentHash").value(segmentHash))));

        try {
            SearchResponse<DocumentSegmentDTO> response = client.search(searchRequest, DocumentSegmentDTO.class);
            if (response.hits().hits().size() == 0) {
                return null;
            }
            return response.hits().hits().get(0).source();
        } catch (IOException e) {
            return null;
        }
    }



}
