package org.esdemo.util;

/**
 * @author zacconding
 * @Date 2018-04-11
 * @GitHub : https://github.com/zacscoding
 */
public class ThreadUtil {

    public static String getCurrentThreadInform() {
        Thread t = Thread.currentThread();
        return t == null ? "[thread is null. possible ?]" : ("[" + t.getId() + ", " + t.getName() + "]");
    }

}
