package com.starcloud.ops.business.dataset.core.handler;


import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadCharacterReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadFileReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadUrlReqVO;
import com.starcloud.ops.business.dataset.core.handler.dto.UploadResult;
import com.starcloud.ops.business.dataset.pojo.dto.UserBaseDTO;

public interface ProcessingService {

    UploadResult fileProcessing(UploadFileReqVO reqVO, UserBaseDTO baseDBHandleDTO);

    UploadResult urlProcessing(UploadUrlReqVO uploadUrlReqVO , UserBaseDTO baseDBHandleDTO);


    UploadResult stringProcessing(UploadCharacterReqVO uploadCharacterReqVO, UserBaseDTO baseDBHandleDTO);


}
