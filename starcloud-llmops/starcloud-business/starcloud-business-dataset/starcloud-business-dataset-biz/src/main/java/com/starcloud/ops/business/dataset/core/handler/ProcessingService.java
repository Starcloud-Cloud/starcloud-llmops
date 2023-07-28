package com.starcloud.ops.business.dataset.core.handler;


import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadRespDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ProcessingService {

    Boolean fileProcessing(MultipartFile file, byte[] fileContent, SplitRule splitRule, String datasetId);

    Boolean urlProcessing(String url, SplitRule splitRule, String datasetId);

    Boolean stringProcessing(String data, SplitRule splitRule, String datasetId);

    // 参数校验方法
    void validate(SplitRule splitRule, String datasetId);

}
