package com.aliens.command.excel.template.dialect;

import com.aliens.command.excel.model.FieldType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejialin on 2018/3/12.
 */
public class GolangDialect implements Dialect {

    private static GolangDialect instance;

    public static Map<FieldType, String> fieldTypemapping = new HashMap<FieldType, String>();

    private GolangDialect() {

    }

    public static synchronized GolangDialect getInstance() {
        if (instance == null) {
            instance = new GolangDialect();
        }
        return instance;
    }

    static {
        fieldTypemapping.put(FieldType.STRING, "string");
        fieldTypemapping.put(FieldType.FLOAT, "float32");
        fieldTypemapping.put(FieldType.DOUBLE, "float64");
        fieldTypemapping.put(FieldType.BOOL, "bool");
        fieldTypemapping.put(FieldType.INT, "int32");
        fieldTypemapping.put(FieldType.LONG, "int64");

        fieldTypemapping.put(FieldType.ENUM, "int32");
        fieldTypemapping.put(FieldType.ENUM_NAME, "string");
        fieldTypemapping.put(FieldType.ID, "int32");
        fieldTypemapping.put(FieldType.NAME, "string");

        fieldTypemapping.put(FieldType.REFER, "int32");
        fieldTypemapping.put(FieldType.JSON, "string");
        fieldTypemapping.put(FieldType.TERM, "int32");
    }

    @Override
    public String getType(FieldType fieldType, FieldType subFieldType) {
        String prefix = "";
        if (fieldType == FieldType.ARRAY) {
//            if (subFieldType != FieldType.ENUM) {
//
//            }
            prefix = "[]";
            fieldType = subFieldType;
        }

        if (subFieldType == FieldType.ARRAY) {
            prefix += "[]";
        }
        String content = fieldTypemapping.get(fieldType);
        if (content == null) {
            content = "int32";
        }
        return prefix + content;
    }
}
