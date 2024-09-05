package com.starcloud.ops.business.job.biz.processor.dto;

import lombok.Data;
import tech.powerjob.worker.core.processor.ProcessResult;

@Data
public class CozeProcessResultDTO extends ProcessResult {

    private Object data;

    public CozeProcessResultDTO(boolean success, Object data) {
        super(success);
        this.data = data;
    }
}
