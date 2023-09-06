package com.starcloud.ops.business.dataset.core.handler;


import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadCharacterReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadFileReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadResult;
import com.starcloud.ops.business.dataset.pojo.dto.BaseDBHandleDTO;

public interface ProcessingService {

    UploadResult fileProcessing(UploadFileReqVO reqVO, BaseDBHandleDTO baseDBHandleDTO);

    UploadResult urlProcessing(UploadUrlReqVO uploadUrlReqVO ,BaseDBHandleDTO baseDBHandleDTO);


    UploadResult stringProcessing(UploadCharacterReqVO uploadCharacterReqVO,BaseDBHandleDTO baseDBHandleDTO);


}
