package com.starcloud.ops.business.app.service.xhs.material;

import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.material.request.CreativeMaterialQueryReq;

import java.util.List;

public interface CreativeMaterialService {

    /**
     * 查询素材
     * @param queryReq
     * @return
     */
     List<AbstractBaseCreativeMaterialDTO> filterMaterial(CreativeMaterialQueryReq queryReq);
}
