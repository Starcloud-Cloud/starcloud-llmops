package com.starcloud.ops.business.dataset.service.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 *
 */
@Data
public class sourceDataUrlUploadDTO {

    private String datasetId;

    private List<sourceDataUploadRespDTO> source;


}
