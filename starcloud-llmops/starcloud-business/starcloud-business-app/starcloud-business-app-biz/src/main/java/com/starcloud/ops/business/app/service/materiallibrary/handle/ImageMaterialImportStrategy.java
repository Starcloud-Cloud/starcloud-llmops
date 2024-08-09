package com.starcloud.ops.business.app.service.materiallibrary.handle;

import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_IMPORT_FAIL_IMAGE_NO_SUPPRT;

/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.app.service.materiallibrary.handle
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2024/06/27  11:19
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2024/06/27   AlanCusack    1.0         1.0 Version
 */
@Slf4j
@Component
public class ImageMaterialImportStrategy implements MaterialImportStrategy{
    /**
     * @param importReqVO importReqVO
     */
    @Override
    public void importMaterial(MaterialLibraryImportReqVO importReqVO) {
        // 批量上传图片
        throw exception(MATERIAL_LIBRARY_IMPORT_FAIL_IMAGE_NO_SUPPRT);
    }
}
