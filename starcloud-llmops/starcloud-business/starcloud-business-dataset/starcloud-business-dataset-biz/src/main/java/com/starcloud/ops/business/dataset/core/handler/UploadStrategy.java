package com.starcloud.ops.business.dataset.core.handler;

import com.starcloud.ops.business.dataset.core.handler.dto.UploadFileRespDTO;

public interface UploadStrategy {
    UploadFileRespDTO process();
}
