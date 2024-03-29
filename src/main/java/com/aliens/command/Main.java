package com.aliens.command;

import com.aliens.command.excel.Config;
import com.aliens.command.excel.ExcelParser;
import com.aliens.command.excel.JsonConverter;
import com.aliens.command.excel.SheetParser;
import com.aliens.command.excel.model.TableData;
import com.aliens.command.excel.template.ConverterManager;
import com.aliens.command.excel.template.dialect.GolangDialect;
import com.aliens.command.log.SystemLogger;
import com.aliens.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejialin on 2018/3/13.
 */
public class Main {

    public static final String PARAM_NAME_DIALECT = "-d"; //输出语言方言

    public static final String PARAM_NAME_TEMPLATE = "-t"; //模板路径

    public static final String PARAM_NAME_EXCLUDE = "-exclude"; //过滤表格名

    public static final String PARAM_NAME_INCLUDE = "-include"; //包含表格名

    public static final String PARAM_NAME_T_INCLUDE = "-t_include"; //包含数据表名

    public static final String PARAM_NAME_FIELD_FILTER = "-ff"; //过滤字段名

    public static final String PARAM_NAME_FIELD_TYPE_ALIAS = "-fta"; //字段类型别名替换

    public static final String PARAM_NAME_INPUT = "-i"; //json输出目录

    public static final String PARAM_NAME_OUTPUT = "-o"; //输出路径

    public static final String PARAM_NAME_LINE = "-l"; //解析对应行号

    public static final String PARAM_NAME_ALIAS = "-a"; //表格别名

    public static final String PARAM_NAME_LOGLEVEL = "-log"; //

    public static void main(String[] args) {
        //dealUECommand(args);

//        ExcelParser parser = new ExcelParser();
//        parser.parse(new File("/Users/hejialin/git/server/tt_earth/tools/table"));
//        for (TableData tableData : parser.getData().values()) {
//            List<Map<String, Object>> datas = tableData.getDataArray();
//
//            if (!tableData.haveField()) {
//                return;
//            }
//
//            JSONArray array = new JSONArray();
//            JSONObject rowData;
//            for (Map<String, Object> data : datas) {
//                rowData = new JSONObject(true);
//                rowData.putAll(data);
//                array.add(rowData);
//            }
//            System.out.println(array.toJSONString());
//
//        }
        //File output = new File("/Users/hejialin/git/aliens/aliensboot/aliensboot-custom-servers/slg_server/data/table_out_json");
//        parser.parse(new File("/Users/hejialin/git/aliens/aliensboot/aliensboot-toolkit/excel2all/test"));
//
//        File output = new File("/Users/hejialin/git/aliens/aliensboot/aliensboot-toolkit/excel2all/test");
//        output.mkdirs();
//        for (TableData data : parser.getData().values()) {
//            new JsonConverter().convert(data, output.getAbsolutePath());
//        }
        dealExcelCommand(args);
    }

    private static void dealExcelCommand(String[] args) {
        Map<String, String> params = new HashMap<String, String>();

        String name = null;
        String value = null;
        for (String arg : args) {
            if (name == null) {
                name = arg;
            } else if (value == null) {
                value = arg;
            }
            if (name != null && value != null) {
                params.put(name, value);
                name = null;
                value = null;
            }
        }

        System.out.println("args:" + params);


        String inputPath = params.get(PARAM_NAME_INPUT);
        if (inputPath == null || inputPath.isEmpty()) {
            System.err.println("invalid input path -i :" + inputPath);
            return;
        }

        String outPath = params.get(PARAM_NAME_OUTPUT);
        if (outPath == null || outPath.isEmpty()) {
            System.err.println("invalid outPath path -o :" + outPath);
            return;
        }

        String dialect = params.get(PARAM_NAME_DIALECT);
        if (dialect == null || dialect.isEmpty()) {
            System.err.println("invalid dialect -d :" + dialect);
            return;
        }

        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            System.err.println("invalid input path -i :" + inputPath);
            return;
        }

        String filters = params.get(PARAM_NAME_EXCLUDE);
        if (filters != null){
            String[] filter = filters.split(",");
            Config.setExclude(filter);
        }

        String includes = params.get(PARAM_NAME_INCLUDE);
        if (includes != null){
            String[] include = includes.split(",");
            Config.setInclude(include);
        }

        String tableIncludes = params.get(PARAM_NAME_T_INCLUDE);
        if (tableIncludes != null){
            String[] tableInclude = tableIncludes.split(",");
            Config.setTableInclude(tableInclude);
        }


        String fieldFilter = params.get(PARAM_NAME_FIELD_FILTER);
        if (fieldFilter != null){
            String[] filter = fieldFilter.split(",");
            Config.setFieldFilter(filter);
        }

        String lineNo = params.get(PARAM_NAME_LINE);
        if (lineNo != null && !lineNo.isEmpty()) {
            String[] lineNos = lineNo.split(",");
            if (lineNos.length != SheetParser.lineNo.length) {
                System.err.println("invalid line param -l :" + lineNo);
                return;
            } else {
                SheetParser.lineNo[0] = Integer.parseInt(lineNos[0]);
                SheetParser.lineNo[1] = Integer.parseInt(lineNos[1]);
                SheetParser.lineNo[2] = Integer.parseInt(lineNos[2]);
            }
        }

        String fieldType = params.get(PARAM_NAME_FIELD_TYPE_ALIAS);
        if (fieldType != null && !fieldType.isEmpty()) {
            String[] aliasArray = fieldType.split(",");
            for (int i =0; i<aliasArray.length; i++) {
                String[] aliasMapping = aliasArray[i].split(":");
                if (aliasMapping != null && aliasMapping.length == 2) {
                    Config.setFiledTypeAlias(aliasMapping[0].trim(), aliasMapping[1].trim());
                }
            }
        }

        String logLevel = params.get(PARAM_NAME_LOGLEVEL);
        SystemLogger.showInfo = "info".equals(logLevel);

        String alias = params.get(PARAM_NAME_ALIAS);
        if (alias != null && !alias.isEmpty()) {
            String[] aliasArray = alias.split(",");
            for (int i =0; i<aliasArray.length; i++) {
                String[] aliasMapping = aliasArray[i].split(":");
                if (aliasMapping != null && aliasMapping.length == 2) {
                    Config.setAlias(aliasMapping[0].trim(), aliasMapping[1].trim());
                }
            }
        }

        String templatePath = params.get(PARAM_NAME_TEMPLATE);

        ExcelParser parser = new ExcelParser();

        parser.parse(inputFile);

        if (dialect.equals("json")) {
            File output = new File(outPath);
            output.mkdirs();
            for (TableData data : parser.getData().values()) {
                new JsonConverter().convert(data, output.getAbsolutePath());
            }
        } else if (dialect.equals("go")) {
            ConverterManager template = new ConverterManager(templatePath);
            String content = template.convert(parser.getData().values(), GolangDialect.getInstance());
            FileUtil.instance.writeContent(outPath, content);
        } else if (dialect.equals("javascript")) {
            ConverterManager template = new ConverterManager(templatePath);
            String content = template.convert(parser.getData().values(), GolangDialect.getInstance());
            FileUtil.instance.writeContent(outPath, content);
        }
    }
}



