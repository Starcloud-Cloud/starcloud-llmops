package com.starcloud.ops.business.app.service.xhs.material;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.BindMigrationReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.SliceMigrationReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceUseRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanMaterialDO;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.enums.materiallibrary.ColumnTypeEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialBindTypeEnum;
import com.starcloud.ops.business.app.enums.plugin.PluginBindTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialFieldTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialUsageModel;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import com.starcloud.ops.business.app.service.plugins.PluginConfigService;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.app.utils.MaterialDefineUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CreativeMaterialManager {

    @Resource
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibraryAppBindService bindService;

    @Resource
    private MaterialLibrarySliceService sliceService;

    @Resource
    private PluginConfigService pluginConfigService;

    @Resource
    private MaterialLibraryTableColumnService columnService;

    /**
     * 删除素材库
     */
    public void deleteMaterial(String uid) {
        log.info("delete material library,uid={}", uid);
        try {
            MaterialLibraryAppReqVO reqVO = new MaterialLibraryAppReqVO();
            reqVO.setAppUid(uid);
            materialLibraryService.deleteMaterialLibraryByApp(reqVO);
        } catch (Exception e) {
            log.warn("delete material error,{}", e.getMessage(), e);
        }
    }

    /**
     * 查询表头 分组配置
     */
    public List<MaterialFieldConfigDTO> getHeader(String appUid) {
        if (StringUtils.isBlank(appUid)) {
            return Collections.emptyList();
        }
        List<MaterialLibraryTableColumnRespVO> headers = queryHeader(appUid);
        List<MaterialFieldConfigDTO> result = new ArrayList<>();
        for (MaterialLibraryTableColumnRespVO tableColumnRespVO : headers) {
            MaterialFieldConfigDTO fieldConfigDTO = new MaterialFieldConfigDTO();
            fieldConfigDTO.setFieldName(tableColumnRespVO.getColumnCode());
            fieldConfigDTO.setDesc(tableColumnRespVO.getColumnName());
            fieldConfigDTO.setIsGroupField(tableColumnRespVO.getIsGroupColumn());
            result.add(fieldConfigDTO);
        }
        return result;
    }

    /**
     * 判断素材内容显示类型 true显示图片 false显示列表
     */
    public Boolean judgePicture(String uid) {
        List<MaterialFieldConfigDTO> header = getHeader(uid);
        return !CollectionUtils.isEmpty(header)
                && header.size() == 1
                && MaterialFieldTypeEnum.image.getCode().equalsIgnoreCase(header.get(0).getType());
    }


    /**
     * appuid未绑定素材库  新建空素材库
     */
    public void createEmptyLibrary(String appName, String appUid, Integer appType, Long creator) {
        AtomicBoolean exist = new AtomicBoolean(false);
        DataPermissionUtils.executeIgnore(() -> {
            MaterialLibraryAppBindDO appBind = bindService.getMaterialLibraryAppBind(appUid);
            if (Objects.nonNull(appBind)) {
                log.warn("material library exists,uid={},appType={}", appUid, appType);
                exist.set(true);
            }
        });

        if (exist.get()) {
            return;
        }

        MaterialLibraryAppReqVO appReqV = new MaterialLibraryAppReqVO();
        appReqV.setAppName(appName);
        appReqV.setAppUid(appUid);
        appReqV.setAppType(appType);
        appReqV.setUserId(creator);

        long start = System.currentTimeMillis();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        WebFrameworkUtils.setLoginUserId(WebFrameworkUtils.getRequest(), creator);
        materialLibraryService.createMaterialLibraryByApp(appReqV);
        WebFrameworkUtils.setLoginUserId(WebFrameworkUtils.getRequest(), loginUserId);
        long end = System.currentTimeMillis();
        log.info("create empty material library,appUid={}, {}", appUid, end - start);
    }

    /**
     * 迁移旧素材数据 从变量迁移
     */
    public void migrateFromConfig(String appName, String appUid, Integer appType,
                                  String materialLibraryJsonVariable, Long creator) {
        if (StringUtils.isBlank(materialLibraryJsonVariable)) {
            return;
        }
        log.info("start migrate material, appName={}", appName);

        List<Map> list = JSONUtil.parseArray(materialLibraryJsonVariable).toList(Map.class);
        BeanPath beanPath = new BeanPath("[0].libraryUid");
        Object uid = beanPath.get(list);
        if (Objects.isNull(uid)) {
            return;
        }
        BindMigrationReqVO bindMigrationReqVO = new BindMigrationReqVO();
        bindMigrationReqVO.setAppName(appName);
        bindMigrationReqVO.setAppUid(appUid);
        bindMigrationReqVO.setAppType(appType);
        bindMigrationReqVO.setLibraryUid(uid.toString());
        bindMigrationReqVO.setUserId(creator);

        long start = System.currentTimeMillis();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        WebFrameworkUtils.setLoginUserId(WebFrameworkUtils.getRequest(), creator);
        bindService.createMaterialLibraryAppBind(bindMigrationReqVO);
        WebFrameworkUtils.setLoginUserId(WebFrameworkUtils.getRequest(), loginUserId);
        long end = System.currentTimeMillis();
        log.info("migrate from config, uid={}, type={} {}", appUid, appType, end - start);
    }

    /**
     * 迁移旧素材数据 从库里迁移
     */
    public void migrateFromData(String appName, String appUid, Integer appType,
                                WorkflowStepWrapperRespVO materialHandler,
                                List<Map<String, Object>> materialList, Long creator) {
        log.info("start migrate material, appName={}", appName);
        String materialDefine = materialHandler.getVariableToString(CreativeConstants.MATERIAL_DEFINE);
        if (StringUtils.isBlank(materialDefine)) {
            return;
        }
        List<MaterialFieldConfigDTO> materialFieldConfigDTOList = MaterialDefineUtil.parseConfig(materialDefine);
        if (CollectionUtil.isEmpty(materialFieldConfigDTOList)) {
            return;
        }

        List<MaterialLibraryTableColumnSaveReqVO> tableColumnConfigList = new ArrayList<>(materialFieldConfigDTOList.size());
        for (MaterialFieldConfigDTO materialFieldConfigDTO : materialFieldConfigDTOList) {
            MaterialLibraryTableColumnSaveReqVO saveReqVO = convertMaterialLibraryTableColumn(materialFieldConfigDTO);
            tableColumnConfigList.add(saveReqVO);
        }

        SliceMigrationReqVO sliceMigrationReqVO = new SliceMigrationReqVO();
        sliceMigrationReqVO.setMaterialList(materialList);
        sliceMigrationReqVO.setTableColumnSaveReqVOS(tableColumnConfigList);
        sliceMigrationReqVO.setAppName(appName);
        sliceMigrationReqVO.setAppUid(appUid);
        sliceMigrationReqVO.setAppType(appType);
        sliceMigrationReqVO.setUserId(creator);

        long start = System.currentTimeMillis();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        WebFrameworkUtils.setLoginUserId(WebFrameworkUtils.getRequest(), creator);
        materialLibraryService.materialLibraryDataMigration(sliceMigrationReqVO);
        WebFrameworkUtils.setLoginUserId(WebFrameworkUtils.getRequest(), loginUserId);
        long end = System.currentTimeMillis();
        log.info("material library migrate from data,uid={},type={} {}", appUid, appType, end - start);
    }

    /**
     * 旧表头结构转成新表头结构
     */
    private static MaterialLibraryTableColumnSaveReqVO convertMaterialLibraryTableColumn(MaterialFieldConfigDTO materialFieldConfigDTO) {
        MaterialLibraryTableColumnSaveReqVO saveReqVO = new MaterialLibraryTableColumnSaveReqVO();
        saveReqVO.setColumnCode(materialFieldConfigDTO.getFieldName());
        saveReqVO.setColumnName(materialFieldConfigDTO.getDesc());
        String fieldConfigType = materialFieldConfigDTO.getType();
        if (MaterialFieldTypeEnum.image.getCode().equalsIgnoreCase(fieldConfigType)) {
            saveReqVO.setColumnType(ColumnTypeEnum.IMAGE.getCode());
        } else if (MaterialFieldTypeEnum.string.getCode().equalsIgnoreCase(fieldConfigType)) {
            saveReqVO.setColumnType(ColumnTypeEnum.STRING.getCode());
        } else if (MaterialFieldTypeEnum.textBox.getCode().equalsIgnoreCase(fieldConfigType)) {
            saveReqVO.setColumnType(ColumnTypeEnum.STRING.getCode());
        } else if (MaterialFieldTypeEnum.document.getCode().equalsIgnoreCase(fieldConfigType)) {
            saveReqVO.setColumnType(ColumnTypeEnum.DOCUMENT.getCode());
        } else {
            saveReqVO.setColumnType(ColumnTypeEnum.STRING.getCode());
        }
        saveReqVO.setIsRequired(materialFieldConfigDTO.isRequired());
        saveReqVO.setSequence((long) materialFieldConfigDTO.getOrder());
        saveReqVO.setIsGroupColumn(materialFieldConfigDTO.getIsGroupField());
        return saveReqVO;
    }

    /**
     * 我的应用复制
     */
    public void copyAppMaterial(String sourceUid, String name, String targetUid) {
        AtomicBoolean exist = new AtomicBoolean(false);
        DataPermissionUtils.executeIgnore(() -> {
            MaterialLibraryAppBindDO appBind = bindService.getMaterialLibraryAppBind(sourceUid);
            if (Objects.nonNull(appBind)) {
                log.warn("material library exists,uid={}", sourceUid);
                exist.set(true);
            }
        });

        if (!exist.get()) {
            createEmptyLibrary(name, targetUid, MaterialBindTypeEnum.APP_MAY.getCode(), WebFrameworkUtils.getLoginUserId());
            return;
        }

        MaterialLibrarySliceAppReqVO source = new MaterialLibrarySliceAppReqVO();
        source.setAppUid(sourceUid);

        MaterialLibraryAppReqVO target = new MaterialLibraryAppReqVO();
        target.setAppUid(targetUid);
        target.setAppType(MaterialBindTypeEnum.APP_MAY.getCode());
        target.setAppName(name);
        target.setUserId(WebFrameworkUtils.getLoginUserId());

        copyLibrary(source, target, PluginBindTypeEnum.owner);
    }

    /**
     * 应用市场新建执行计划 copy素材库
     */
    public void upgradeMaterialLibrary(String sourceUid, CreativePlanMaterialDO creativePlan, String appName) {
        MaterialLibrarySliceAppReqVO source = new MaterialLibrarySliceAppReqVO();
        source.setAppUid(sourceUid);

        MaterialLibraryAppReqVO target = new MaterialLibraryAppReqVO();
        target.setAppUid(creativePlan.getUid());
        target.setAppType(MaterialBindTypeEnum.CREATION_PLAN.getCode());
        target.setAppName(appName);
        target.setUserId(WebFrameworkUtils.getLoginUserId());

        copyLibrary(source, target, PluginBindTypeEnum.sys);
    }

    /**
     * 复制插件配置 定时任务
     */
    private void copyPluginConfig(String sourceUid, String targetUid, PluginBindTypeEnum typeEnum) {
        MaterialLibraryAppReqVO appReqVO = new MaterialLibraryAppReqVO();
        appReqVO.setAppUid(sourceUid);
        DataPermissionUtils.executeIgnore(() -> {
            MaterialLibraryRespVO sourceLibrary = materialLibraryService.getMaterialLibraryByApp(appReqVO);
            appReqVO.setAppUid(targetUid);
            MaterialLibraryRespVO targetLibrary = materialLibraryService.getMaterialLibraryByApp(appReqVO);
            pluginConfigService.copyPluginConfig(sourceLibrary.getUid(), targetLibrary.getUid(), typeEnum);
        });
    }


    /**
     * 应用市场初始化应用 全量更新素材库
     */
    public void upgradeMaterialLibrary(String sourceUid, String planUid) {
        long start = System.currentTimeMillis();
        MaterialLibrarySliceAppReqVO source = new MaterialLibrarySliceAppReqVO();
        source.setAppUid(sourceUid);

        MaterialLibraryAppReqVO target = new MaterialLibraryAppReqVO();
        target.setAppUid(planUid);
        target.setAppType(MaterialBindTypeEnum.CREATION_PLAN.getCode());
        copyLibrary(source, target, PluginBindTypeEnum.sys);
        long end = System.currentTimeMillis();
        log.info("full update library ,sourceUid={}, planUid={} {}", sourceUid, planUid, end - start);
    }

    /**
     * 更新表头 更新插件
     */
    @DataPermission(enable = false)
    public void upgradeColumns(String sourceUid, String planUid) {
        columnService.updateColumn(sourceUid, planUid);
        MaterialLibraryAppReqVO appReqVO = new MaterialLibraryAppReqVO();
        appReqVO.setAppUid(sourceUid);
        MaterialLibraryRespVO sourceLibrary = materialLibraryService.getMaterialLibraryByApp(appReqVO);

        appReqVO.setAppUid(planUid);
        MaterialLibraryRespVO targetLibrary = materialLibraryService.getMaterialLibraryByApp(appReqVO);
        pluginConfigService.updatePluginConfig(sourceLibrary.getUid(), targetLibrary.getUid());
    }

    /**
     * 发布应用市场 审核通过触发 copy素材库
     */
    public void upgradeMaterialLibrary(String sourceUid, AppMarketEntity appMarketEntity) {
        MaterialLibrarySliceAppReqVO source = new MaterialLibrarySliceAppReqVO();
        source.setAppUid(sourceUid);
        MaterialLibraryAppReqVO target = new MaterialLibraryAppReqVO();
        target.setAppUid(appMarketEntity.getUid());
        target.setAppType(MaterialBindTypeEnum.APP_MARKET.getCode());
        target.setAppName(appMarketEntity.getName());
        target.setUserId(WebFrameworkUtils.getLoginUserId());
        copyLibrary(source, target, PluginBindTypeEnum.sys);
    }

    /**
     * 根据素材库配置查询素材列表
     */
    public List<Map<String, Object>> getMaterialList(CreativePlanRespVO creativePlan) {
        CreativePlanConfigurationDTO configuration = creativePlan.getConfiguration();
        AppMarketRespVO appInformation = configuration.getAppInformation();
        WorkflowStepWrapperRespVO materialStepWrapper = CreativeUtils.getMaterialStepWrapper(appInformation);
        // 获取到素材使用模式
        MaterialUsageModel materialUsageModel = CreativeUtils.getMaterialUsageModelByStepWrapper(materialStepWrapper);

        // 查询素材库数据
        MaterialLibrarySliceAppReqVO materialListRequest = new MaterialLibrarySliceAppReqVO();

        // 选择模式执行查询条件构造
        if (MaterialUsageModel.SELECT.equals(materialUsageModel)) {
            materialListRequest = CreativeUtils.getSelectMaterialRequestByStepWrapper(materialStepWrapper);
        } else {
            // 构造排序条件
            SortingField sortingField = new SortingField();
            sortingField.setOrder(SortingField.ORDER_ASC);
            sortingField.setField(MaterialLibrarySliceAppReqVO.SORT_FIELD_USED_COUNT);
            materialListRequest.setSortingField(sortingField);
            materialListRequest.setLibraryUid(null);
        }

        // 设置应用UID
        String source = creativePlan.getSource();
        if (CreativePlanSourceEnum.isApp(source)) {
            materialListRequest.setAppUid(appInformation.getUid());
        } else {
            materialListRequest.setAppUid(creativePlan.getUid());
        }

        long start = System.currentTimeMillis();
        log.info("查询素材列表整体参数：{}", JsonUtils.toJsonString(materialListRequest));
        MaterialLibrarySliceUseRespVO materialLibrarySlice = materialLibraryService.getMaterialLibrarySlice(materialListRequest);
        long end = System.currentTimeMillis();
        log.info("查询素材列表整体耗时, {}", end - start);
        return convert(materialLibrarySlice);
    }

    /**
     * 复制素材库
     */
    public void copyLibrary(MaterialLibrarySliceAppReqVO source, MaterialLibraryAppReqVO target, PluginBindTypeEnum typeEnum) {
        log.info("start material library copy, sourceUid={}, targetUid={}", source.getAppUid(), target.getAppUid());
        long start = System.currentTimeMillis();
        materialLibraryService.materialLibraryCopy(target, source);
        long end = System.currentTimeMillis();
        log.info("material library copy, {}", end - start);
        copyPluginConfig(source.getAppUid(), target.getAppUid(), typeEnum);
    }

    /**
     * 查询素材库数据
     * 暂时只取第一个素材库数据 jsonschema只生成第一个素材库表头
     */
    private List<MaterialLibrarySliceRespVO> queryLibrary(String uid) {
        log.info("start material library query, uid={}", uid);
        long start = System.currentTimeMillis();
        List<MaterialLibrarySliceRespVO> librarySliceResp = sliceService.getMaterialLibrarySliceListByAppUid(uid);
        long end = System.currentTimeMillis();
        log.info("material library query, {}", end - start);
        return librarySliceResp;
    }

    /**
     * 查询表头
     *
     * @param uid
     * @return
     */
    private List<MaterialLibraryTableColumnRespVO> queryHeader(String uid) {
        MaterialLibraryAppReqVO appReqVO = new MaterialLibraryAppReqVO();
        appReqVO.setAppUid(uid);
        long start = System.currentTimeMillis();
        MaterialLibraryRespVO materialLibraryByUid = materialLibraryService.getMaterialLibraryByApp(appReqVO);
        long end = System.currentTimeMillis();
        log.info("material library query header, {}", end - start);
        if (Objects.isNull(materialLibraryByUid) || CollectionUtil.isEmpty(materialLibraryByUid.getTableMeta())) {
            return Collections.emptyList();
        }
        return materialLibraryByUid.getTableMeta();
    }

    /**
     * 素材库数据转成 Map
     */
    private List<Map<String, Object>> convert(MaterialLibrarySliceUseRespVO librarySlice) {
        if (Objects.isNull(librarySlice)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        List<MaterialLibrarySliceRespVO> sliceRespList = librarySlice.getSliceRespVOS();
        List<MaterialLibraryTableColumnRespVO> tableMeta = librarySlice.getTableMeta();
        Map<String, Integer> columnCodeType = tableMeta.stream().collect(Collectors.toMap(MaterialLibraryTableColumnRespVO::getColumnCode, MaterialLibraryTableColumnRespVO::getColumnType, (a, b) -> a));
        for (MaterialLibrarySliceRespVO sliceRespVO : sliceRespList) {
            List<MaterialLibrarySliceRespVO.TableContent> tableContentList = sliceRespVO.getContent();
            if (CollectionUtil.isEmpty(tableContentList)) {
                continue;
            }
            Map<String, Object> row = new HashMap<>();
            row.put("__id__", sliceRespVO.getId());
            row.put("__usageCount__", sliceRespVO.getUsedCount());
            for (MaterialLibrarySliceRespVO.TableContent tableContent : tableContentList) {
                if (Objects.isNull(tableContent)) {
                    continue;
                }
                row.put(tableContent.getColumnCode(), tableContent.getValue());
                Integer typeCode = columnCodeType.get(tableContent.getColumnCode());
                String extend = tableContent.getExtend();

                if (ColumnTypeEnum.IMAGE.getCode().equals(typeCode) && StringUtils.isNotBlank(extend)) {
                    Map<String, Object> map = JSONObject.parseObject(extend, new TypeReference<Map<String, Object>>() {
                    });
                    map.put("content", tableContent.getDescription());
                    if (CollectionUtil.isNotEmpty(tableContent.getTags())) {
                        map.put("tag", tableContent.getTags().stream().collect(Collectors.joining(",")));
                    }
                    row.put(tableContent.getColumnCode() + "_ext", map);
                }
            }
            result.add(row);
        }
        return result;
    }
}
