package com.starcloud.ops.business.app.service.plugins.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginConfigReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginConfigRespVO;
import com.starcloud.ops.business.app.convert.plugin.PluginConfigConvert;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginConfigDO;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginDefinitionDO;
import com.starcloud.ops.business.app.dal.mysql.plugin.PluginConfigMapper;
import com.starcloud.ops.business.app.dal.mysql.plugin.PluginDefinitionMapper;
import com.starcloud.ops.business.app.enums.plugin.PluginBindTypeEnum;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.plugins.PluginConfigService;
import com.starcloud.ops.business.app.service.plugins.PluginsDefinitionService;
import com.starcloud.ops.business.job.api.BusinessJobApi;
import com.starcloud.ops.business.user.api.dept.DeptPermissionApi;
import com.starcloud.ops.business.user.enums.dept.DeptPermissionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.*;

@Slf4j
@Service
public class PluginConfigServiceImpl implements PluginConfigService {

    @Resource
    private PluginConfigMapper pluginConfigMapper;

    @Resource
    private PluginDefinitionMapper pluginDefinitionMapper;

    @Resource
    private BusinessJobApi businessJobApi;

    @Resource
    private DeptPermissionApi deptPermissionApi;

    @Resource
    @Lazy
    private MaterialLibraryService materialLibraryService;

    @Override
    public PluginConfigRespVO create(PluginConfigVO pluginConfigVO) {
        PluginConfigDO oldConfig = pluginConfigMapper.selectByLibraryUid(pluginConfigVO.getLibraryUid(), pluginConfigVO.getPluginUid());
        if (Objects.nonNull(oldConfig)) {
            return PluginConfigConvert.INSTANCE.convert(oldConfig);
        }

        DataPermissionUtils.executeIgnore(() -> {
            PluginDefinitionDO pluginDefinitionDO = pluginDefinitionMapper.selectByUid(pluginConfigVO.getPluginUid());
            if (Objects.isNull(pluginDefinitionDO)) {
                throw exception(PLUGIN_NOT_EXIST, pluginConfigVO.getPluginUid());
            }
            if (!pluginDefinitionDO.getPublished()) {
                deptPermissionApi.adminEditPermission(pluginDefinitionDO.getDeptId());
            }
        });

        MaterialLibraryRespVO libraryRespVO = materialLibraryService.getMaterialLibraryByUid(pluginConfigVO.getLibraryUid());
        deptPermissionApi.adminEditPermission(libraryRespVO.getDeptId());
        deptPermissionApi.checkPermission(DeptPermissionEnum.plugin_bind_add, Long.valueOf(libraryRespVO.getCreator()));

        PluginConfigDO pluginConfigDO = PluginConfigConvert.INSTANCE.convert(pluginConfigVO);
        pluginConfigDO.setUid(IdUtil.fastSimpleUUID());
        pluginConfigDO.setType(PluginBindTypeEnum.owner.getCode());
        pluginConfigMapper.insert(pluginConfigDO);
        return PluginConfigConvert.INSTANCE.convert(pluginConfigDO);
    }

    @Override
    public void modify(PluginConfigReqVO pluginVO) {
        PluginConfigDO pluginConfigDO = getByUid(pluginVO.getUid());
        deptPermissionApi.adminEditPermission(pluginConfigDO.getDeptId());
        PluginConfigDO updateConfig = PluginConfigConvert.INSTANCE.convert(pluginVO);
        updateConfig.setId(pluginConfigDO.getId());
        pluginConfigMapper.updateById(updateConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uid, boolean forced) {
        PluginConfigDO pluginConfigDO = getByUid(uid);
        if (!forced) {
            deptPermissionApi.adminEditPermission(pluginConfigDO.getDeptId());
            deptPermissionApi.checkPermission(DeptPermissionEnum.plugin_bind_delete, Long.valueOf(pluginConfigDO.getCreator()));
        }

        if (!forced && Objects.equals(PluginBindTypeEnum.sys.getCode(), pluginConfigDO.getType())) {
            throw exception(SYSTEM_PLUGIN);
        }
        pluginConfigMapper.deleteById(pluginConfigDO.getId());
        // 删除定时任务
        businessJobApi.deleteByForeignKey(pluginConfigDO.getUid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByPluginUid(String pluginUid) {
        List<PluginConfigDO> pluginConfigList = pluginConfigMapper.selectByPluginUid(pluginUid);
        if (CollectionUtil.isEmpty(pluginConfigList)) {
            return;
        }
        pluginConfigMapper.deleteBatchIds(pluginConfigList.stream().map(PluginConfigDO::getId).collect(Collectors.toList()));
        pluginConfigList.forEach(config -> {
            businessJobApi.deleteByForeignKey(config.getUid());
        });
    }

    @Override
    public PluginConfigRespVO getByLibrary(String libraryUid, String pluginUid) {
        PluginConfigDO pluginConfigDO = pluginConfigMapper.selectByLibraryUid(libraryUid, pluginUid);
        return PluginConfigConvert.INSTANCE.convert(pluginConfigDO);
    }


    @Override
    public List<PluginConfigRespVO> configList(String libraryUid) {
        List<PluginConfigDO> pluginConfigDOList = pluginConfigMapper.selectByLibraryUid(libraryUid);
        return PluginConfigConvert.INSTANCE.convert(pluginConfigDOList);
    }

    @Override
    public PluginConfigDO getByUid(String uid) {
        PluginConfigDO pluginConfigDO = pluginConfigMapper.selectByUid(uid);
        if (Objects.isNull(pluginConfigDO)) {
            throw exception(PLUGIN_CONFIG_NOT_EXIST, "uid :" + uid);
        }
        return pluginConfigDO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyPluginConfig(String sourceUid, String targetUid, PluginBindTypeEnum typeEnum) {
        List<PluginConfigDO> pluginConfigDOList = pluginConfigMapper.selectByLibraryUid(sourceUid);
        deleteByLibraryUid(targetUid);
        for (PluginConfigDO pluginConfigDO : pluginConfigDOList) {
            insertPluginConfig(pluginConfigDO, targetUid, typeEnum);
        }
    }

    @Override
    public void updatePluginConfig(String sourceUid, String targetUid) {
        Map<String, PluginConfigDO> sourceConfigMap = pluginConfigMapper.selectByLibraryUid(sourceUid).stream().collect(Collectors.toMap(PluginConfigDO::getPluginUid, Function.identity(), (a, b) -> a));
        List<PluginConfigDO> targetConfigList = pluginConfigMapper.selectByLibraryUid(targetUid);
        for (PluginConfigDO pluginConfigDO : targetConfigList) {
            // 删除后剩余需新增插件
            PluginConfigDO updateConfig = sourceConfigMap.remove(pluginConfigDO.getPluginUid());
            if (Objects.nonNull(updateConfig)) {
//                 更新已有插件
                pluginConfigDO.setExecuteParams(updateConfig.getExecuteParams());
                pluginConfigDO.setFieldMap(updateConfig.getFieldMap());
                pluginConfigDO.setBindName(updateConfig.getBindName());
                pluginConfigMapper.updateById(pluginConfigDO);
//                businessJobApi.updateJob(updateConfig.getUid(), pluginConfigDO.getUid(), pluginConfigDO.getLibraryUid());
            } else {
                if (!Objects.equals(PluginBindTypeEnum.owner.getCode(), pluginConfigDO.getType())) {
                    // 删除
                    delete(pluginConfigDO.getUid(), true);
                }
            }
        }
        // 新增
        for (PluginConfigDO sourceDO : sourceConfigMap.values()) {
            insertPluginConfig(sourceDO, targetUid, PluginBindTypeEnum.sys);
        }
    }

    private void insertPluginConfig(PluginConfigDO sourceDO, String targetUid, PluginBindTypeEnum typeEnum) {
        PluginConfigDO configDO = new PluginConfigDO();
        configDO.setUid(IdUtil.fastSimpleUUID());
        configDO.setExecuteParams(sourceDO.getExecuteParams());
        configDO.setFieldMap(sourceDO.getFieldMap());
        configDO.setLibraryUid(targetUid);
        configDO.setPluginUid(sourceDO.getPluginUid());
        configDO.setType(typeEnum.getCode());
        configDO.setBindName(sourceDO.getBindName());
        pluginConfigMapper.insert(configDO);
//        复制插件不再copy定时任务
//        businessJobApi.copyJob(sourceDO.getUid(), configDO.getUid(), configDO.getLibraryUid());
    }

    private void deleteByLibraryUid(String libraryUid) {
        List<PluginConfigDO> pluginConfigDOList = pluginConfigMapper.selectByLibraryUid(libraryUid);
        if (CollectionUtil.isEmpty(pluginConfigDOList)) {
            return;
        }
        pluginConfigMapper.deleteBatchIds(pluginConfigDOList.stream().map(PluginConfigDO::getId).collect(Collectors.toList()));
        pluginConfigDOList.forEach(config -> {
            businessJobApi.deleteByForeignKey(config.getUid());
        });
    }
}
