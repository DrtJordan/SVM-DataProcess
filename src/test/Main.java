package test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;


public class Main {
    public static void main(String args[]) throws IOException {
    	 String filePath = "svmData/test1/t.txt";  
         File fp = new File(filePath);  
         // 目录已存在创建文件夹  
         if (!fp.exists()) {  
             fp.mkdirs();// 目录不存在的情况下，会抛出异常  
         }  
         System.out.println("执行结束"+filePath);  
    }
}