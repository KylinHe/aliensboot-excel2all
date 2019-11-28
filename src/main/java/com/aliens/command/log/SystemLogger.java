package com.aliens.command.log;

/**
 * Created by hejialin on 2018/3/10.
 */
public class SystemLogger implements ILogger {

    public static String currSheetName = "";

    public static String currColumnName = "";

    public static int currLine = 0;

    public static boolean showInfo = false;

    @Override
    public void Info(String content) {
        if (showInfo) {
            System.out.println(content);
        }
    }

    @Override
    public void Warning(String content) {
        System.out.println(content);
    }

    @Override
    public void Error(String content) {
        System.err.println("\033[31m " + content + " \033[0m");
        // System.exit(0);
    }
}
