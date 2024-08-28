package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONException;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.*;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceUseRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryMapper;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialBindTypeEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialFormatTypeEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryTypeEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import com.starcloud.ops.business.app.service.materiallibrary.handle.ExcelMaterialImportStrategy;
import com.starcloud.ops.business.app.service.materiallibrary.handle.ImageMaterialImportStrategy;
import com.starcloud.ops.business.app.service.materiallibrary.handle.MaterialImportStrategy;
import com.starcloud.ops.business.app.service.materiallibrary.handle.ZipMaterialImportStrategy;
import com.starcloud.ops.business.app.util.MaterialTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.DOWNLOAD_TEMPLATE_ERROR;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;
import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.*;

/**
 * 素材知识库 Service 实现类
 *
 * @author starcloudadmin
 */
@Slf4j
@Service
@Validated
public class MaterialLibraryServiceImpl implements MaterialLibraryService {


    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MaterialLibrarySliceService materialLibrarySliceService;

    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    @Resource
    private MaterialLibraryAppBindService materialLibraryAppBindService;

    @Resource
    private MaterialLibraryMapper materialLibraryMapper;


    @Override
    public Long createSystemMaterialLibrary(MaterialLibrarySaveReqVO createReqVO) {
        // 插入
        MaterialLibraryDO materialLibrary = saveMaterialLibrary(createReqVO);
        // 返回
        return materialLibrary.getId();
    }

    /**
     * 通过应用名称创建素材知识库
     *
     * @param appName 应用名称
     * @return 编号
     */
    @Override
    public Long createSystemMaterialLibrary(String appName, Integer createSource) {
        Assert.notBlank(appName, "素材库名称不可以为空,创建素材库失败");
        Assert.notNull(appName, "素材库类型不可以为空,创建素材库失败");
        String name;
        int libraryType;
        if (Objects.equals(MaterialBindTypeEnum.MEMBER_COPY.getCode(), createSource)) {
            libraryType = MaterialLibraryTypeEnum.MEMBER.getCode();
            name = appName;
        } else if (Objects.equals(MaterialBindTypeEnum.APP_MARKET.getCode(), createSource)) {
            libraryType = MaterialLibraryTypeEnum.SYSTEM.getCode();
            name = StrUtil.format(MATERIAL_LIBRARY_TEMPLATE_PUBLISH, appName);
        } else if (ObjectUtils.equalsAny(createSource, MaterialBindTypeEnum.APP_MAY.getCode(), MaterialBindTypeEnum.CREATION_PLAN.getCode())) {
            libraryType = MaterialLibraryTypeEnum.SYSTEM.getCode();
            name = StrUtil.format(MATERIAL_LIBRARY_TEMPLATE_SYSTEM, appName);
        } else {
            libraryType = MaterialLibraryTypeEnum.SYSTEM.getCode();
            name = StrUtil.format(MATERIAL_LIBRARY_TEMPLATE_SYSTEM, appName);
        }
        MaterialLibraryDO materialLibrary = saveMaterialLibrary(new MaterialLibrarySaveReqVO().setName(name).setLibraryType(libraryType).setCreateSource(createSource));
        return materialLibrary.getId();
    }

    /**
     * 通过应用创建素材知识库
     *
     * @param appReqVO 应用名称
     */
    @Override
    public void createMaterialLibraryByApp(MaterialLibraryAppReqVO appReqVO) {

        AppValidate.notBlank(appReqVO.getAppName(), "获取素材库失败，应用名称不能为空");
        AppValidate.notBlank(appReqVO.getAppUid(), "获取素材库失败，应用编号不能为空");
        AppValidate.notNull(appReqVO.getAppType(), "获取素材库失败，应用类型不能为空");
        AppValidate.notNull(appReqVO.getUserId(), "获取素材库失败，用户编号不能为空");

        String name = StrUtil.format(MATERIAL_LIBRARY_TEMPLATE_SYSTEM, appReqVO.getAppName());

        // 创建系统素材库
        MaterialLibraryDO materialLibrary = saveMaterialLibrary(new MaterialLibrarySaveReqVO().setName(name).setLibraryType(MaterialLibraryTypeEnum.SYSTEM.getCode()).setCreateSource(appReqVO.getAppType()));
        // 添加绑定关系
        materialLibraryAppBindService.createMaterialLibraryAppBind(new MaterialLibraryAppBindSaveReqVO().setLibraryId(materialLibrary.getId()).setAppUid(appReqVO.getAppUid()).setAppType(appReqVO.getAppType()).setUserId(appReqVO.getUserId()));

    }


    /**
     * 通过应用获取绑定的素材知识库
     *
     * @param appReqVO@return 编号
     */
    @Override
    public MaterialLibraryRespVO getMaterialLibraryByApp(MaterialLibraryAppReqVO appReqVO) {
        AppValidate.notBlank(appReqVO.getAppUid(), "获取素材库失败，应用编号不能为空");

        MaterialLibraryAppBindDO bind = materialLibraryAppBindService.getMaterialLibraryAppBind(appReqVO.getAppUid());

        MaterialLibraryDO materialLibrary;
        if (Objects.isNull(bind)) {
            throw exception(MATERIAL_LIBRARY_NO_BIND_APP);
        }

        materialLibrary = validateMaterialLibraryExists(bind.getLibraryId());

        // 数据转换
        MaterialLibraryRespVO bean = BeanUtils.toBean(materialLibrary, MaterialLibraryRespVO.class);

        if (MaterialFormatTypeEnum.isExcel(materialLibrary.getFormatType())) {
            List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(materialLibrary.getId());
            bean.setTableMeta(BeanUtils.toBean(tableColumnDOList, MaterialLibraryTableColumnRespVO.class));
        }
        return bean;


    }


    @Override
    public void updateMaterialLibrary(MaterialLibrarySaveReqVO updateReqVO) {
        MaterialLibraryDO materialLibraryDO = materialLibraryMapper.selectById(updateReqVO.getId());

        if (materialLibraryDO == null) {
            throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
        }
        if (!materialLibraryDO.getFormatType().equals(updateReqVO.getFormatType())) {
            throw exception(MATERIAL_LIBRARY_FORAMT_NO_MODIFY);

        }
        // 更新
        MaterialLibraryDO updateObj = BeanUtils.toBean(updateReqVO, MaterialLibraryDO.class);
        materialLibraryMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaterialLibrary(Long id) {

        MaterialLibraryDO libraryDO = materialLibraryMapper.selectById(id);

        if (libraryDO == null) {
            throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
        }

        // 删除表头信息
        if (MaterialFormatTypeEnum.isExcel(libraryDO.getFormatType())) {
            materialLibraryTableColumnService.deleteMaterialLibraryTableColumnByLibraryId(id);
        }
        // 具体素材库数据
        materialLibrarySliceService.deleteMaterialLibrarySliceByLibraryId(id);
        // 删除素材库
        materialLibraryMapper.deleteById(id);
    }

    /**
     * 通过应用删除素材知识库
     *
     * @param appReqVO 编号
     */
    @Override
    public void deleteMaterialLibraryByApp(MaterialLibraryAppReqVO appReqVO) {
        AppValidate.notBlank(appReqVO.getAppUid(), "获取素材库失败，应用编号不能为空");

        MaterialLibraryAppBindDO bind = materialLibraryAppBindService.getMaterialLibraryAppBind(appReqVO.getAppUid());
        if (Objects.isNull(bind)) {
            throw exception(MATERIAL_LIBRARY_NO_BIND_APP);
        }
        List<MaterialLibraryAppBindDO> bindList = materialLibraryAppBindService.getBindList(bind.getLibraryId());

        if (bindList.size() > 1) {
            materialLibraryAppBindService.deleteMaterialLibraryAppBind(bind.getId());
            return;
        }
        // 删除绑定关系
        materialLibraryAppBindService.deleteMaterialLibraryAppBind(bind.getId());

        MaterialLibraryDO materialLibrary = validateMaterialLibraryExists(bind.getLibraryId());
        // 删除表头信息
        if (MaterialFormatTypeEnum.isExcel(materialLibrary.getFormatType())) {
            materialLibraryTableColumnService.deleteMaterialLibraryTableColumnByLibraryId(materialLibrary.getId());
        }
        // 具体素材库数据
        materialLibrarySliceService.deleteMaterialLibrarySliceByLibraryId(materialLibrary.getId());
        // 删除素材库
        materialLibraryMapper.deleteById(materialLibrary.getId());


    }

    @Override
    public MaterialLibraryDO getMaterialLibrary(Long id) {
        return materialLibraryMapper.selectById(id);
    }

    /**
     * 通过素材库UID 获取 素材库详情
     *
     * @param uid 素材库UID
     * @return 素材库详情
     */
    @Override
    public MaterialLibraryRespVO getMaterialLibraryByUid(String uid) {
        Assert.notBlank(uid, "素材库 UID 不可以为空,获取素材详情失败");

        MaterialLibraryDO materialLibrary = materialLibraryMapper.selectByUid(uid);

        if (materialLibrary == null) {
            throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
        }
        // 数据转换
        MaterialLibraryRespVO bean = BeanUtils.toBean(materialLibrary, MaterialLibraryRespVO.class);

        if (MaterialFormatTypeEnum.isExcel(materialLibrary.getFormatType())) {
            List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(materialLibrary.getId());
            bean.setTableMeta(BeanUtils.toBean(tableColumnDOList, MaterialLibraryTableColumnRespVO.class));
        }
        return bean;
    }

    @Override
    public PageResult<MaterialLibraryDO> getMaterialLibraryPage(MaterialLibraryPageReqVO pageReqVO) {
        return materialLibraryMapper.selectPage2(pageReqVO);
    }

    /**
     * 素材库验证
     *
     * @param id 素材库编号
     */
    @Override
    public MaterialLibraryDO validateMaterialLibraryExists(Long id) {
        MaterialLibraryDO materialLibraryDO = materialLibraryMapper.selectById(id);

        if (materialLibraryDO == null) {
            throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
        }
        return materialLibraryDO;
    }

    /**
     * 素材库验证
     *
     * @param uid 素材库编号
     */
    @Override
    public MaterialLibraryDO validateMaterialLibraryExists(String uid) {
        MaterialLibraryDO materialLibraryDO = materialLibraryMapper.selectByUid(uid);

        if (materialLibraryDO == null) {
            throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
        }
        return materialLibraryDO;
    }


    /**
     * 导入素材库数据
     *
     * @param importRespVO 导入数据的 VO
     */
    @Override
    public void importMaterialData(MaterialLibraryImportReqVO importRespVO) {
        // 验证数据类型
        validateMaterialLibraryExists(importRespVO.getLibraryId());
        // 根据素材类型获取素材执行策略
        MaterialImportStrategy strategy = getImportStrategy(importRespVO.getMaterialType());
        // 导入素材
        strategy.importMaterial(importRespVO);
    }

    /**
     * 导出表格素材模板
     *
     * @param id       素材库 编号
     * @param response response
     */
    @Override
    public void exportTemplate(Long id, HttpServletResponse response) {

        MaterialLibraryDO libraryDO = materialLibraryMapper.selectById(id);

        if (libraryDO == null) {
            throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
        }

        // 删除表头信息
        if (!MaterialFormatTypeEnum.isExcel(libraryDO.getFormatType())) {
            throw exception(MATERIAL_LIBRARY_EXPORT_FAIL_ERROR_TYPE);
        }
        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(id);
        if (CollUtil.isEmpty(tableColumnDOList)) {
            throw exception(MATERIAL_LIBRARY_EXPORT_FAIL_COULMN_EMPTY);
        }
        List<String> columnNames = tableColumnDOList.stream().map(MaterialLibraryTableColumnDO::getColumnName).collect(Collectors.toList());
        try {
            String zipNamePrefix = StrUtil.format("{}-{}", libraryDO.getName(), TEMPLATE_FILE_SUFFIX);
            String excelNamePrefix = "导入模板";
            File file = MaterialTemplateUtils.readTemplate(zipNamePrefix, excelNamePrefix, String.valueOf(libraryDO.getId()), columnNames);
            IoUtil.write(response.getOutputStream(), false, FileUtil.readBytes(file));
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        } catch (JSONException e) {
            log.error("JSON Exception", e);
            throw exception(DOWNLOAD_TEMPLATE_ERROR, "自定义配置解析错误");
        } catch (Exception e) {
            log.error("generation template error", e);
            throw exception(DOWNLOAD_TEMPLATE_ERROR, e.getMessage());
        }


    }


    /**
     * 获取应用执行的素材
     *
     * @param appReqVO 素材库编号
     */
    @Override
    public List<MaterialLibrarySliceUseRespVO> getMaterialLibrarySliceList(List<MaterialLibrarySliceAppReqVO> appReqVO) {
        return appReqVO.stream()
                .map(this::selectMaterialLibrarySliceList)
                .collect(Collectors.toList());
    }

    /**
     * 获取应用执行的素材
     *
     * @param appReqVO 素材库查询
     */
    @Override
    public MaterialLibrarySliceUseRespVO getMaterialLibrarySlice(MaterialLibrarySliceAppReqVO appReqVO) {
        return this.selectMaterialLibrarySliceList(appReqVO);
    }

    /**
     * 素材库-》素材库的复制（包含全部数据）
     *
     * @param copyReqVO 模板素材库VO
     * @return 素材库 UID
     */
    @Override
    public Long materialLibraryCopy(MaterialLibraryCopyReqVO copyReqVO) {
        // 复制素材库
        Long newLibraryId = materialLibraryCopy(copyReqVO.getId(), copyReqVO.getName(), MaterialBindTypeEnum.MEMBER_COPY.getCode());
        // 复制表头数据
        materialLibraryTableColumnService.materialLibraryCopy(copyReqVO.getId(), newLibraryId);

        if (!copyReqVO.getCopyAll()) {
            return newLibraryId;
        }
        // 复制表数据
        materialLibrarySliceService.materialLibrarySliceCopy(copyReqVO.getId(), newLibraryId);
        return newLibraryId;
    }


    /**
     * @param newApp 新应用
     * @param oldApp 旧应用
     */
    @Override
    public void materialLibraryCopy(MaterialLibraryAppReqVO newApp, MaterialLibraryAppReqVO oldApp) {
        Assert.notNull(newApp.getAppType(), "素材库复制失败，新应用类型为空");
        AtomicReference<MaterialLibraryAppBindDO> templateBind = new AtomicReference<>();

        // 关闭数据权限，避免因为没有数据权限，查询不到数据，进而导致唯一校验不正确
        DataPermissionUtils.executeIgnore(() -> {

            MaterialLibraryAppBindDO bind = materialLibraryAppBindService.getMaterialLibraryAppBind(oldApp.getAppUid());

            if (bind == null) {
                throw exception(MATERIAL_LIBRARY_NO_BIND_APP);
            }
            templateBind.set(bind);

            validateMaterialLibraryExists(templateBind.get().getLibraryId());

        });

        MaterialLibraryAppBindDO bind = materialLibraryAppBindService.getMaterialLibraryAppBind(newApp.getAppUid());

        if (bind != null) {
            MaterialLibraryDO materialLibrary = validateMaterialLibraryExists(bind.getLibraryId());

            // if (MaterialBindTypeEnum.isAppMarket(bind.getAppType())) {
            //     if (!MaterialLibraryTypeEnum.isMember(materialLibrary.getLibraryType())) {
            deleteMaterialLibrary(materialLibrary.getId());
            //     }
            // }
        }


        // 复制素材库
        Long newLibraryId = materialLibraryCopy(templateBind.get().getLibraryId(), newApp.getAppName(), newApp.getAppType());
        // 复制表头数据
        materialLibraryTableColumnService.materialLibraryCopy(templateBind.get().getLibraryId(), newLibraryId);
        // 复制表数据
        materialLibrarySliceService.materialLibrarySliceCopy(templateBind.get().getLibraryId(), newLibraryId);
        // 添加绑定关系
        materialLibraryAppBindService.createMaterialLibraryAppBind(new MaterialLibraryAppBindSaveReqVO().setLibraryId(newLibraryId).setAppUid(newApp.getAppUid()).setAppType(newApp.getAppType()).setUserId(newApp.getUserId()));

    }

    /**
     * 仅仅复制一个新的素材库
     *
     * @param libraryId 素材库编号
     */
    @Override
    public Long materialLibraryCopy(Long libraryId, MaterialLibraryAppReqVO appReqVO) {

        return this.materialLibraryCopy(libraryId, appReqVO.getAppName(), appReqVO.getAppType());
    }

    /**
     * 素材数据迁移
     *
     * @param migrationReqVO 迁移 VO
     */
    @Override
    public void materialLibraryDataMigration(SliceMigrationReqVO migrationReqVO) {
        this.createMaterialLibraryByApp(migrationReqVO);

        MaterialLibraryRespVO materialLibrary = this.getMaterialLibraryByApp(migrationReqVO);

        List<MaterialLibraryTableColumnSaveReqVO> tableColumnSaveReqVOS = migrationReqVO.getTableColumnSaveReqVOS();
        if (CollUtil.isEmpty(tableColumnSaveReqVOS)) {
            log.info("materialLibraryCopy:Skip migration if table header is empty");
            return;
        }

        tableColumnSaveReqVOS.forEach(data -> data.setLibraryId(materialLibrary.getId()));
        materialLibraryTableColumnService.saveBatchData(tableColumnSaveReqVOS);

        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(materialLibrary.getId());

        if (migrationReqVO.getMaterialList() != null && !migrationReqVO.getMaterialList().isEmpty()) {

            List<MaterialLibrarySliceSaveReqVO> sliceDOList = new ArrayList<>();

            migrationReqVO.getMaterialList().forEach(material -> {
                MaterialLibrarySliceSaveReqVO sliceSaveReqVO = new MaterialLibrarySliceSaveReqVO();
                sliceSaveReqVO.setLibraryId(materialLibrary.getId());
                sliceSaveReqVO.setStatus(true);
                sliceSaveReqVO.setIsShare(false);

                List<MaterialLibrarySliceSaveReqVO.TableContent> contents = new ArrayList<>();

                material.forEach((key, value) -> {
                    MaterialLibrarySliceSaveReqVO.TableContent tableContent = new MaterialLibrarySliceSaveReqVO.TableContent();

                    MaterialLibraryTableColumnDO columnDO = findColumnDOByCode(tableColumnDOList, key);
                    if (columnDO != null) {
                        tableContent.setValue(ObjectUtil.toString(value));
                        tableContent.setColumnCode(columnDO.getColumnCode());
                        tableContent.setColumnName(columnDO.getColumnName());
                        tableContent.setColumnId(columnDO.getId());
                        contents.add(tableContent);
                    }
                });
                sliceSaveReqVO.setContent(contents);
                sliceDOList.add(sliceSaveReqVO);

            });
            materialLibrarySliceService.saveBatchData(sliceDOList);
        }
    }

    /**
     * 更新素材库插件配置
     *
     * @param loginUserId       用户编号
     * @param plugInConfigReqVO 插件配置 VO
     */
    @Override
    public void updatePluginConfig(Long loginUserId, MaterialLibrarySavePlugInConfigReqVO plugInConfigReqVO) {
        MaterialLibraryDO materialLibraryDO = materialLibraryMapper.selectByIdAndUser(loginUserId, plugInConfigReqVO.getId());

        if (materialLibraryDO == null) {
            throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
        }

        materialLibraryMapper.updateById(new MaterialLibraryDO().setId(plugInConfigReqVO.getId()).setPluginConfig(plugInConfigReqVO.getPluginConfig()));

    }

    /**
     * 素材数据使用计数
     *
     * @param sliceUsageCountReqVO 表头计数 VO
     */
    @Override
    public void materialLibrarySliceUsageCount(SliceUsageCountReqVO sliceUsageCountReqVO) {
        MaterialLibraryRespVO materialLibrary = getMaterialLibraryByApp(sliceUsageCountReqVO);
        sliceUsageCountReqVO.getSliceCountReqVOS().forEach(sliceCountReqVO -> materialLibrarySliceService.updateSliceUsedCount(materialLibrary.getId(), sliceCountReqVO.getSliceId(), sliceCountReqVO.getNums()));
    }

    // /**
    //  * 更新素材库文件数量
    //  *
    //  * @param libraryId 素材库编号
    //  */
    // @Override
    // public void updateMaterialLibraryFileCount(Long libraryId) {
    //     try {
    //         long size = materialLibrarySliceService.getMaterialLibrarySliceByLibraryId(libraryId).size();
    //         materialLibraryMapper.updateById(new MaterialLibraryDO().setFileCount(size).setId(libraryId));
    //     } catch (RuntimeException e) {
    //         log.error("素材库文件数更新失败，素材库编号为:({})", libraryId, e);
    //     }
    //
    // }


    // ========================================私有方法区 ========================================
    private Long materialLibraryCopy(Long libraryId, String libraryName, Integer createSource) {

        // 关闭数据权限，避免因为没有数据权限，查询不到数据，进而导致唯一校验不正确
        AtomicReference<MaterialLibraryDO> materialLibraryDO = new AtomicReference<>();
        DataPermissionUtils.executeIgnore(() -> materialLibraryDO.set(validateMaterialLibraryExists(libraryId)));

        String name = Objects.isNull(libraryName) ? materialLibraryDO.get().getName() : libraryName;

        // String newName = "";
        // // 检查字符串是否包含下划线
        // int underscoreIndex = name.indexOf("_");
        // // 判断是否存在下划线 存在则取下划线前的字符 如果下划线前的字符为空 则不做处理
        // if (underscoreIndex != -1) {
        //     // 如果存在下划线，截取下划线前的部分
        //     newName = name.substring(0, underscoreIndex);
        // }
        // if (StrUtil.isBlank(newName)) {
        //     newName = name;
        // }

        return createSystemMaterialLibrary(name, createSource);
    }

    private MaterialLibraryDO saveMaterialLibrary(MaterialLibrarySaveReqVO createReqVO) {
        // 插入
        MaterialLibraryDO materialLibrary = BeanUtils.toBean(createReqVO, MaterialLibraryDO.class);
        materialLibrary.setUid(IdUtil.fastSimpleUUID());
        materialLibrary.setAllFileSize(0L);
        materialLibrary.setStatus(true);
        materialLibrary.setTotalUsedCount(0L);
        materialLibraryMapper.insert(materialLibrary);

        // 返回
        return materialLibrary;
    }


    /**
     * 查询指定素材库下的指定数据
     *
     * @param appReqVO 查询 VO
     * @return MaterialLibrarySliceUseRespVO
     */
    private MaterialLibrarySliceUseRespVO selectMaterialLibrarySliceList(MaterialLibrarySliceAppReqVO appReqVO) {
        if (Objects.nonNull(appReqVO.getLibraryUid())) {

            MaterialLibrarySliceUseRespVO sliceUseRespVO = new MaterialLibrarySliceUseRespVO();
            MaterialLibraryDO materialLibraryDO = materialLibraryMapper.selectByUid(appReqVO.getLibraryUid());
            if (materialLibraryDO == null) {
                throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
            }
            sliceUseRespVO.setLibraryId(sliceUseRespVO.getLibraryId());
            if (MaterialFormatTypeEnum.isExcel(materialLibraryDO.getFormatType())) {
                List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(materialLibraryDO.getId());
                sliceUseRespVO.setTableMeta(BeanUtils.toBean(tableColumnDOList, MaterialLibraryTableColumnRespVO.class));
            }

            List<MaterialLibrarySliceRespVO> sliceRespVOS = materialLibrarySliceService.selectSliceBySortingField(materialLibraryDO.getId(), appReqVO.getSliceIdList(), appReqVO.getRemovesliceIdList(), appReqVO.getSortingField());

            sliceUseRespVO.setSliceRespVOS(sliceRespVOS);
            return sliceUseRespVO;
        }
        MaterialLibrarySliceUseRespVO sliceUseRespVO = new MaterialLibrarySliceUseRespVO();

        MaterialLibraryRespVO materialLibrary;
        try {
            materialLibrary = this.getMaterialLibraryByApp(appReqVO);
        } catch (ServiceException e) {
            return new MaterialLibrarySliceUseRespVO();
        }

        sliceUseRespVO.setLibraryId(sliceUseRespVO.getLibraryId());
        if (MaterialFormatTypeEnum.isExcel(materialLibrary.getFormatType())) {
            List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(materialLibrary.getId());
            sliceUseRespVO.setTableMeta(BeanUtils.toBean(tableColumnDOList, MaterialLibraryTableColumnRespVO.class));
        }

        List<MaterialLibrarySliceRespVO> sliceRespVOS = materialLibrarySliceService.selectSliceBySortingField(materialLibrary.getId(), appReqVO.getSliceIdList(), appReqVO.getRemovesliceIdList(), appReqVO.getSortingField());

        sliceUseRespVO.setSliceRespVOS(sliceRespVOS);
        return sliceUseRespVO;
    }


    private MaterialLibraryTableColumnDO findColumnDOByCode(List<MaterialLibraryTableColumnDO> tableColumnDOList, String columnCode) {
        for (MaterialLibraryTableColumnDO tableColumnDO : tableColumnDOList) {
            if (tableColumnDO.getColumnCode().equals(columnCode)) {
                return tableColumnDO; // 找到匹配的ColumnDO，返回之
            }
        }
        return null; // 如果没有找到匹配的ColumnDO，返回null
    }


    private void validateMaterialLibraryExists(Long id, Integer formatType) {
        MaterialLibraryDO materialLibraryDO = materialLibraryMapper.selectById(id);

        if (materialLibraryDO == null) {
            throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
        }

        if (formatType != null && !formatType.equals(materialLibraryDO.getFormatType())) {
            throw exception(MATERIAL_LIBRARY_FORAMT_NO_MODIFY);
        }
    }

    /**
     * 根据素材库的格式类型获取相应的导入策略。
     *
     * @param formatType 素材库的格式类型
     * @return 对应的导入策略实例
     */
    private MaterialImportStrategy getImportStrategy(Integer formatType) {
        if (MaterialTypeEnum.isExcel(formatType)) {
            return applicationContext.getBean(ExcelMaterialImportStrategy.class);
        }
        if (MaterialTypeEnum.isImage(formatType)) {
            return applicationContext.getBean(ImageMaterialImportStrategy.class);
        }
        if (MaterialTypeEnum.isZip(formatType)) {
            return applicationContext.getBean(ZipMaterialImportStrategy.class);
        }
        // 对于未知的素材类型，返回错误信息
        throw new UnsupportedOperationException("Unsupported material format type.");

    }

}