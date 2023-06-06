package com.starcloud.ops.llm.langchain.core.vectorstores;

import com.starcloud.ops.llm.langchain.core.model.llm.document.DocumentSegment;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryHit;
import org.apache.commons.math3.util.MathArrays;
import org.apache.ibatis.annotations.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * embedding保存在数据库中，在本地内存中做l2向量距离计算
 */
@Component
@ConditionalOnProperty(name = "starcloud.llm.vector.store", havingValue = "default", matchIfMissing = true)
public class DefaultVectorStore implements BasicVectorStore {

    /**
     * 向量放在数据库中
     * <p>
     * create table embeddings if not exists
     * (
     * id         uuid,
     * hash       varchar(64),   判断重复数据 不再重新计算embeddings
     * embedding  BLOB,
     * created_at timestamp
     * );
     */

    @Resource(name = "defaultRepository")
    private SegmentEmbeddingMapper mapper;


    @Override
    public void addSegment(List<DocumentSegment> segments) {
        //todo 不需插入 复用llm_segments_embeddings中的向量
    }

    @Override
    public List<KnnQueryHit> knnSearch(List<Float> queryVector, KnnQueryDTO queryDTO) {
        List<Map<String, Object>> maps = mapper.selectBySegmentIds(queryDTO.getSegmentIds());
        List<KnnQueryHit> knnQueryHitList = new ArrayList<>();

        for (Map<String, Object> map : maps) {
            DocumentSegment documentSegment = DocumentSegment.builder()
                    .tenantId(Long.valueOf(map.get("tenant_id").toString()))
                    .dataSetId(String.valueOf(map.get("dataset_id")))
                    .documentId(String.valueOf(map.get("document_id")))
                    .segmentId(String.valueOf(map.get("segment_id")))
                    .segmentText(String.valueOf(map.get("content")))
                    .vector((List<Float>) map.get("vector"))
                    .build();
            List<Float> vector = (List<Float>) map.get("vector");
            double[] docVec = vector.stream().mapToDouble(Float::floatValue).toArray();
            double[] queryVec = queryVector.stream().mapToDouble(Float::floatValue).toArray();
            double score = MathArrays.distance(docVec, queryVec);
            KnnQueryHit knnQueryHit = KnnQueryHit.builder().document(documentSegment).score(score).build();
            knnQueryHitList.add(knnQueryHit);

        }
        // desc sort
        knnQueryHitList.sort((a, b) -> a.getScore() > b.getScore() ? -1 : 1);
        return knnQueryHitList.subList(0, Math.toIntExact(queryDTO.getK()));
    }

    @Override
    public void removeSegment(List<String> segmentIds) {
        mapper.deleteBySegmentIds(segmentIds);
    }

    public interface SegmentEmbeddingMapper {

        @Select("<script> "
                + "select e.tenant_id,e.dataset_id,e.document_id,e.segment_id,e.vector,s.content"
                + " from llm_segments_embeddings e inner join llm_document_segments s on e.segment_id = s.id"
                + " where status = true and segment_id in "
                + "  <foreach collection='list' item='item' index='index' "
                + "    open='(' separator=',' close=')' >                 "
                + "    #{item}                                            "
                + "  </foreach>"
                + "</script>"
        )
        List<Map<String, Object>> selectBySegmentIds(List<String> list);

        @Update(
                "<script> "
                + "update llm_segments_embeddings set status = false where segment_id in "
                + "  <foreach collection='list' item='item' index='index' "
                + "    open='(' separator=',' close=')' >                 "
                + "    #{item}                                            "
                + "  </foreach>"
                + "</script>"
        )
        int deleteBySegmentIds(List<String> list);

    }


}
