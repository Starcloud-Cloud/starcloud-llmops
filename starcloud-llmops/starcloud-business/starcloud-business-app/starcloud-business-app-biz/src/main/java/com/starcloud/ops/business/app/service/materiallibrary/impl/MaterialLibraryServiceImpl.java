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
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.*;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.*;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryMapper;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.DOWNLOAD_TEMPLATE_ERROR;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;
import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.TEMPLATE_FILE_SUFFIX;

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
    public Long createMaterialLibrary(MaterialLibrarySaveReqVO createReqVO) {
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
    public String createMaterialLibraryByApp(String appName) {
        Assert.notBlank(appName, "应用名称不可以为空,创建素材库失败");
        MaterialLibraryDO materialLibrary = saveMaterialLibrary(new MaterialLibrarySaveReqVO().setName(StrUtil.format("{}的初始素材库", appName)).setLibraryType(MaterialLibraryTypeEnum.SYSTEM.getCode()));
        return materialLibrary.getUid();
    }

    /**
     * 通过应用创建素材知识库
     *
     * @param appReqVO 应用名称
     */
    @Override
    public void createMaterialLibraryByApp(MaterialLibraryAppReqVO appReqVO) {
        Assert.notBlank(appReqVO.getAppName(), "获取素材库失败，应用名称不能为空");
        Assert.notBlank(appReqVO.getAppUid(), "获取素材库失败，应用编号不能为空");
        Assert.notNull(appReqVO.getAppType(), "获取素材库失败，应用类型不能为空");
        Assert.notNull(appReqVO.getUserId(), "获取素材库失败，用户编号不能为空");
        // 创建系统素材库
        MaterialLibraryDO materialLibrary = saveMaterialLibrary(new MaterialLibrarySaveReqVO().setName(StrUtil.format("{}的初始素材库", appReqVO.getAppName())).setLibraryType(MaterialLibraryTypeEnum.SYSTEM.getCode()));
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

    /**
     * @param libraryUid  素材库编号
     * @return
     */
    @Override
    public MaterialLibraryRespVO getMaterialLibraryByAppUid(String libraryUid) {
        MaterialLibraryDO materialLibrary = materialLibraryMapper.selectByUid(libraryUid);
        if (materialLibrary==null){
            return null;
        }


        // 数据转换
        MaterialLibraryRespVO bean = BeanUtils.toBean(materialLibrary, MaterialLibraryRespVO.class);

        if (MaterialFormatTypeEnum.isExcel(materialLibrary.getFormatType())) {
            List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(materialLibrary.getId());
            bean.setTableMeta(BeanUtils.toBean(tableColumnDOList, MaterialLibraryTableColumnRespVO.class));
        }

        return null;
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
     * 应用发布，直接复制一份新的素材库出来（版本管理）
     *
     * @param appReqVO 应用中素材库绑定关系
     * @return 素材库 UID
     */
    @Override
    public List<String> materialLibraryCopy(List<MaterialLibrarySliceAppReqVO> appReqVO) {
        // 检查输入参数是否为空
        if (appReqVO == null || appReqVO.isEmpty()) {
            return Collections.emptyList();
        }
        // 使用Java 8 Stream API进行并行处理以提高性能
        return appReqVO.stream()
                .map(this::processMaterialLibrary)
                .collect(Collectors.toList());
    }

    /**
     * @param newApp 新应用
     * @param oldApp 旧应用
     */
    @Override
    public void materialLibraryCopy(MaterialLibraryAppReqVO newApp, MaterialLibraryAppReqVO oldApp) {

        MaterialLibraryRespVO oldMaterialLibrary = this.getMaterialLibraryByApp(oldApp);

        MaterialLibraryDO newMaterialLibrary = saveMaterialLibrary(new MaterialLibrarySaveReqVO().setName(StrUtil.format("{}_发布版本", oldMaterialLibrary.getName())).setLibraryType(MaterialLibraryTypeEnum.PUBLISH.getCode()));


        // 复制表头
        List<MaterialLibraryTableColumnDO> oldTableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(oldMaterialLibrary.getId());
        List<MaterialLibraryTableColumnSaveReqVO> newTableColumnSaveList = BeanUtils.toBean(oldTableColumnDOList, MaterialLibraryTableColumnSaveReqVO.class);
        newTableColumnSaveList.forEach(data -> {
            data.setLibraryId(newMaterialLibrary.getId());
            data.setId(null);
        });
        materialLibraryTableColumnService.saveBatchData(newTableColumnSaveList);

        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(newMaterialLibrary.getId());

        // 查询数据复制到新的素材库
        // 获取原始素材数据
        List<MaterialLibrarySliceDO> sliceOldDOList;

        MaterialLibrarySlicePageReqVO pageReqVO = new MaterialLibrarySlicePageReqVO();
        pageReqVO.setPageNo(1);
        pageReqVO.setPageSize(100);
        pageReqVO.setLibraryId(oldMaterialLibrary.getId());
        sliceOldDOList = materialLibrarySliceService.getMaterialLibrarySlicePage(pageReqVO).getList();

        sliceOldDOList.forEach(sliceData -> {
            sliceData.setId(null);
            sliceData.setLibraryId(newMaterialLibrary.getId());
            List<MaterialLibrarySliceDO.TableContent> datasList = sliceData.getContent();
            if (datasList != null) {
                datasList.forEach(datas -> {
                    if (datas != null && datas.getColumnCode() != null) {
                        MaterialLibraryTableColumnDO newColumnDO = findColumnDOByCode(tableColumnDOList, datas.getColumnCode());
                        if (newColumnDO != null) {
                            datas.setColumnId(newColumnDO.getId());
                        }
                    }
                });
            }
        });

        materialLibrarySliceService.saveBatchData(sliceOldDOList);
    }

    /**
     * 素材数据迁移
     *
     * @param migrationReqVO 迁移 VO
     * @return 素材库 UID
     */
    @Override
    public String materialLibraryDataMigration(SliceMigrationReqVO migrationReqVO) {
        this.createMaterialLibraryByApp(migrationReqVO);

        MaterialLibraryRespVO materialLibrary = this.getMaterialLibraryByApp(migrationReqVO);

        List<MaterialLibraryTableColumnSaveReqVO> tableColumnSaveReqVOS = migrationReqVO.getTableColumnSaveReqVOS();

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
        return materialLibrary.getUid();
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
        if (Objects.nonNull(sliceUsageCountReqVO.getLibraryUid())) {
            MaterialLibraryDO materialLibrary = this.validateMaterialLibraryExists(sliceUsageCountReqVO.getLibraryUid());

            sliceUsageCountReqVO.getSliceCountReqVOS().forEach(sliceCountReqVO -> materialLibrarySliceService.updateSliceUsedCount(materialLibrary.getId(), sliceCountReqVO.getSliceId(), sliceCountReqVO.getNums()));
            return;
        }
        MaterialLibraryRespVO materialLibrary = getMaterialLibraryByApp(sliceUsageCountReqVO);
        sliceUsageCountReqVO.getSliceCountReqVOS().forEach(sliceCountReqVO -> materialLibrarySliceService.updateSliceUsedCount(materialLibrary.getId(), sliceCountReqVO.getSliceId(), sliceCountReqVO.getNums()));
    }

    /**
     * 更新素材库文件数量
     *
     * @param libraryId 素材库编号
     */
    @Override
    public void updateMaterialLibraryFileCount(Long libraryId) {
        try {
            long size = materialLibrarySliceService.getMaterialLibrarySliceByLibraryId(libraryId).size();
            materialLibraryMapper.updateById(new MaterialLibraryDO().setFileCount(size).setId(libraryId));
        } catch (RuntimeException e) {
            log.error("素材库文件数更新失败，素材库编号为:({})", libraryId);
        }

    }


    // ========================================私有方法区 ========================================
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


    private String processMaterialLibrary(MaterialLibrarySliceAppReqVO appReqVO) {
        MaterialLibraryRespVO materialLibrary = this.getMaterialLibraryByApp(appReqVO);
        if (Objects.isNull(materialLibrary.getUid())) {
            throw exception(MATERIAL_LIBRARY_ID_EMPTY);
        }
        // 假设uid为素材库的唯一标识
        String uid = materialLibrary.getUid();

        // 查询素材库的详细信息，根据实际情况处理可能的异常和空结果
        MaterialLibraryDO materialLibraryDO = materialLibraryMapper.selectByUid(uid);
        if (materialLibraryDO == null) {
            throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
        }
        // 创建并返回新的素材库的UID
        return createNewMaterialLibrary(materialLibraryDO, appReqVO.getSliceIdList());
    }

    /**
     * 根据提供的素材库详细信息创建新的素材库。
     *
     * @param materialLibraryDO 素材库DO。
     * @return 新创建的素材库的UID。
     */
    private String createNewMaterialLibrary(MaterialLibraryDO materialLibraryDO, List<Long> slices) {
        MaterialLibrarySaveReqVO saveReqVO = new MaterialLibrarySaveReqVO();

        saveReqVO.setName(materialLibraryDO.getName() + "_发布版本");
        saveReqVO.setIconUrl(materialLibraryDO.getIconUrl());
        saveReqVO.setDescription(materialLibraryDO.getDescription());
        saveReqVO.setFormatType(materialLibraryDO.getFormatType());
        saveReqVO.setStatus(true);

        Long materialLibrary = this.createMaterialLibrary(saveReqVO);
        MaterialLibraryDO newMaterialLibraryDO = this.validateMaterialLibraryExists(materialLibrary);

        // 获取原始素材数据
        List<MaterialLibrarySliceDO> sliceOldDOList;
        if (CollUtil.isEmpty(slices)) {
            MaterialLibrarySlicePageReqVO pageReqVO = new MaterialLibrarySlicePageReqVO();
            pageReqVO.setPageNo(1);
            pageReqVO.setPageSize(100);
            pageReqVO.setLibraryId(materialLibraryDO.getId());
            sliceOldDOList = materialLibrarySliceService.getMaterialLibrarySlicePage(pageReqVO).getList();
        } else {
            sliceOldDOList = materialLibrarySliceService.getMaterialLibrarySlice(materialLibraryDO.getId(), slices);
        }

        // 非 excel 处理
        if (!MaterialFormatTypeEnum.isExcel(newMaterialLibraryDO.getFormatType())) {
            if (CollUtil.isNotEmpty(sliceOldDOList)) {
                materialLibrarySliceService.saveBatchData(BeanUtils.toBean(sliceOldDOList, MaterialLibrarySliceSaveReqVO.class));
            }
            return newMaterialLibraryDO.getUid();
        }
        // 复制表头
        List<MaterialLibraryTableColumnDO> oldTableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(materialLibraryDO.getId());
        // 复制表头数据
        List<MaterialLibraryTableColumnSaveReqVO> newTableColumnSaveList = BeanUtils.toBean(oldTableColumnDOList, MaterialLibraryTableColumnSaveReqVO.class);
        newTableColumnSaveList.forEach(data -> {
            data.setLibraryId(newMaterialLibraryDO.getId());
            data.setId(null);
        });
        materialLibraryTableColumnService.saveBatchData(newTableColumnSaveList);

        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(newMaterialLibraryDO.getId());

        // 复制数据
        sliceOldDOList.forEach(sliceData -> {
            sliceData.setId(null);
            sliceData.setLibraryId(newMaterialLibraryDO.getId());
            // 增加对getContent()返回值的空检查
            List<MaterialLibrarySliceDO.TableContent> datasList = sliceData.getContent();
            if (datasList != null) {
                datasList.forEach(datas -> {
                    // 增加对datas的空检查
                    if (datas != null && datas.getColumnCode() != null) {
                        // 假设newTableColumnDOList是已经定义好的，且通过getColumnCode()可以找到对应的ColumnDO
                        // 这里需要一个机制来查找并获取对应的ColumnDO，例如通过getColumnCode()的值进行搜索
                        MaterialLibraryTableColumnDO newColumnDO = findColumnDOByCode(tableColumnDOList, datas.getColumnCode());
                        if (newColumnDO != null) {
                            datas.setColumnId(newColumnDO.getId());
                        }
                    }
                });
            }
        });

        materialLibrarySliceService.saveBatchData(sliceOldDOList);

        return newMaterialLibraryDO.getUid();

    }

    private MaterialLibraryTableColumnDO findColumnDOByCode(List<MaterialLibraryTableColumnDO> tableColumnDOList, String columnCode) {
        // 为了优化性能，这里可以考虑使用更高效的数据结构进行搜索，比如HashMap
        // 由于示例中没有给出具体的ColumnDO实现，这里简单地使用循环遍历列表进行查找
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