package com.ddm.app.utils;

import java.io.File;

public class ContentDeleter {

    public static boolean delete (File input){
        File[] content = input.listFiles();
        if(content != null){
            for (File file : content){
                delete(file);
            }
        }
        return input.delete();
    }
}
