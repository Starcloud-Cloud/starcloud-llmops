package com.starcloud.ops.business.app.util;

import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sun.security.action.GetPropertyAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.doPrivileged;

@Slf4j
public class MaterialTemplateUtils {

    private static final String description = "zip包结构说明\n" +
            "1. 包名、目录名、excel文件名和第二行表头都不允许修改\n" +
            "2. 只可从第三行开始新增素材内容\n" +
            "3. 单元格中图片为images目录下相对路径";

    private static final String path = Paths.get(doPrivileged(new GetPropertyAction("java.io.tmpdir")), "template").toString();

    public static File readTemplate(String materialType) throws IOException {
        // 目录  系统临时目录/template/{materialType}.zip
        String filePath = path + File.separator + materialType + ".zip";
        File file = new File(filePath);
        if (!file.exists()) {
            return createTemplate(materialType);
        }
        return file;
    }

    private static synchronized File createTemplate(String materialType) throws IOException {
        String filePath = path + File.separator + materialType + ".zip";
        File file = new File(filePath);
        if (!file.exists()) {
            return createZipFile(materialType);
        }
        return file;
    }

    // 生成压缩文件
    private static File createZipFile(String materialType) throws IOException {
        log.info("create zip template, materialType={}", materialType);
        long start = System.currentTimeMillis();
        // 目录  系统临时目录/template/{materialType}/
        String dirPath = path + File.separator + materialType;
        File dir = new File(dirPath);
        File imageDir = new File(dirPath + File.separator + "images");
        imageDir.mkdirs();
        createExcel(materialType, dirPath);
        // 打包到当前目录 系统临时目录/template/
        File zip = ZipUtil.zip(dir, StandardCharsets.UTF_8);
        long end = System.currentTimeMillis();
        log.info("create zip template, {} ms", end - start);
        return zip;
    }


    // 生成excel文件
    private static File createExcel(String materialType, String dirPath) throws IOException {
        // excel文件绝对路径  系统临时目录/template/{materialType}/{materialType}.zip
        File file = new File(dirPath + File.separator + materialType + ".xlsx");
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(file)) {
            MaterialTypeEnum typeEnum = MaterialTypeEnum.of(materialType);
            List<String> excelHead = parseHead(typeEnum.getAClass());

            Sheet sheet = workbook.createSheet(materialType);

            // 创建一行   zip说明
            Row descRow = sheet.createRow(0);
            descRow.setHeight((short) 1500);
            Cell descCell = descRow.createCell(0);
            descCell.setCellValue(description);
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
