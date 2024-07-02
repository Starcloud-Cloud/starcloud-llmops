package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibrarySaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_FORAMT_NO_MODIFY;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_NOT_EXISTS;
// import static cn.iocoder.yudao.module.llm.enums.ErrorCodeConstants.*;

/**
 * 素材知识库 Service 实现类
 *
 * @author starcloudadmin
 */
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
        if (materialLibraryDO.getFormatType().equals(updateReqVO.getFormatType())) {
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
        MaterialLibraryDO materialLibraryDO = validateMaterialLibraryExists(importRespVO.getLibraryId());
        // 根据素材类型获取素材执行策略
        MaterialImportStrategy strategy = getImportStrategy(importRespVO.getMaterialType());
        // 导入素材
        strategy.importMaterial(importRespVO);
    }



// ========================================私有方法区 ========================================


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