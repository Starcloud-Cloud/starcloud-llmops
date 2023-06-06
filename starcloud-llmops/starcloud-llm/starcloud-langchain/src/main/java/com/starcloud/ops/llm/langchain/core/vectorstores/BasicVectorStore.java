package com.starcloud.ops.llm.langchain.core.vectorstores;

import com.starcloud.ops.llm.langchain.core.model.llm.document.DocumentSegment;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryHit;

import java.util.List;

public interface BasicVectorStore {

    void addSegment(List<DocumentSegment> segments);

    List<KnnQueryHit> knnSearch(List<Float> queryVector, KnnQueryDTO queryDTO);

    void removeSegment(List<String> segmentIds);

}
