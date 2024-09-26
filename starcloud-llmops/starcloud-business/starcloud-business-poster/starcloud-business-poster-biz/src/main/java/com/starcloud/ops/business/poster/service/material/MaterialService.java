package com.starcloud.ops.business.poster.service.material;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.model.poster.PosterTemplateDTO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialSaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.MaterialDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 海报素材 Service 接口
 *
 * @author starcloudadmin
 */
public interface MaterialService {

    /**
     * 创建海报素材
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMaterial(@Valid MaterialSaveReqVO createReqVO);

    /**
     * 批量添加素材
     *
     * @param createReqVOS 素材列表
     * @return 是否添加成功
     */
    Boolean batchCreateMaterial(List<MaterialSaveReqVO> createReqVOS);

    /**
     * 更新海报素材
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterial(@Valid MaterialSaveReqVO updateReqVO);

    /**
     * 删除海报素材
     *
     * @param id 编号
     */
    void deleteMaterial(Long id);

    /**
     * 获得海报素材
     *
     * @param id 编号
     * @return 海报素材
     */
    MaterialDO getMaterial(Long id);

    /**
     * 获得海报素材分页
     *
     * @param pageReqVO 分页查询
     * @return 海报素材分页
     */
    PageResult<MaterialDO> getMaterialPage(MaterialPageReqVO pageReqVO);

    /**
     * 获取当前分类下素材数量
     *
     * @param categoryId 素材分类 ID
     * @return
     */
    Long getMaterialCountByCategoryId(Long categoryId);

    /**
     * 根据分组删除海报素材数据
     *
     * @param groupId 分组编号
     */
    void deleteMaterialByGroup(Long groupId);

    /**
     * 根据分组编号获取数据
     *
     * @param groupId 分组编号
     * @return 海报素材数据
     */
    List<MaterialDO> getMaterialByGroup(Long groupId);

    /**
     * 更新海报素材数据
     *
     * @param materialReqVOS
     */
    void updateMaterialByGroup(Long groupId, List<MaterialSaveReqVO> materialReqVOS);

    /**
     * 根据海报模板UID获取海报详情
     *
     * @param uid 海报模板UID
     * @return 海报详情
     */
    PosterTemplateDTO posterTemplate(String uid);

    /**
     * 根据分组获取海报列表
     *
     * @param group 分组编号
     * @return 海报素材列表
     */
    List<PosterTemplateDTO> listPosterTemplateByGroup(Long group);

}