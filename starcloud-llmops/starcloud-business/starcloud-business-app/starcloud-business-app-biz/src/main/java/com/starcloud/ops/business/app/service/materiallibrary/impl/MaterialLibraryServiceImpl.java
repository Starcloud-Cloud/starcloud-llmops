package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibrarySaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceUseRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryMapper;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialFormatTypeEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialTypeEnum;
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
import java.util.Collections;
import java.util.List;
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
    private MaterialLibrarySliceService materialLibrarySliceService;


    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    @Resource
    private MaterialLibraryMapper materialLibraryMapper;


    @Resource
    private ApplicationContext applicationContext;


    @Override
    public Long createMaterialLibrary(MaterialLibrarySaveReqVO createReqVO) {
        // 插入
        MaterialLibraryDO materialLibrary = BeanUtils.toBean(createReqVO, MaterialLibraryDO.class);
        materialLibrary.setUid(IdUtil.fastSimpleUUID());
        materialLibrary.setAllFileSize(0L);
        materialLibrary.setTotalUsedCount(0L);
        materialLibraryMapper.insert(materialLibrary);
        // 返回
        return materialLibrary.getId();
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

    @Override
    public PageResult<MaterialLibraryDO> getMaterialLibraryPage(MaterialLibraryPageReqVO pageReqVO) {
        return materialLibraryMapper.selectPage(pageReqVO);
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
     * 获取应用执行的素材
     *
     * @param appReqVO 素材库编号
     */
    @Override
    public List<MaterialLibrarySliceUseRespVO> getMaterialLibrarySliceList(List<MaterialLibrarySliceAppReqVO> appReqVO) {


        return Collections.emptyList();
    }


// ========================================私有方法区 ========================================


    private String processMaterialLibrary(MaterialLibrarySliceAppReqVO appReqVO) {
        // 假设uid为素材库的唯一标识
        String uid = appReqVO.getLibraryUid();

        // 查询素材库的详细信息，根据实际情况处理可能的异常和空结果
        MaterialLibraryDO materialLibraryDO = materialLibraryMapper.selectByUid(uid);
        if (materialLibraryDO == null) {
            throw exception(111);
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
        saveReqVO.setStatus(materialLibraryDO.getFormatType());

        Long materialLibrary = this.createMaterialLibrary(saveReqVO);

        MaterialLibraryDO newMaterialLibraryDO = this.validateMaterialLibraryExists(materialLibrary);

        if (!MaterialFormatTypeEnum.isExcel(newMaterialLibraryDO.getFormatType())) {


            // 复制数据
            return newMaterialLibraryDO.getUid();

        }
        // 复制表头

        // 复制数据


        return newMaterialLibraryDO.getUid();
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