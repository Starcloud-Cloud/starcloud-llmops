package com.starcloud.ops.llm.langchain.core.indexes.vectorstores;

import cn.hutool.core.collection.CollectionUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.starcloud.ops.llm.langchain.core.model.llm.document.DocumentSegmentDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "starcloud-llm.vector.store", havingValue = "elasticsearch")
public class ElasticSearchVectorStore implements BasicVectorStore {

    private static final String INDEX_NAME = "vector_index_l2";

    @Autowired
    private ElasticsearchClient esClient;

    @Override
    public void addSegment(List<DocumentSegmentDTO> segments) {
        List<BulkOperation> operations = new ArrayList<>();
        for (DocumentSegmentDTO document : segments) {
            BulkOperation operation = BulkOperation.of(builder -> builder
                    .index(index -> index
                            .index(INDEX_NAME)
                            .document(document)
                            .id(document.getSegmentId())
                    ));
            operations.add(operation);
        }
        BulkRequest request = BulkRequest.of(builder -> builder
                .operations(operations));
        try {
            esClient.bulk(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<KnnQueryHit> knnSearch(List<Float> queryVector, KnnQueryDTO queryDTO) {
        queryDTO.checkDefaultValue();
        TermQuery status = TermQuery.of(t -> t.field("status").value(true));

        TermsQuery termsQuery;
        if (CollectionUtil.isNotEmpty(queryDTO.getDatasetIds())) {
            List<FieldValue> datasetIds = queryDTO.getDatasetIds().stream().map(FieldValue::of).collect(Collectors.toList());
            termsQuery = TermsQuery.of(t -> t.field("datasetId").terms(f -> f.value(datasetIds)));
        } else if (CollectionUtil.isNotEmpty(queryDTO.getDocumentIds())) {
            List<FieldValue> documentIds = queryDTO.getDocumentIds().stream().map(FieldValue::of).collect(Collectors.toList());
            termsQuery = TermsQuery.of(t -> t.field("documentId").terms(f -> f.value(documentIds)));
        } else if (CollectionUtil.isNotEmpty(queryDTO.getSegmentIds())) {
            List<FieldValue> segmentIds = queryDTO.getSegmentIds().stream().map(FieldValue::of).collect(Collectors.toList());
            termsQuery = TermsQuery.of(t -> t.field("segmentId").terms(f -> f.value(segmentIds)));
        } else {
            throw new IllegalArgumentException("数据集id、文档id、分段id不能同时为空");
        }
        BoolQuery boolQuery = BoolQuery.of(b -> b.must(new Query(status)).must(new Query(termsQuery)));
        Query query = Query.of(q -> q.bool(boolQuery));

        KnnQuery knnQuery = new KnnQuery.Builder()
                .k(queryDTO.getK())
                .field("vector")
                .numCandidates(queryDTO.getNumCandidates())
                .queryVector(queryVector)
                .filter(query)
                .build();
        SearchRequest vector = new SearchRequest.Builder().knn(knnQuery).index(INDEX_NAME).build();
        try {
            SearchResponse<DocumentSegmentDTO> search = esClient.search(vector, DocumentSegmentDTO.class);
            return search.hits().hits().stream().map(hit -> {
                return KnnQueryHit.builder().score(hit.score()).document(hit.source()).build();
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeSegment(List<String> segmentIds) {
        List<BulkOperation> operations = new ArrayList<>();
        DocumentSegmentDTO documentSegment = DocumentSegmentDTO.builder().status(false).build();
        for (String segmentId : segmentIds) {
            BulkOperation operation = BulkOperation.of(builder -> builder
                    .update(index -> index
                            .id(segmentId)
                            .action(a -> a.doc(documentSegment))
                    ));
            operations.add(operation);
        }
        BulkRequest request = BulkRequest.of(builder -> builder
                .index(INDEX_NAME)
                .operations(operations));
        try {
            esClient.bulk(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
