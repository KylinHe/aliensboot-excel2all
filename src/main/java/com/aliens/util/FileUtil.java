package com.aliens.util;

import com.aliens.command.log.SystemLogger;

import java.io.*;

/**
 * Created by hejialin on 2018/3/12.
 */
public enum FileUtil {

    instance;

    private SystemLogger log = new SystemLogger();

    public String readContent(String filePath) {
        String encoding = "UTF-8";
        File file = new File(filePath);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(filecontent);

        } catch (FileNotFoundException e) {
            log.Info(e.getMessage());
        } catch (IOException e) {
            log.Info(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.Info(e.getMessage());
                }
            }
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            log.Info(e.getMessage());
            return null;
        }
    }

    public void writeContent(String filePath, String content) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            fos = new FileOutputStream(filePath);
            osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(content);
            osw.flush();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.Info(e.getMessage());
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    log.Info(e.getMessage());
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.Info(e.getMessage());
                }
            }
        }
    }
}
