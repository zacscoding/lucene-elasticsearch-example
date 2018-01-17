package com.lucene.learn.util;

import java.io.File;

/**
 * @author zacconding
 * @Date : 2018-01-15
 * @GitHub : https://github.com/zacscoding
 */
public class FileUtil {
    public static void mkdirs(String filepath, boolean deleteIfExist) {
        File file = new File(filepath);
        if(file.exists()) {
            if(deleteIfExist) {
                file.delete();
                file.mkdirs();
            }
        }
        else {
            file.mkdirs();
        }
    }
}
