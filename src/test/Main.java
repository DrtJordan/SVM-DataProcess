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
         // Ŀ¼�Ѵ��ڴ����ļ���  
         if (!fp.exists()) {  
             fp.mkdirs();// Ŀ¼�����ڵ�����£����׳��쳣  
         }  
         System.out.println("ִ�н���"+filePath);  
    }
}