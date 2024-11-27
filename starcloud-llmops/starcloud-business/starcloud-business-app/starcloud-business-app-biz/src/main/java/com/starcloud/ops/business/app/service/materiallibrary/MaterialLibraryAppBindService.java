package com.starcloud.ops.business.app.service.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.BindMigrationReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.BindAppContentRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 应用素材绑定 Service 接口
 *
 * @author starcloudadmin
 */
public interface MaterialLibraryAppBindService {

    /**
     * 创建应用素材绑定
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMaterialLibraryAppBind(@Valid MaterialLibraryAppBindSaveReqVO createReqVO);

    /**
     * 绑定关系迁移
     *
     * @param bindMigrationReqVO 迁移的 VO
     */
    void createMaterialLibraryAppBind(@Valid BindMigrationReqVO bindMigrationReqVO);

    /**
     * 更新应用素材绑定
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterialLibraryAppBind(@Valid MaterialLibraryAppBindSaveReqVO updateReqVO);


    /**
     * 更新应用素材绑定
     *
     * @param newAppUid 更新信息
     * @param oldAppUid 更新信息
     */
    void updateMaterialLibraryAppBind(String newAppUid, String oldAppUid);

    /**
     * 删除应用素材绑定
     *
     * @param id 编号
     */
    void deleteMaterialLibraryAppBind(Long id);

    /**
     * 获得应用素材绑定
     *
     * @param id 编号
     * @return 应用素材绑定
     */
    MaterialLibraryAppBindDO getMaterialLibraryAppBind(Long id);

    /**
     * 获得应用素材绑定
     *
     * @param appUid 编号
     * @return 应用素材绑定
     */
    MaterialLibraryAppBindDO getMaterialLibraryAppBind(String appUid);

    /**
     * 获得应用素材绑定
     *
     * @param appUid 编号
     * @return 应用素材绑定
     */
    List<MaterialLibraryAppBindDO> getBindList(String appUid);

    /**
     * 获得应用素材绑定分页
     *
     * @param pageReqVO 分页查询
     * @return 应用素材绑定分页
     */
    PageResult<MaterialLibraryAppBindDO> getMaterialLibraryAppBindPage(MaterialLibraryAppBindPageReqVO pageReqVO);

    /**
     * 通过素材编号获取绑定关系
     *
     * @param libraryId 素材编号
     * @return 绑定列表
     */
    List<MaterialLibraryAppBindDO> getBindList(Long libraryId);

    /**
     * 绑定的app信息
     */
    List<BindAppContentRespVO> getBindAppContent(Long libraryId);
}