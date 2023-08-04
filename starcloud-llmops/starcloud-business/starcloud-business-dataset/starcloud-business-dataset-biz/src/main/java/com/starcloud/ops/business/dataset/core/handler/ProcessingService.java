package com.starcloud.ops.business.dataset.core.handler;


import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import org.springframework.web.multipart.MultipartFile;

public interface ProcessingService {

    Boolean fileProcessing(MultipartFile file, byte[] fileContent, SplitRule splitRule, String datasetId,String batch,Integer dataModel,String dataType);

    Boolean urlProcessing(String url, SplitRule splitRule, String datasetId,String batch,Integer dataModel,String dataType);

    Boolean stringProcessing(String title,String context, SplitRule splitRule, String datasetId,String batch,Integer dataModel,String dataType);

    // 参数校验方法
    void validate(SplitRule splitRule, String datasetId);

}
