package com.starcloud.ops.business.app.util.MaterialLibrary;

import cn.hutool.poi.excel.ExcelPicUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.app.util.MaterialLibrary
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2024/06/24  16:16
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2024/06/24   AlanCusack    1.0         1.0 Version
 */
public class MaterialLibraryExcelUtil extends ExcelPicUtil {


    public static Map<String, PictureData> getPictureFromExcel(MultipartFile file, int sheetNum) throws EncryptedDocumentException, IOException {

        // 获取图片PictureData集合
        String fileName = file.getOriginalFilename();
        Workbook workbook = null;
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        InputStream fileInputStream = file.getInputStream();
        if (fileName.endsWith("xls")) {
            // 2003
            workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet sheet = (HSSFSheet) workbook.getSheetAt(sheetNum - 1);
            Map<String, PictureData> pictures = getPictures(sheet);
            return pictures;
        } else if (fileName.endsWith("xlsx")) {
            // 2007
            workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(sheetNum - 1);
            Map<String, PictureData> pictures = getPictures(sheet);
            return pictures;
        }
        return new HashMap();
    }

    /**
     * 获取图片和位置 (xls版)
     *
     * @param sheet
     * @return
     * @throws IOException
     */
    public static Map<String, PictureData> getPictures(HSSFSheet sheet) throws IOException {
        Map<String, PictureData> map = new HashMap<String, PictureData>();
        List<HSSFShape> list = sheet.getDrawingPatriarch().getChildren();
        for (HSSFShape shape : list) {
            if (shape instanceof HSSFPicture) {
                HSSFPicture picture = (HSSFPicture) shape;
                HSSFClientAnchor cAnchor = picture.getClientAnchor();
                PictureData pdata = picture.getPictureData();
                // 行号-列号
                String key = cAnchor.getRow1() + "-" + cAnchor.getCol1();
                map.put(key, pdata);
            }
        }
        return map;
    }

    /**
     * 获取图片和位置 (xlsx版)
     *
     * @param sheet
     * @return
     * @throws IOException
     */
    public static Map<String, PictureData> getPictures(XSSFSheet sheet) throws IOException {
        Map<String, PictureData> map = new HashMap<String, PictureData>();
        List<POIXMLDocumentPart> list = sheet.getRelations();
        for (POIXMLDocumentPart part : list) {
            if (part instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) part;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    XSSFPicture picture = (XSSFPicture) shape;
                    XSSFClientAnchor anchor = picture.getPreferredSize();
                    CTMarker marker = anchor.getFrom();
                    // 行号-列号
                    String key = marker.getRow() + "-" + marker.getCol();
                    map.put(key, picture.getPictureData());
                }
            }
        }
        return map;
    }

    private static void inputStreamToFile(InputStream ins, File file) {
        try (OutputStream os = new FileOutputStream(file)) {
            int bytesRead;
            int bytes = 8192;
            byte[] buffer = new byte[bytes];
            while ((bytesRead = ins.read(buffer, 0, bytes)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
