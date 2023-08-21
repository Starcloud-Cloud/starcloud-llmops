package com.starcloud.ops.business.dataset.core.handler;


import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadCharacterReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadFileReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadResult;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import org.springframework.web.multipart.MultipartFile;

public interface ProcessingService {

    UploadResult fileProcessing(MultipartFile file, byte[] fileContent, UploadFileReqVO reqVO, Integer dataModel, String dataType);

    UploadResult urlProcessing(String url,UploadUrlReqVO uploadUrlReqVO, Integer dataModel, String dataType);


    UploadResult stringProcessing(UploadCharacterReqVO uploadCharacterReqVO, Integer dataModel, String dataType);

    // 参数校验方法
    DatasetsDO getDatasetInfo(String datasetId);

}
