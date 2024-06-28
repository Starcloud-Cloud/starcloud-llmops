package com.starcloud.ops.business.app.service.materiallibrary.handle;

import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;

/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.app.service.materiallibrary.handle
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2024/06/27  11:14
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2024/06/27   djl     1.0         1.0 Version
 */

// 修改策略模式 由参数控制实现类
public interface MaterialImportStrategy {

    void importMaterial(MaterialLibraryImportReqVO importReqVO);
}
