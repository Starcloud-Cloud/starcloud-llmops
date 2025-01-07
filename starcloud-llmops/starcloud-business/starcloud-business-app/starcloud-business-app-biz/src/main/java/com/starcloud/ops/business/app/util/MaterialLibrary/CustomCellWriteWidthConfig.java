package com.starcloud.ops.business.app.util.MaterialLibrary;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCellWriteWidthConfig extends AbstractColumnWidthStyleStrategy {

    private final Map<Integer, Map<Integer, Integer>> CACHE = new HashMap<>();

    // CustomCellWriteWidthConfig() {
    // }
    //
    // public static CustomCellWriteWidthConfig createCustomCellWriteWidthConfig() {
    //     return new CustomCellWriteWidthConfig();
    // }

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell,
                                  Head head, Integer relativeRowIndex, Boolean isHead) {
        boolean needSetWidth = isHead || !CollectionUtils.isEmpty(cellDataList);
        if (needSetWidth) {
            Map<Integer, Integer> maxColumnWidthMap = CACHE.computeIfAbsent(writeSheetHolder.getSheetNo(), k -> new HashMap<>());
            Integer columnWidth = this.dataLength(cellDataList, cell, isHead);
            if (columnWidth > 0) {
                if (columnWidth > 10) {
                    columnWidth = 20;
                }else{
                    columnWidth = 10;
                }
                Integer maxColumnWidth = maxColumnWidthMap.get(cell.getColumnIndex());
                if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
                    maxColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                    Sheet sheet = writeSheetHolder.getSheet();
                    sheet.setColumnWidth(cell.getColumnIndex(), 256 * columnWidth + 184);
                }
            }
        }
    }

    /**
     * 计算长度
     *
     * @param cellDataList
     * @param cell
     * @param isHead
     * @return
     */
    private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
        if (isHead) {
            return cell.getStringCellValue().getBytes().length;
        } else {
            CellData<?> cellData = cellDataList.get(0);
            CellDataTypeEnum type = cellData.getType();
            if (type == null) {
                return -1;
            } else {
                switch (type) {
                    case STRING:
                        // 换行符（数据需要提前解析好）
                        int index = cellData.getStringValue().indexOf("\n");
                        return index != -1 ?
                                cellData.getStringValue().substring(0, index).getBytes().length + 1 : cellData.getStringValue().getBytes().length + 1;
                    case BOOLEAN:
                        return cellData.getBooleanValue().toString().getBytes().length;
                    case NUMBER:
                        return cellData.getNumberValue().toString().getBytes().length;
                    default:
                        return -1;
                }
            }
        }
    }

}
