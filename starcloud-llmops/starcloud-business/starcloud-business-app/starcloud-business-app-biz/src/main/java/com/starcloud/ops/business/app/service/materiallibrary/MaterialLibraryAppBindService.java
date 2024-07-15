package com.starcloud.ops.business.app.service.materiallibrary;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;

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
     * 更新应用素材绑定
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterialLibraryAppBind(@Valid MaterialLibraryAppBindSaveReqVO updateReqVO);

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
     * @param id 编号
     * @return 应用素材绑定
     */
    Long getMaterialLibraryAppBind(String appUid);

    /**
     * 获得应用素材绑定分页
     *
     * @param pageReqVO 分页查询
     * @return 应用素材绑定分页
     */
    PageResult<MaterialLibraryAppBindDO> getMaterialLibraryAppBindPage(MaterialLibraryAppBindPageReqVO pageReqVO);

}