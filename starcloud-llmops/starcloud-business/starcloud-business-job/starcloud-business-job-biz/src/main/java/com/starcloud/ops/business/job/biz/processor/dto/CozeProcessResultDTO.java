package com.starcloud.ops.business.job.biz.processor.dto;

import lombok.Data;
import tech.powerjob.worker.core.processor.ProcessResult;

@Data
public class CozeProcessResultDTO extends ProcessResult {

    private Object data;

    private int count;

    public CozeProcessResultDTO(boolean success, Object data,int count) {
        super(success);
        this.data = data;
        this.count = count;
    }
}
