package com.starcloud.ops.business.poster.service.material;

import javax.validation.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.poster.controller.admin.material.vo.PosterMaterialPageReqsVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.PosterMaterialSaveReqVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.PosterMaterialPageReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.PosterMaterialDO;

/**
 * 海报素材 Service 接口
 *
 * @author starcloudadmin
 */
public interface PosterMaterialService {

    /**
     * 创建海报素材
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMaterial(@Valid PosterMaterialSaveReqVO createReqVO);

    /**
     * 更新海报素材
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterial(@Valid PosterMaterialSaveReqVO updateReqVO);

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
    PosterMaterialDO getMaterial(Long id);

    /**
     * 获得海报素材分页
     *
     * @param pageReqVO 分页查询
     * @return 海报素材分页
     */
    PageResult<PosterMaterialDO> getMaterialPage(PosterMaterialPageReqsVO pageReqVO);

    /**
     * 获取当前分类下素材数量
     * @param  categoryId  素材分类 ID
     * @return
     */
    Long getMaterialCountByCategoryId(Long categoryId);

    PageResult<PosterMaterialDO> getPosterMaterial(PosterMaterialPageReqVO pageReqVO);

    /**
     * 根据素材分类获取素材
     * @param categoryId
     * @return
     */
    PosterMaterialDO getMaterialByCategoryId(Long categoryId);
}