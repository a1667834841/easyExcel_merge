package com.zhu.excel_merge;

/**
 * @author ggBall
 * @version 1.0.0
 * @ClassName MyMergeStrategy.java
 * @Description TODO
 * @createTime 2023年03月01日 11:51:00
 */

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @title: MyMergeStrategy
 * @Author ggball
 * @Date: 2021/9/27 21:09
 * @Version 1.0
 */
@Slf4j
public class MyMergeStrategy<T> extends AbstractMergeStrategy {
    private int firstRowIndex;
    private int lastRowIndex;
    private int firstColumnIndex;
    private int lastColumnIndex;

    // 主键值集合
    private List<String> primaryIdList = new ArrayList<>();
    // 需要合并的列index
    Set<Integer> colIndexSet = new HashSet<>();

    public MyMergeStrategy(int firstRowIndex, int lastRowIndex, int firstColumnIndex, int lastColumnIndex) {
        if (firstRowIndex >= 0 && lastRowIndex >= 0 && firstColumnIndex >= 0 && lastColumnIndex >= 0) {
            this.firstRowIndex = firstRowIndex;
            this.lastRowIndex = lastRowIndex;
            this.firstColumnIndex = firstColumnIndex;
            this.lastColumnIndex = lastColumnIndex;
        } else {
            throw new IllegalArgumentException("All parameters must be greater than 0");
        }

    }

    /**
     *从数据list
     */
    public MyMergeStrategy(List<T> data) throws  IllegalAccessException {
        if (data.size() == 0){
            throw new RuntimeException("no data exception");
        }
        for (T row : data) {
            Class<?> type = row.getClass();
            Field[] fields = getAllDeclaredFields(type);


            for (Field field : fields) {
                ExcelKey key = field.getDeclaredAnnotation(ExcelKey.class);
                if (null != key) {
                    field.setAccessible(true);
                    Object filedValue = field.get(row);
                    field.setAccessible(false);
                    if (null != filedValue) {
                        // 添加合并主键值
                        primaryIdList.add(String.valueOf(filedValue));
                    }
                    break;
                }
            }

        }

        // 获取需要合并的列
        T row = data.get(0);
        Class<?> aClass = row.getClass();
        Field[] fields = getAllDeclaredFields(aClass);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            ExcelMerge merge = field.getDeclaredAnnotation(ExcelMerge.class);
            if (null != merge && merge.isMerge()) {
                colIndexSet.add(i);
            }
        }


    }


    /**
     * 获取合并的首行坐标和下行行数
     * @return map<合并的首行坐标,下行行数>
     */
    public Map<Integer,Integer> getMergeRowMap() {
        Map<Integer, Integer> mergeRowMap = new HashMap<>();
        if (null == primaryIdList || primaryIdList.size() == 0) {
            return mergeRowMap;
        }
        // 主键索引
        int idIndex = 0;
        // 主键临时值
        String tempValue = null;

        // 如果主键都相同
        HashSet<String> idSet = new HashSet<>(primaryIdList);
        if (idSet.size() == 1) {
            mergeRowMap.put(0,primaryIdList.size()-1);
            return mergeRowMap;
        }

        // 主键不相同
        for (int i = 0; i < primaryIdList.size(); i++) {
            if (null == tempValue) {
                tempValue = primaryIdList.get(i);
            }
            String id = primaryIdList.get(i);
            if (!id.equals(tempValue)) {
                mergeRowMap.put(idIndex,(i-1)-idIndex);
                idIndex = i;
                tempValue = null;
            }
            if (primaryIdList.size()-1 == i) {
                mergeRowMap.put(idIndex,i-idIndex);
            }


        }

        return mergeRowMap;
    }

    /**
     *获取类的属性和方法
     */
    public static Field[] getAllDeclaredFields(Class<?> clazz) {
        Class<?> superclass;
        List<Field> fieldList = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        while ((superclass = clazz.getSuperclass()) != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(superclass.getDeclaredFields())));
            clazz = superclass;
        }
        Field[] res = new Field[fieldList.size()];
        res = fieldList.toArray(res);
        return res;
    }


    /**
     *合并单元格方法
     */
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        Map<Integer, Integer> mergeRowMap = getMergeRowMap();
        // 行坐标
        int rowIndex = cell.getRowIndex();
        // 列坐标
        int columnIndex = cell.getColumnIndex();
        // 下行行数
        Integer downRows = mergeRowMap.get(rowIndex-head.getHeadNameList().size());

        // 和并列坐标
        boolean mergeContains = colIndexSet.contains(columnIndex);
        if (null != downRows && mergeContains) {

            // 创建单元格范围地址
            CellRangeAddress cellRangeAddress = new CellRangeAddress(rowIndex, rowIndex+downRows, columnIndex, columnIndex);
            sheet.addMergedRegionUnsafe(cellRangeAddress);
        }

    }

    @Override
    public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, CellData cellData, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

        // 如果字符类型的单元格值为空字符串，那就设置成“/”
        if (cellData.getType().equals(CellDataTypeEnum.STRING) && StringUtils.isEmpty(cellData.getStringValue())) {
            cellData.setStringValue("/");
        }

        // 如果数字类型的单元格值为空，那就设置成“/”
        if (cellData.getType().equals(CellDataTypeEnum.NUMBER) && null == cellData.getNumberValue()) {
            cellData.setStringValue("/");
        }

        super.afterCellDataConverted(writeSheetHolder, writeTableHolder, cellData, cell, head, relativeRowIndex, isHead);
    }

}
