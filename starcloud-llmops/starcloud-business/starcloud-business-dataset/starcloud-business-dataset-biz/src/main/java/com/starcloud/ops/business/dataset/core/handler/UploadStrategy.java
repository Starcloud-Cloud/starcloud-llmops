package com.starcloud.ops.business.dataset.core.handler;

import com.starcloud.ops.business.dataset.core.handler.dto.UploadContentDTO;

public interface UploadStrategy {
    UploadContentDTO process(Long userId);
}
