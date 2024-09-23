package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.BindMigrationReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryAppBindMapper;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_APP_BIND_NOT_EXISTS;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_NO_BIND_APP;

/**
 * 应用素材绑定 Service 实现类
 *
 * @author starcloudadmin
 */
@Slf4j
@Service
@Validated
public class MaterialLibraryAppBindServiceImpl implements MaterialLibraryAppBindService {


    @Resource
    @Lazy
    private MaterialLibraryService materialLibraryService;

    @Resource
    @Lazy
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    @Resource
    @Lazy
    private MaterialLibrarySliceService materialLibrarySliceService;


    @Resource
    private MaterialLibraryAppBindMapper materialLibraryAppBindMapper;

    @Override
    @Transactional
    public Long createMaterialLibraryAppBind(MaterialLibraryAppBindSaveReqVO createReqVO) {

        // 处理之前的绑定
        handleBindStatus(createReqVO);

        // 插入
        MaterialLibraryAppBindDO materialLibraryAppBind = BeanUtils.toBean(createReqVO, MaterialLibraryAppBindDO.class);
        materialLibraryAppBind.setStatus(true);
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
    @Transactional
    public void createMaterialLibraryAppBind(BindMigrationReqVO bindMigrationReqVO) {
        MaterialLibraryRespVO materialLibrary;
        if (Objects.nonNull(bindMigrationReqVO.getLibraryUid())) {
            materialLibrary = materialLibraryService.getMaterialLibraryByUid(bindMigrationReqVO.getLibraryUid());
        } else {
            materialLibrary = materialLibraryService.getMaterialLibraryByApp(new MaterialLibraryAppReqVO().setAppUid(bindMigrationReqVO.getAppUid()));
        }
        createMaterialLibraryAppBind(new MaterialLibraryAppBindSaveReqVO().setLibraryId(materialLibrary.getId()).setAppUid(bindMigrationReqVO.getAppUid()).setAppType(bindMigrationReqVO.getAppType()).setUserId(bindMigrationReqVO.getUserId()));
    }

    @Override
    @Transactional
    public void updateMaterialLibraryAppBind(MaterialLibraryAppBindSaveReqVO updateReqVO) {


        MaterialLibraryAppBindDO bind = this.getMaterialLibraryAppBind(updateReqVO.getAppUid());

        if (bind == null) {
            log.error("当前应用未绑定素材库，{}", updateReqVO.getAppUid());
            throw exception(MATERIAL_LIBRARY_NO_BIND_APP);
        }

        if (updateReqVO.getAppType() == null) {
            updateReqVO.setAppType(bind.getAppType());
        }

        if (updateReqVO.getUserId() == null) {
            updateReqVO.setUserId(bind.getUserId());
        }

        // 处理之前的绑定
        handleBindStatus(updateReqVO);

        // materialLibraryAppBindMapper.deleteById(bind.getId());

        MaterialLibraryAppBindDO materialLibraryAppBind = BeanUtils.toBean(updateReqVO, MaterialLibraryAppBindDO.class);
        materialLibraryAppBind.setStatus(true);
        materialLibraryAppBindMapper.insert(materialLibraryAppBind);

    }

    /**
     * 更新应用素材绑定
     *
     * @param newAppUid 更新信息
     * @param oldAppUid 更新信息
     */
    @Override
    @Transactional
    public void updateMaterialLibraryAppBind(String newAppUid, String oldAppUid) {

        MaterialLibraryAppBindDO newBind = this.getMaterialLibraryAppBind(newAppUid);

        if (newBind == null) {
            log.error("当前应用未绑定素材库，{}", newAppUid);
            throw exception(MATERIAL_LIBRARY_NO_BIND_APP);
        }

        MaterialLibraryAppBindDO oldBind = this.getMaterialLibraryAppBind(oldAppUid);

        if (oldBind == null) {
            log.error("当前应用未绑定素材库，{}", oldAppUid);
            throw exception(MATERIAL_LIBRARY_NO_BIND_APP);
        }
        // 创建绑定关键
        createMaterialLibraryAppBind(new MaterialLibraryAppBindSaveReqVO().setLibraryId(newBind.getLibraryId()).setAppUid(oldBind.getAppUid()).setAppType(oldBind.getAppType()).setUserId(oldBind.getUserId()));
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


    private void handleBindStatus(MaterialLibraryAppBindSaveReqVO createReqVO) {
        // 获取当前应用下的所有绑定记录
        List<MaterialLibraryAppBindDO> binds = this.getBindList(createReqVO.getAppUid());

        if (binds == null || binds.isEmpty()) {
            return;
        }

        // List<Long> bindIdList = binds.stream()
        //         .filter(MaterialLibraryAppBindDO::getStatus)
        //         .map(MaterialLibraryAppBindDO::getId)
        //         .collect(Collectors.toList());

        // if (bindIdList.isEmpty()) {
        //     return;
        // }

        // 处理后需要更新的 sku
        List<MaterialLibraryAppBindDO> bindDOS = new ArrayList<>();

        binds.forEach(bind -> bindDOS.add(new MaterialLibraryAppBindDO().setId(bind.getId()).setStatus(false)));

        materialLibraryAppBindMapper.updateBatch(bindDOS);

    }

    /**
     * 获得应用素材绑定
     *
     * @param appUid 编号
     * @return 应用素材绑定
     */
    @Override
    public List<MaterialLibraryAppBindDO> getBindList(String appUid) {
        return materialLibraryAppBindMapper.selectListByApp(appUid);
    }

    @Override
    public PageResult<MaterialLibraryAppBindDO> getMaterialLibraryAppBindPage(MaterialLibraryAppBindPageReqVO pageReqVO) {
        return materialLibraryAppBindMapper.selectPage(pageReqVO);
    }

    /**
     * 通过素材编号获取绑定关系
     *
     * @param libraryId 素材编号
     * @return 绑定列表
     */
    @Override
    public List<MaterialLibraryAppBindDO> getBindList(Long libraryId) {
        return materialLibraryAppBindMapper.selectListByLibrary(libraryId);
    }

}