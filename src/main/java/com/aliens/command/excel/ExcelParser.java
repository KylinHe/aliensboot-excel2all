package com.aliens.command.excel;

import com.aliens.command.excel.model.TableData;
import com.aliens.command.log.ILogger;
import com.aliens.command.log.SystemLogger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Created by hejialin on 2018/3/10.
 */
public class ExcelParser {

    private static final String EXCEL_TYPE_XLS = "xls";
    private static final String EXCEL_TYPE_XLSX = "xlsx";

    //解析的全局数据
    private Map<String, TableData> data = new TreeMap<>();

    private ILogger log = new SystemLogger();

    public Map<String, TableData> getData() {
        return data;
    }


    public void parse(final File srcFile) {
        parse0(srcFile);
        Map<String, Map<String, Object>> mapping = getAllIDMapping();
        // 注入关联
        for (TableData tableData : this.data.values()) {
            updateRef(tableData, mapping);
        }
        // 合并字段
        for (TableData tableData : this.data.values()) {
            tableData.mergeField();
            //tableData.filterField();
        }
    }

    private int parse0(final File srcFile) {
        if(srcFile.isFile()){
            return parse1(srcFile, getPrefix(srcFile.getAbsolutePath()));
        }else{
            int successCount = 0 ;
            for (File f : srcFile.listFiles()) {
                if(f.isDirectory()) continue;
                successCount+=parse1(f, getPrefix(f.getAbsolutePath()));
            }
            return successCount;
        }
    }

    //get all id-name mapping
    private Map<String, Map<String, Object>> getAllIDMapping() {
        Map<String, Map<String, Object>> allMapping = new HashMap<String, Map<String, Object>>();
        for (Map.Entry<String, TableData> entry : data.entrySet()) {
            Map<String, Object> idMapping = entry.getValue().getIdMapping();
            if (idMapping != null && !idMapping.isEmpty()) {
                allMapping.put(entry.getKey(), idMapping);
            }
        }
        return allMapping;
    }

    private String getPrefix(String absolutePath){
        return absolutePath.substring(absolutePath.lastIndexOf('.')+1);
    }

    private int parse1(final File srcFile, final String fileType) {
        if (srcFile.getName().startsWith("~$")) {
            return 0;
        }

        if (Config.isFilter(srcFile.getName().split("\\.")[0])) {
            log.Info("file " + srcFile.getName() + " is skipping!");
            return 0;
        }
        Workbook workbook = null;
        try {
            switch (fileType) {
                case EXCEL_TYPE_XLS:
                    workbook = new HSSFWorkbook(new FileInputStream(srcFile));
                    break;
                case EXCEL_TYPE_XLSX:
                    workbook = new XSSFWorkbook(srcFile);
                    break;
                default:
            }
            if (workbook != null) {
                log.Info("parseTemplate excel file :" + srcFile.getAbsolutePath());
                readWorkBook(workbook);
            }
            return 1;
        } catch(Exception e){
            e.printStackTrace();
            log.Error("sheet:" + SystemLogger.currSheetName + " column name:" + SystemLogger.currColumnName + " line:" + (SystemLogger.currLine + 1) + " : " + e.getMessage());
//            if(e instanceof ReadExcelException){
//                throw ((ReadExcelException) e).append(srcFile.getAlias());
//            }
        }finally{
            //TODO 关闭会修改文件
//            if(workbook != null){
//                try {
//                    workbook.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        return 0;
    }

    private void readWorkBook(Workbook workbook) {
        SheetParser parser = null;
        TableData tableData = null;
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            String sheetName = sheet.getSheetName();
            if (data.containsKey(sheetName)) {
                log.Error("sheet " + sheet.getSheetName() + " already exists");
            } else if (sheetName.startsWith(SheetParser.FILTER_CHAR) || Config.isFilter(sheetName)) {
                log.Info("sheet " + sheet.getSheetName() + " is skipping!");
            } else {
                tableData = new SheetParser().parse(sheet, workbook.getCreationHelper().createFormulaEvaluator());
                data.put(sheet.getSheetName(), tableData);
            }
        }
    }

    //update refer mapping
    public void updateRef(TableData data, Map<String, Map<String, Object>> mapping) {
        if (data == null || !data.haveRef()) {
            return;
        }

        for (Map.Entry<String, String> refer : data.getRefField().entrySet()) {
            String refTableName = refer.getValue();
            String fieldName = refer.getKey();
            Map<String, Object> referTableMapping = mapping.get(refTableName);
            if (referTableMapping == null || referTableMapping.isEmpty()) {
                log.Warning("table " + data.getAlias() + " refer data not found :" + fieldName + " - " + refTableName);
                continue;
            }

            for (Map<String, Object> tableFields : data.getDataArray()) {
                Object refKey = tableFields.get(fieldName);
                if (refKey == null) {
                    continue;
                }
                if (refKey instanceof String[]) {
                    String[] keys = (String[])refKey;
                    List<Object> refValues = new ArrayList<Object>();
                    for (int i=0; i<keys.length; i++) {
                        if (this.isEmptyStr(keys[i])) {
                            continue;
                        }
                        Object refValue = referTableMapping.get(keys[i]);
                        if (refValue != null) {
                            refValues.add(refValue);
                        } else {
                            log.Warning("table " + data.getAlias() + ":[]" + fieldName + " refer value not found :" + keys[i]);
                            continue;
                        }
                    }
                    tableFields.put(fieldName, refValues.toArray());
                } else {
                    if (this.isEmptyStr(refKey)) {
                        tableFields.put(fieldName, 0);
                        continue;
                    }
                    Object refValue = referTableMapping.get(refKey);

                    if (refValue != null) {
                        tableFields.put(fieldName, refValue);
                    } else {
                        tableFields.put(fieldName, null);
                        //tableFields.put(fieldName, 0);
                        log.Warning("table " + data.getAlias() + ":" + fieldName + " refer value not found :" + refKey);
                        continue;
                    }
                }


            }
        }
    }

    public boolean isEmptyStr(Object obj) {
        if (obj instanceof String) {
            String objS = (String)obj;
            return objS.isEmpty();
        }
        return false;
    }



    public static void main(String[] args) throws Exception {
        ExcelParser parser = new ExcelParser();
        parser.parse(new File("/Users/hejialin/Downloads/testconvert"));
        File output = new File("/Users/hejialin/Downloads/testconvert/output");
        output.mkdirs();
        for (TableData data : parser.getData().values()) {
            new JsonConverter().convert(data, output.getAbsolutePath());
        }

    }
}
