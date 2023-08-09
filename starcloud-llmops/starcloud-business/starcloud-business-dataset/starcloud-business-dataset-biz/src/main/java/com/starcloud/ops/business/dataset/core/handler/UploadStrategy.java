package com.starcloud.ops.business.dataset.core.handler;

import com.starcloud.ops.business.dataset.core.handler.dto.UploadResultDTO;

public interface UploadStrategy {
    UploadResultDTO process(Long userId);
}
