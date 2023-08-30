package com.starcloud.ops.business.dataset.core.handler;


import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadCharacterReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadFileReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadResult;

public interface ProcessingService {

    UploadResult fileProcessing(UploadFileReqVO reqVO);

    UploadResult urlProcessing(UploadUrlReqVO uploadUrlReqVO);


    UploadResult stringProcessing(UploadCharacterReqVO uploadCharacterReqVO);


}
