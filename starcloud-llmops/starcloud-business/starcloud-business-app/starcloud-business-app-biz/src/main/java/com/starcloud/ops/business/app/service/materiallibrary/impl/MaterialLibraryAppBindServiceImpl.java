package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryAppBindMapper;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 应用素材绑定 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class MaterialLibraryAppBindServiceImpl implements MaterialLibraryAppBindService {

    @Resource
    private MaterialLibraryAppBindMapper materialLibraryAppBindMapper;

    @Override
    public Long createMaterialLibraryAppBind(MaterialLibraryAppBindSaveReqVO createReqVO) {

        MaterialLibraryAppBindDO bind = this.getMaterialLibraryAppBind(createReqVO.getAppUid(), createReqVO.getAppType(), createReqVO.getUserId());
        if (bind == null){

        }

        // 插入
        MaterialLibraryAppBindDO materialLibraryAppBind = BeanUtils.toBean(createReqVO, MaterialLibraryAppBindDO.class);
        materialLibraryAppBindMapper.insert(materialLibraryAppBind);
        // 返回
        return materialLibraryAppBind.getId();
    }

    @Override
    public void updateMaterialLibraryAppBind(MaterialLibraryAppBindSaveReqVO updateReqVO) {
        // 校验存在
        validateMaterialLibraryAppBindExists(updateReqVO.getId());
        // 更新
        MaterialLibraryAppBindDO updateObj = BeanUtils.toBean(updateReqVO, MaterialLibraryAppBindDO.class);
        materialLibraryAppBindMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaterialLibraryAppBind(Long id) {
        // 校验存在
        validateMaterialLibraryAppBindExists(id);
        // 删除
        materialLibraryAppBindMapper.deleteById(id);
    }

    private void validateMaterialLibraryAppBindExists(Long id) {
        if (materialLibraryAppBindMapper.selectById(id) == null) {
            // throw exception(MATERIAL_LIBRARY_APP_BIND_NOT_EXISTS);
            throw exception(11);
        }
    }

    @Override
    public MaterialLibraryAppBindDO getMaterialLibraryAppBind(Long id) {
        return materialLibraryAppBindMapper.selectById(id);
    }

    /**
     * 获得应用素材绑定
     *
     * @param appUid 编号
     * @return 应用素材绑定
     */
    @Override
    public MaterialLibraryAppBindDO getMaterialLibraryAppBind(String appUid, Integer appType, Long userId) {

        return materialLibraryAppBindMapper.selectOneByApp(appUid, appType, userId);
    }

    @Override
    public PageResult<MaterialLibraryAppBindDO> getMaterialLibraryAppBindPage(MaterialLibraryAppBindPageReqVO pageReqVO) {
        return materialLibraryAppBindMapper.selectPage(pageReqVO);
    }

}