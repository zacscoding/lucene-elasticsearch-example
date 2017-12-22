package com.lucene.learn;

import java.io.File;

import org.junit.Test;

public class FIndDirectory {
    private String dirPath = "D:\\lucene";
    private String target = "index";
    
    @Test
    public void test() {
        File dir = new File(dirPath);
        search(dir,target);
    }
    
    private void search(File dir, String target) {
        if(dir == null)
            return;
        
        //System.out.println("check :: " + dir.getName());
        if(dir.getName().equals(target)) {
            System.out.println(dir.getAbsolutePath());
        }
        
        File[] files = dir.listFiles();        
        if(files ==null || files.length ==0)
            return;
        
        for(File file : files) {
            if(file.isDirectory()) {
                search(file,target);
            }
        }        
    }
    
    

}
