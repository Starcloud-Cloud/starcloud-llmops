package com.starcloud.ops.business.app.service.materiallibrary.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.github.junrar.exception.RarException;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import com.starcloud.ops.business.app.service.materiallibrary.listener.ExcelDataEventListener;
import com.starcloud.ops.business.app.service.materiallibrary.listener.ExcelHeadEventListener;
import com.starcloud.ops.business.app.util.UnpackUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.EXCEL_NOT_EXIST;
import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.*;
import static com.starcloud.ops.business.app.enums.xhs.CreativeConstants.MATERIAL_TMP_DIR_PATH;
import static com.starcloud.ops.business.app.util.MaterialLibrary.OperateImportUtil.getFileExtension;

/**
 * 压缩包素材导入
 */
@Slf4j
@Service
@Component
public class ZipMaterialImportStrategy implements MaterialImportStrategy {
    //
    // @Autowired
    // private StringRedisTemplate redisTemplate;
    //
    // @Resource
    // private UploadMaterialImageManager uploadMaterialImageManager;
    //
    // @Resource
    // private MaterialLibraryService materialLibraryService;

    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    @Autowired
    public ZipMaterialImportStrategy(MaterialLibraryTableColumnService materialLibraryTableColumnService) {
        this.materialLibraryTableColumnService = materialLibraryTableColumnService;
    }

    /**
     * @param importReqVO
     */
    @Override
    public void importMaterial(MaterialLibraryImportReqVO importReqVO) {
        String parseUid = IdUtil.fastSimpleUUID();
        long start = System.currentTimeMillis();
        // 获取素材库表头信息
        List<MaterialLibraryTableColumnDO> materialConfigList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(importReqVO.getLibraryId());

        // 压缩包只读取一个
        // -解压 并且返回压缩包内的数据
        File[] childrenDirs = unpackReturnFile(importReqVO.getFile()[0]);

        if (childrenDirs == null) {
            throw exception(EXCEL_NOT_EXIST);
        }


        File excel = null;
        File unzipDir = null;

        // 获取模板文件
        for (File childrenDir : childrenDirs) {
            if (childrenDir != null && childrenDir.isDirectory()) { // 避免空指针异常
                File[] excelFiles = childrenDir.listFiles(ZipMaterialImportStrategy::isTemplateFile);
                if (excelFiles != null && excelFiles.length > 0) {
                    excel = excelFiles[0];
                    unzipDir = childrenDir;
                    break;
                }
            }
        }


        if (Objects.isNull(excel)) {
            throw new RuntimeException(StrUtil.format("压缩包中未找到名为:{} 的文件", TEMPLATE_FILE_NAME));
        }

        Set<String> heads = materialConfigList.stream().map(MaterialLibraryTableColumnDO::getColumnName).collect(Collectors.toSet());
        // 初始化一个表头监听器
        ExcelHeadEventListener excelHeadEventListener = new ExcelHeadEventListener(1, heads);

        // 读取表头
        try {
            EasyExcel.read(excel, excelHeadEventListener)
                    .headRowNumber(TEMPLATE_FILE_TABLE_HEAD_CELL + 1)
                    .sheet()
                    .doRead();
        } catch (ExcelAnalysisException a) {
            log.info("表头读取完成，验证成功 开始异步存储数据");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ExcelDataEventListener excelDataEventListener = new ExcelDataEventListener();

        EasyExcel.read(excel, excelDataEventListener)
                .headRowNumber(2)
                .sheet()
                .doReadSync();

    }


    /**
     * 解压 并且返回压缩包内的数据
     *
     * @param file 压缩包文件
     */
    private File[] unpackReturnFile(MultipartFile file) {

        // 判断文件是否是支持的压缩文件
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new RuntimeException("文件不存在，文件名不存在");
        }
        if (!StrUtil.equalsAnyIgnoreCase(getFileExtension(originalFilename), SUPPORT_COMPRESS_FORMAT)) {
            throw new RuntimeException("不支持的解压的数据");
        }

        String parseUid = IdUtil.fastSimpleUUID();
        //     系统默认临时文件目录/material/parseUid
        String dirPath = Paths.get(MATERIAL_TMP_DIR_PATH, parseUid).toString();
        File dir = new File(dirPath);
        dir.mkdirs();

        File zipFile = Paths.get(dirPath, file.getOriginalFilename()).toFile();

        // 数据转换
        try {
            file.transferTo(zipFile);
        } catch (IOException e) {
            throw new RuntimeException("数据转换异常");
        }


        // 解压文件
        try {
            UnpackUtils.unpack(zipFile, dir);
        } catch (RuntimeException | IOException | RarException | InterruptedException e) {
            throw new RuntimeException("数据解压异常");
        }

        // 解析excel文件   解压文件下/目录/excel.xlsx
        // 压缩包解压后下面的目录
        File[] childrenDirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        return childrenDirs;
    }

    private static boolean isTemplateFile(File file) {
        // 确保是文件而不是目录
        if (!FileUtil.isFile(file)) {
            return false;
        }

        // 检查文件名是否匹配
        return TEMPLATE_FILE_NAME.equalsIgnoreCase(FileNameUtil.getName(file));
    }


    /**
     * @param materialTableColumn 素材库存在的表头
     * @param importTableColumn   导入数据的表头
     */
    private void validateMaterialTableColumn(Set<String> materialTableColumn, Set<String> importTableColumn) {
        if (CollUtil.isEmpty(materialTableColumn)) {
            return;
        }
        if (!CollUtil.isEqualList(materialTableColumn, importTableColumn)) {
            throw new RuntimeException("表头与当前表结构不一致,列名称及顺序需保持一致\n" +
                    "\n" +
                    "表头需要与现有表格结构保持一致。");
            // throw exception(EXCEL_HEADER_REQUIRED_FILED, subtract);
        }
    }

}
