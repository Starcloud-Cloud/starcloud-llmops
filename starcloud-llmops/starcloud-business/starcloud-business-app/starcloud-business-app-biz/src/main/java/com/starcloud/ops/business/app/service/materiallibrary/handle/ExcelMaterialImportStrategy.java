package com.starcloud.ops.business.app.service.materiallibrary.handle;

import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_EXPORT_FAIL_EXCEL_NO_SUPPRT;

/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.app.service.materiallibrary.handle
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2024/06/27  11:18
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2024/06/27   AlanCusack    1.0         1.0 Version
 */
@Slf4j
@Component
public class ExcelMaterialImportStrategy implements MaterialImportStrategy{
    /**
     * @param importReqVO
     */
    @Override
    public void importMaterial(MaterialLibraryImportReqVO importReqVO) {
        throw exception(MATERIAL_LIBRARY_EXPORT_FAIL_EXCEL_NO_SUPPRT);
    }
}
