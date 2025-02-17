package com.starcloud.ops.business.app.service.xhs.material;

import cn.hutool.json.JSON;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.model.creative.CreativeMaterialGenerationDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.BaseMaterialVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.FilterMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.GeneralFieldCodeReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ModifyMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.MaterialRespLogVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.MaterialRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageUidReqVO;
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
    void batchInsert(List<? extends AbstractCreativeMaterialDTO> materialDTOList);

    /**
     * 素材生成
     *
     * @param request 请求
     */
    JSON materialGenerate(CreativeMaterialGenerationDTO request);

    /**
     * 自定义素材生成
     *
     * @param request 请求
     */
    JSON customMaterialGenerate(CreativeMaterialGenerationDTO request);

    /**
     * 生成字段code
     *
     * @param reqVO
     * @return
     */
    List<MaterialFieldConfigDTO> generalFieldCode(GeneralFieldCodeReqVO reqVO);

    /**
     * 判断素材库字段设置内容显示类型 true显示图片 false显示列表
     * @param uid
     * @param planSource
     * @return
     */
    Boolean judgePicture(String uid, String planSource);

    /**
     *
     * @return
     */
    PageResult<MaterialRespLogVO> infoPageByMarketUid(AppLogConversationInfoPageUidReqVO query);
}
