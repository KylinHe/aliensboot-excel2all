package com.aliens.command.excel.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliens.util.CharacterUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hejialin on 2018/3/10.
 */
public class TableData {

    private String alias;

    private String name;

    private String fixName;

    private String upperName;

    //field header
    private List<TableField> fieldInfo = new ArrayList<TableField>();

    //refer other table field   field alias- mapping table alias
    private Map<String, String> refField = new HashMap<String, String>();

    //all data
    private List<Map<String, Object>> dataArray = new ArrayList<Map<String, Object>>();


    //id - alias mapping
    private Map<String, Object> idMapping = new HashMap<String, Object>();

    public static final Pattern PREFIX_REG = Pattern.compile("^(\\S*\\D)(\\d+)$");

    public TableData(String alias) {
        this.alias = alias;
        this.name = alias;
    }

    public void addRefField(String fieldName, String mappingTableName) {
        refField.put(fieldName, mappingTableName);
    }

    public boolean haveRef() {
        return !this.refField.isEmpty();
    }

    public Map<String, String> getRefField() {
        return refField;
    }

    public String getAlias() {
        return alias;
    }

    public String getName() {
        return name;
    }

    public String getFixName() {
        if (this.fixName == null) {
            this.fixName = CharacterUtil.instance.transferCamelCasing(this.name);
        }
        return this.fixName;
    }

    public String getUpperName() {
        if (this.upperName == null) {
            this.upperName = this.name.toUpperCase();
        }
        return this.upperName;
    }


    // 过滤字段
    public void filterField() {
        Iterator<TableField> fieldIter = fieldInfo.iterator();
        while (fieldIter.hasNext()) {
            TableField field = fieldIter.next();
            if (field.getFieldType() == FieldType.ENUM_NAME) {
                fieldIter.remove();
            }
        }

    }


    //合并字段
    public void mergeField() {
        //Map<String, TableField> repeatFields = new HashMap<String, TableField>();

        Map<String, List<String>> repeatCounter = new HashMap<String, List<String>>();

        for (TableField field : fieldInfo) {
            String name = getPrefixName(field.getName());
            List<String> counter = repeatCounter.get(name);
            if (counter == null) {
                counter = new ArrayList<String>();
                repeatCounter.put(name, counter);
            }
            counter.add(field.getName());
        }


        //合并字段名
        Iterator<TableField> fieldIter = fieldInfo.iterator();
        while (fieldIter.hasNext()) {
            TableField field = fieldIter.next();
            String name = getPrefixName(field.getName());
            List<String> counter = repeatCounter.get(name);

            //处理重复字段
            if (counter != null && counter.size() > 1) {
                if (counter.get(0).equals(field.getName())) {
                    field.setName(name);
                    field.setSubType(field.getFieldType());
                    field.setFieldType(FieldType.ARRAY);
                } else {
                    fieldIter.remove();
                }
            }
        }



        //合并数据
        for (Map<String, Object> rowData : dataArray) {
            Map<String, List<Object>> margeData = new HashMap<String, List<Object>>();
            for (String fieldName : rowData.keySet()) {
                String name = getPrefixName(fieldName);
                List<String> counter = repeatCounter.get(name);
                //处理重复字段
                if (counter != null && counter.size() > 1) {
                    List<Object> dataList = margeData.get(name);
                    if (dataList == null) {
                        dataList = new ArrayList<Object>();
                        margeData.put(name, dataList);
                    }

                    Object dataValue = rowData.get(fieldName);
                    TableField field = this.getFieldByName(fieldName);
                    // 关联类型为int
                    if (field != null && field.getFieldType() == FieldType.REFER) {
                        if (dataValue == null || "".equals(dataValue)) {
                            dataValue = 0;
                            rowData.put(fieldName, dataValue);
                        }
                    }

                    dataList.add(dataValue);
                }
            }

            for (Map.Entry<String, List<String>> entry : repeatCounter.entrySet()) {
                if (entry.getValue().size() <= 1) {
                    continue;
                }
                for (String fieldName : entry.getValue()) {
                    rowData.remove(fieldName);
                }
            }

            for (Map.Entry<String, List<Object>> margeEntry : margeData.entrySet()) {
                rowData.put(margeEntry.getKey(), margeEntry.getValue().toArray());
            }
        }

    }

    public String getPrefixName(String name) {
        Matcher bodyMatcher = PREFIX_REG.matcher(name);
        if (bodyMatcher.find()) {
            return bodyMatcher.group(1);
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getIdMapping() {
        return idMapping;
    }

    public void addTableIDMapping(Object id, String name) {
        idMapping.put(name, id);
    }

    public List<TableField> getFieldInfo() {
        return fieldInfo;
    }

    public boolean haveField() {
        return fieldInfo != null && !fieldInfo.isEmpty();
    }

    public TableField getField(int index) {
        if (fieldInfo == null || fieldInfo.size() <= index) {
            return null;
        }
        return fieldInfo.get(index);
    }

    public TableField getFieldByName(String fieldName) {
        for (TableField field : fieldInfo) {
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    public TableField getFieldByType(FieldType fieldType) {
        for (TableField field : fieldInfo) {
            if (fieldType.equals(field.getFieldType())) {
                return field;
            }
        }
        return null;
    }

    public void setFieldInfo(List<TableField> fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public List<Map<String, Object>> getDataArray() {
        return dataArray;
    }

    public void addData(Map<String, Object> rowData) {
        this.dataArray.add(rowData);
    }

    public String getJsonData() {
        JSONArray array = new JSONArray();
        JSONObject rowData = null;
        for (Map<String, Object> data : dataArray) {
            rowData = new JSONObject(true);
            rowData.putAll(data);
            if (rowData.containsKey("Name")){//删除数据中的name字段
                rowData.remove("Name");
            }
            array.add(rowData);
        }
        if (array.size() == 1) {
            return rowData.toJSONString();
        }
        return array.toJSONString();
    }

    @Override
    public String toString() {
        return "TableData{" +
                "fieldInfo=" + fieldInfo +
                ", dataArray=" + dataArray +
                '}';
    }

}
