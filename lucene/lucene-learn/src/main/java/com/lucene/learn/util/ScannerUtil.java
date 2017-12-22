package com.lucene.learn.util;

import java.util.Scanner;

public class ScannerUtil {
    private ScannerUtil(){}
    
    private static final Scanner scanner = new Scanner(System.in);
    
    public static String nextLine(String command) {
        System.out.print(command);
        return scanner.nextLine();
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(scanner != null)
            scanner.close();
    }

}
