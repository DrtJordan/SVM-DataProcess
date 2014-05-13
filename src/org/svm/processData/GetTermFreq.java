package org.svm.processData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.ictclas4j.utility.ExcludeStopWords;

/**
 * @function:get the frequency of term. 
 * @author weiwei
 *
 */
public class GetTermFreq {
		public static void writeToFile(String className,String outfile,HashMap<String,Integer> wordListOfDoc){
    		//put word list of this news to a txt 
			String wordListOfDocToString=className+"\n";
			String word;
			Integer wordFreq;
			for(Map.Entry<String, Integer> entry: wordListOfDoc.entrySet()){
    			word=entry.getKey();
    			wordFreq=entry.getValue();
    			wordListOfDocToString+=word+":"+wordFreq.toString()+" ";
    		}
			wordListOfDocToString+="\n";
			
			//begin to write.
    		BufferedWriter out = null; 
    		 
    	    try {  
    	    	File file1=new File(outfile);
    	    	 if (!file1.exists()) {
        			    file1.createNewFile();
    	    	 }
    	         out = new BufferedWriter(new OutputStreamWriter(   
    	                  new FileOutputStream(file1, true)));   
    	                 out.write(wordListOfDocToString);   
    	        } catch (Exception e) {   
    	            e.printStackTrace();   
    	        } finally {   
    	            try {   
    	                out.close();   
    	            } catch (IOException e) {   
    	                e.printStackTrace();   
    	            }   
    	        } 
		}
        public static HashMap<String,Integer> getTF(String className,File text,String outfile){
        	BufferedReader reader = null; 
	    	try {
		    	reader = new BufferedReader(new FileReader(text)); 
		    	String textString="",tempString;
		
		    	while ((tempString = reader.readLine()) != null){ 
		    		textString+=tempString;
		    	}
		    	textString=textString.replaceAll("[\t\n\r ¡¡]", "").trim();
		    	
		    	String[] resultArray =ExcludeStopWords.excludeStopWords(textString).split(" ");
        		//wordListOfDoc for every document record the word frequency. 
        		HashMap<String,Integer> wordListOfDoc=new HashMap<String,Integer>();
        		
        		//here may have the problem that the address of freq is not given proper to it 
        		//when get method is called
        		for (int j= 0; j< resultArray.length; j++) {
        			if(wordListOfDoc.containsKey(resultArray[j])){
        				wordListOfDoc.put(resultArray[j], wordListOfDoc.get(resultArray[j])+1);
        			}else{
        				wordListOfDoc.put(resultArray[j], 1);
        			}
        		}
        		
        		writeToFile(className,outfile,wordListOfDoc);
        		if(wordListOfDoc.size()!=0)
        			return wordListOfDoc;
        		

	         }catch(Exception e){
	        	 e.printStackTrace();
	         }
	    	return null;
      }
}
