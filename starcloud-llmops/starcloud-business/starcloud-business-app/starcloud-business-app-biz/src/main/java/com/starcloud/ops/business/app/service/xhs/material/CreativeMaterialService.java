package com.starcloud.ops.business.app.service.xhs.material;

import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.BaseMaterialVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.FilterMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ModifyMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.MaterialRespVO;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Validated
public interface CreativeMaterialService {

    /**
     * 枚举类型
     *
     * @return
     */
    Map<String, Object> metadata();

    /**
     * 新建素材
     *
     * @param reqVO
     */
    void creatMaterial(@Valid BaseMaterialVO reqVO);

    /**
     * 删除素材
     *
     * @param uid
     */
    void deleteMaterial(String uid);

    /**
     * 修改素材
     *
     * @param reqVO
     */
    void modifyMaterial(ModifyMaterialReqVO reqVO);


    /**
     * 筛选素材
     *
     * @param queryReq
     * @return
     */
    List<MaterialRespVO> filterMaterial(@Valid FilterMaterialReqVO queryReq);

    /**
     * 批量插入
     *
     * @param materialDTOList
     */
    void batchInsert(List<? extends AbstractBaseCreativeMaterialDTO> materialDTOList);

}
