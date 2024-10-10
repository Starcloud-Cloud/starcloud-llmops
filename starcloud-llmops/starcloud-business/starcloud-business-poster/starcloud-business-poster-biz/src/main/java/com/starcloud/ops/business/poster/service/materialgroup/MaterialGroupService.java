package com.starcloud.ops.business.poster.service.materialgroup;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPublishReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupRespVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupSaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.materialgroup.MaterialGroupDO;

import javax.validation.Valid;

/**
 * 海报素材分组 Service 接口
 *
 * @author starcloudadmin
 */
public interface MaterialGroupService {

    /**
     * 创建海报素材分组
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    String createMaterialGroup(@Valid MaterialGroupSaveReqVO createReqVO);

    /**
     * 更新海报素材分组
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterialGroup(@Valid MaterialGroupSaveReqVO updateReqVO);

    /**
     * 更新海报素材分组
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterialGroupByUid(@Valid MaterialGroupSaveReqVO updateReqVO);

    /**
     * 删除海报素材分组
     *
     * @param id 编号
     */
    void deleteMaterialGroup(Long id);

    /**
     * 删除海报素材分组
     *
     * @param uid 编号
     */
    void deleteMaterialGroupByUid(String uid);

    /**
     * 获得海报素材分组
     *
     * @param id 编号
     * @return 海报素材分组
     */
    MaterialGroupDO getMaterialGroup(Long id);

    /**
     * 获得海报素材分组
     *
     * @param uid 编号
     * @return 海报素材分组
     */
    MaterialGroupDO getMaterialGroupByUid(String uid);

    /**
     * 获得海报素材分组分页
     *
     * @param pageReqVO 分页查询
     * @return 海报素材分组分页
     */
    PageResult<MaterialGroupRespVO> getMaterialGroupPage(MaterialGroupPageReqVO pageReqVO);

    /**
     * 发布数据
     *
     * @param groupId 分组编号
     */
    void publish(String groupId);


    /**
     * 发布数据
     *
     * @param groupId 分组编号
     */
    void cancelPublish(String groupId);

    /**
     * 获取当前分类下分组数量
     *
     * @param categoryId 素材分类 ID
     * @return
     */
    Long getCountByCategoryId(Long categoryId);

    /**
     * @param uid 分组编号
     */
    void copyGroup(String uid);


    /**
     * @param sourceGroupUid 源分组编号
     * @param targetGroupUid 目标分组编号
     */
    void mergeGroup(String sourceGroupUid, String targetGroupUid);

    /**
     * 操作数据是否发布
     * @param publishReqVO 发布数据VO
     */
    void handlePublish(MaterialGroupPublishReqVO publishReqVO);
}