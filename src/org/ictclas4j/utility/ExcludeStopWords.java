package org.ictclas4j.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.ictclas4j.bean.SegResult;

import org.ictclas4j.segment.SegTag;

public class ExcludeStopWords {
	public static final String stopWordTable = "Data/StopWordTable.txt";
	public static Set stopWordSet ;
	static {
		System.out.println("hello i am in static set stopwordset");
		try{
			//load stop words table.
			BufferedReader StopWordFileBr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(stopWordTable)), "GBK"));
			
	        // �������ͣ�ôʵļ���
			stopWordSet= new HashSet<String>(); // ���绯ͣ�ôʼ�
	        String stopWord = null;
	        for (; (stopWord = StopWordFileBr.readLine()) != null;) {
	            stopWordSet.add(stopWord);
	        }
	        
//	        for(Object str:stopWordSet.toArray()){
//	        	System.out.println((String)str);
//	        }
	        
	        
		}catch (FileNotFoundException e) {
	        e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public static String excludeStopWords(String text) {
        try{
		
		SegTag st = new SegTag(1);
		SegResult sr = st.split(text);
		String spiltResultStr=sr.getFinalResult();
	    //System.out.println(spiltResultStr);
	    
		String[] resultArray = spiltResultStr.split(" ");
        // ����ͣ�ôʺ������ִ�ͷ�Ĵ���
        for (int i = 0; i < resultArray.length; i++) {
            if (stopWordSet.contains(resultArray[i].trim())) {
                resultArray[i] = null;
            }else if(resultArray[i].trim().matches("[+\\-&|!(){}\\[\\]^\"~*?:(\\)0-9��-��A-Za-z��-�ڣ�-��]+.*")){
            	resultArray[i] = null;
            }
        }
        // �ѹ��˺���ַ���������뵽һ���ַ�����
        StringBuffer finalStr = new StringBuffer();
        for (int i = 0; i < resultArray.length; i++) {
            if (resultArray[i] != null) {
                finalStr = finalStr.append(resultArray[i].trim()).append(" ");
            }
        }
		//System.out.println(finalStr);
		return finalStr.toString();
    } catch (Exception e) {
        e.printStackTrace();
    }
        return null;
	}
	
	public static void main(String[] args){
		ExcludeStopWords.excludeStopWords("��");
	}

}
