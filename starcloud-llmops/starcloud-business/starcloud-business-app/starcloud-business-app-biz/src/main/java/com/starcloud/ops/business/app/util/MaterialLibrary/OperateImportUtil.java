package com.starcloud.ops.business.app.util.MaterialLibrary;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.github.junrar.exception.RarException;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.listener.ExcelDataEventListener;
import com.starcloud.ops.business.app.service.materiallibrary.listener.ExcelHeadEventListener;
import com.starcloud.ops.business.app.service.materiallibrary.listener.ExcelImageEventListener;
import com.starcloud.ops.business.app.util.MaterialLibrary.dto.ExcelDataImportConfigDTO;
import com.starcloud.ops.business.app.util.UnpackUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryConstants.SUPPORT_COMPRESS_FORMAT;
import static com.starcloud.ops.business.app.enums.xhs.CreativeConstants.MATERIAL_TMP_DIR_PATH;

/**
 * 导入操作工具类
 */
public class OperateImportUtil {


    /**
     * 读取 Excel表头 【将数据都读取到内存中】
     * 【注意】事务好控制，但是数据量太大绝对会出现内存溢出
     *
     * @param file          文件
     * @param limitColSize  从第几行读取数据
     * @param headRowNumber 表头行数
     */
    public static List<String> readExcelHead(File file, Integer limitColSize, Integer headRowNumber) {
        // 初始化一个表头监听器
        ExcelHeadEventListener excelHeadEventListener = new ExcelHeadEventListener(limitColSize);
        // 读取表头并验证
        EasyExcel.read(file, excelHeadEventListener)
                .headRowNumber(headRowNumber)
                .sheet()
                .doReadSync();

        return excelHeadEventListener.getHeads();
    }


    /**
     * 读取 Excel表头 【将数据都读取到内存中】
     * 【注意】事务好控制，但是数据量太大绝对会出现内存溢出
     *
     * @param inputStream   文件
     * @param limitColSize  从第几行读取数据
     * @param headRowNumber 表头行数
     */
    public static List<String> readExcelHead(InputStream inputStream, Integer limitColSize, Integer headRowNumber) {
        // 初始化一个表头监听器
        ExcelHeadEventListener excelHeadEventListener = new ExcelHeadEventListener(limitColSize);
        // 读取表头并验证
        EasyExcel.read(inputStream, excelHeadEventListener)
                .headRowNumber(headRowNumber)
                .sheet()
                .doReadSync();

        return excelHeadEventListener.getHeads();
    }


    /**
     * 这种是 内存溢出出现的情况非常低，但是事务不好控制（主要是出错全部回滚）
     *
     * @param file          文件
     * @param headRowNumber 将数据存储到某张表的service【需要在业务service上实现这个接口，并重写saveBatchData方法】
     * @param columnIndex   泛型类型
     * @return
     */
    public static <T> Map<Integer, List<String>> readOtherExcel(File file, Integer headRowNumber, Map<Integer, List<Long>> columnIndex) {
        // 初始化监听器
        ExcelImageEventListener excelHeadEventListener = new ExcelImageEventListener(columnIndex);
        // 读取表头并验证
        EasyExcel.read(file, excelHeadEventListener)
                .headRowNumber(headRowNumber)
                .sheet()
                .doReadSync();
        return excelHeadEventListener.getColumnData();
    }


    /**
     * 这种是 内存溢出出现的情况非常低，但是事务不好控制（主要是出错全部回滚）
     *
     * @param inputStream   文件流
     * @param commonService 将数据存储到某张表的service【需要在业务service上实现这个接口，并重写saveBatchData方法】
     * @param <T>           泛型类型
     */
    public static <T> void readExcel(InputStream inputStream, File[] files, ExcelDataImportConfigDTO configDTO, MaterialLibrarySliceService commonService, Integer headRowNumber) {
        ExcelDataEventListener readExcelDataListener = new ExcelDataEventListener(commonService, files, "");

        EasyExcel.read(inputStream, readExcelDataListener)
                .customObject(configDTO)
                .sheet()
                .headRowNumber(headRowNumber)
                .doReadSync();
    }

    /**
     * 这种是 内存溢出出现的情况非常低，但是事务不好控制（主要是出错全部回滚）
     *
     * @param file          文件
     * @param commonService 将数据存储到某张表的service【需要在业务service上实现这个接口，并重写saveBatchData方法】
     * @param <T>           泛型类型
     */
    public static <T> void readExcel(File file, File[] files, ExcelDataImportConfigDTO configDTO, MaterialLibrarySliceService commonService, Integer headRowNumber, String unzipDir) {
        ExcelDataEventListener readExcelDataListener = new ExcelDataEventListener(commonService, files, unzipDir);

        EasyExcel.read(file, readExcelDataListener)
                .customObject(configDTO)
                .sheet()
                .headRowNumber(headRowNumber)
                .doReadSync();
    }


    /**
     * 动态表头导出
     *
     * @param response HttpServletResponse
     * @param fileName 文件名
     * @param Myhead   Excel表头信息
     * @param data     Excel数据
     */
    public static void writeExcel(HttpServletResponse response, String fileName, List<List<String>> Myhead, List<List<Object>> data) {
        try {
            // 1 设置下载相关内容
            // 设置mime类型
            response.setContentType("application/vnd.ms-excel");
            // 设置编码
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            fileName = URLEncoder.encode(fileName, "UTF-8");
            // 设置响应头信息 Content-disposition
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream())
                    .head(Myhead)
                    .sheet(fileName)
                    .doWrite(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 有实体的导出
     *
     * @param response HttpServletResponse
     * @param fileName 文件名
     * @param data     数据
     * @param clazz    实体类型
     * @param <T>
     */
    public static <T> void exportExcelByEntity(HttpServletResponse response, String fileName, List<T> data, Class<T> clazz) {
        try {
            // 1 设置下载相关内容
            // 设置mime类型
            response.setContentType("application/vnd.ms-excel");
            // 设置编码
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            fileName = URLEncoder.encode(fileName, "UTF-8");
            // 设置响应头信息 Content-disposition
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 导出
            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet(fileName)
                    .doWrite(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 解压 并且返回压缩包内的数据
     *
     * @param file 压缩包文件
     */
    public static File[] unpackReturnFile(MultipartFile file) {

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


    /**
     * 获取文件后缀名
     *
     * @param fileName 文件名称
     * @return 文件后缀名
     */
    public static String getFileExtension(String fileName) {

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            // "文件名无扩展名，无法确定文件类型"
            return "";
        }
        // 不包含点，所以使用dotIndex + 1
        return fileName.substring(dotIndex + 1);
    }
}
