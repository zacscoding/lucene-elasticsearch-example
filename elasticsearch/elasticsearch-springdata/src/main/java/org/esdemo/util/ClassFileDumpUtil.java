package org.esdemo.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author zacconding
 * @Date 2018-04-06
 * @GitHub : https://github.com/zacscoding
 */
public class ClassFileDumpUtil {

    public static void writeByteCode(byte[] bytes, File file) {
        if(file == null) {
            return;
        }
        BufferedOutputStream buffOut = null;
        try {
            buffOut = new BufferedOutputStream(new FileOutputStream(file));
            buffOut.write(bytes);
            buffOut.flush();
            buffOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
