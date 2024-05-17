package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MaterialTemplateUtils {

    private static final String DESCRIPTION = "zip包结构说明\n" +
            "1. 包名、目录名、excel文件名和第二行表头都不允许修改\n" +
            "2. 只可从第三行开始新增素材内容\n" +
            "3. 单元格中图片为images目录下相对路径";

    /**
     * 同名文件 表头修改后重新生成
     * <p>
     * key-文件名 DIVIDER
     * value-表头
     */
    private static final Map<String, List<String>> EXCEL_HEADER_MAP = new ConcurrentHashMap<>();

    public static final String DIVIDER = "-";

    private static final String PATH = Paths.get(System.getProperty("java.io.tmpdir"), "template").toString();

    /**
     * 实体类生成zip模板
     *
     * @param materialType {@link MaterialTypeEnum#getTypeCode()}
     * @return
     * @throws IOException
     */
    public static File readTemplate(String materialType) throws IOException {
        // 目录  系统临时目录/template/{materialType}.zip
        String filePath = PATH + File.separator + materialType + ".zip";
        File file = new File(filePath);
        if (!file.exists()) {
            return createTemplate(materialType);
        }
        return file;
    }

    /**
     * 生成zip压缩包并保存到服务器
     * 文件不存在或者表头更新后重新生成
     *
     * @param fileNamePrefix
     * @param excelHead
     * @return
     * @throws IOException
     */
    public static File readTemplate(String fileNamePrefix, List<String> excelHead) throws IOException {
        String filePath = PATH + File.separator + fileNamePrefix + ".zip";
        File file = new File(filePath);
        if (!file.exists() || differentHead(fileNamePrefix, excelHead)) {
            return createTemplate(fileNamePrefix, excelHead);
        }
        return file;
    }

    private static boolean differentHead(String fileNamePrefix, List<String> excelHead) {
        List<String> olderHead = EXCEL_HEADER_MAP.get(fileNamePrefix);
        return !CollUtil.isEqualList(olderHead, excelHead);
    }

    private static synchronized File createTemplate(String materialType) throws IOException {
        String filePath = PATH + File.separator + materialType + ".zip";
        File file = new File(filePath);
        if (!file.exists()) {
            return createZipFile(materialType);
        }
        return file;
    }

    private static synchronized File createTemplate(String fileNamePrefix, List<String> excelHead) throws IOException {
        String filePath = PATH + File.separator + fileNamePrefix + ".zip";
        File file = new File(filePath);
        if (!file.exists() || differentHead(fileNamePrefix, excelHead)) {
            EXCEL_HEADER_MAP.put(fileNamePrefix, excelHead);
            return createZipFile(fileNamePrefix, excelHead);
        }
        return file;
    }

    // 生成压缩文件
    private static File createZipFile(String materialType) throws IOException {
        log.info("create zip template, materialType={}", materialType);
        long start = System.currentTimeMillis();
        // 目录  系统临时目录/template/{materialType}/
        String dirPath = PATH + File.separator + materialType;
        File dir = new File(dirPath);
        File imageDir = new File(dirPath + File.separator + "images");
        imageDir.mkdirs();
        createExcel(materialType, dirPath, parseHead(MaterialTypeEnum.of(materialType).getAClass()));
        // 打包到当前目录 系统临时目录/template/
        File zip = ZipUtil.zip(dir, StandardCharsets.UTF_8);
        long end = System.currentTimeMillis();
        log.info("create zip template, {} ms", end - start);
        return zip;
    }

    /**
     * 生成压缩文件
     *
     * @param fileNamePrefix
     * @param excelHead
     * @return
     * @throws IOException
     */
    private static File createZipFile(String fileNamePrefix, List<String> excelHead) throws IOException {
        log.info("create zip template, fileNamePrefix={}", fileNamePrefix);
        long start = System.currentTimeMillis();
        // 目录  系统临时目录/template/{materialType}/
        String dirPath = PATH + File.separator + fileNamePrefix;
        File dir = new File(dirPath);
        File imageDir = new File(dirPath + File.separator + "images");
        imageDir.mkdirs();
        createExcel(fileNamePrefix, dirPath, excelHead);
        // 打包到当前目录 系统临时目录/template/
        File zip = ZipUtil.zip(dir, StandardCharsets.UTF_8);
        long end = System.currentTimeMillis();
        log.info("create zip template, {} ms", end - start);
        return zip;
    }

    // 生成excel文件
    private static File createExcel(String fileNamePrefix, String dirPath, List<String> excelHead) throws IOException {
        File file = new File(dirPath + File.separator + fileNamePrefix + ".xlsx");
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(file)) {

            Sheet sheet = workbook.createSheet(fileNamePrefix);

            // 创建第一行   zip说明
            Row descRow = sheet.createRow(0);
            descRow.setHeight((short) 1500);
            Cell descCell = descRow.createCell(0);
            descCell.setCellValue(DESCRIPTION);
            // 设置第一行说明样式 加粗 字体大小
            CellStyle descStyle = workbook.createCellStyle();
            descStyle.setWrapText(true);
            Font descFont = workbook.createFont();
            descFont.setBold(true);
            descFont.setFontHeightInPoints((short) 12);
            descStyle.setFont(descFont);
            descStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            descStyle.setAlignment(HorizontalAlignment.LEFT);
            descCell.setCellStyle(descStyle);

            // 合并第1行单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, Math.max(excelHead.size() - 1, 5)));

            // 第二行 excel表头
            // 设置第二行表头样式
            CellStyle headStyle = workbook.createCellStyle();
            Font headFont = workbook.createFont();
            headFont.setBold(true);
            headFont.setFontHeightInPoints((short) 14);
            headStyle.setFont(headFont);
            headStyle.setAlignment(HorizontalAlignment.CENTER);
            headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//            headStyle.setBorderTop(BorderStyle.THICK);

            Row headRow = sheet.createRow(1);
            headRow.setHeight((short) 800);
            for (int i = 0; i < excelHead.size(); i++) {
                Cell cell = headRow.createCell(i);
                cell.setCellValue(excelHead.get(i));
                cell.setCellStyle(headStyle);
                sheet.setColumnWidth(i, 256 * 30);
            }
            workbook.write(outputStream);
            return file;
        }
    }

    /**
     * 解析表头
     *
     * @param clazz
     * @return
     */
    private static List<String> parseHead(Class<?> clazz) {
        List<String> excelHead = new ArrayList<>();
        Field[] allFields = clazz.getDeclaredFields();
        // 筛选excel表头字段
        for (Field field : allFields) {
            //判断并获取excel注解信息
            if (field.isAnnotationPresent(ExcelProperty.class)) {
                ExcelProperty excelProperty = field.getDeclaredAnnotation(ExcelProperty.class);
                excelHead.add(excelProperty.value()[0]);
            }
        }
        return excelHead;
    }
}
