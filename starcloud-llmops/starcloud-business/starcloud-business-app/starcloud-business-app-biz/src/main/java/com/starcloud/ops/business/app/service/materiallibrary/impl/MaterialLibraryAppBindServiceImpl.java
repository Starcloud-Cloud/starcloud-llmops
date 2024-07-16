package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.BindMigrationReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryAppBindMapper;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_APP_BIND_NOT_EXISTS;

/**
 * 应用素材绑定 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class MaterialLibraryAppBindServiceImpl implements MaterialLibraryAppBindService {


    @Resource
    @Lazy
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibraryAppBindMapper materialLibraryAppBindMapper;

    @Override
    public Long createMaterialLibraryAppBind(MaterialLibraryAppBindSaveReqVO createReqVO) {

        MaterialLibraryAppBindDO bind = this.getMaterialLibraryAppBind(createReqVO.getAppUid());
        if (bind != null) {
            materialLibraryAppBindMapper.deleteById(bind.getId());
        }

        // 插入
        MaterialLibraryAppBindDO materialLibraryAppBind = BeanUtils.toBean(createReqVO, MaterialLibraryAppBindDO.class);
        materialLibraryAppBindMapper.insert(materialLibraryAppBind);
        // 返回
        return materialLibraryAppBind.getId();
    }

    /**
     * 绑定关系迁移
     *
     * @param bindMigrationReqVO 迁移的 VO
     */
    @Override
    public void createMaterialLibraryAppBind(BindMigrationReqVO bindMigrationReqVO) {
        MaterialLibraryRespVO materialLibrary = materialLibraryService.getMaterialLibraryByUid(bindMigrationReqVO.getLibraryUid());
        this.createMaterialLibraryAppBind(new MaterialLibraryAppBindSaveReqVO().setLibraryId(materialLibrary.getId()).setAppUid(bindMigrationReqVO.getAppUid()).setAppType(bindMigrationReqVO.getAppType()).setUserId(bindMigrationReqVO.getUserId()));
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
            throw exception(MATERIAL_LIBRARY_APP_BIND_NOT_EXISTS);
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
    public MaterialLibraryAppBindDO getMaterialLibraryAppBind(String appUid) {

        return materialLibraryAppBindMapper.selectOneByApp(appUid);
    }

    @Override
    public PageResult<MaterialLibraryAppBindDO> getMaterialLibraryAppBindPage(MaterialLibraryAppBindPageReqVO pageReqVO) {
        return materialLibraryAppBindMapper.selectPage(pageReqVO);
    }

}