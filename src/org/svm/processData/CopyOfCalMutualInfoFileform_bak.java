package org.svm.processData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ictclas4j.utility.ExcludeStopWords;

import org.svm.utility.Utility;

public class CopyOfCalMutualInfoFileform_bak {
/**
 * read the news from a file, and process it to get the mi and df.
 */
	public static void main(String args[]){
		CopyOfCalMutualInfoFileform_bak mi=new CopyOfCalMutualInfoFileform_bak();
		mi.work("D:\\学习\\自学\\natural language  processing\\2014年毕设-用SVM文本分类\\data\\sougou labs\\Reduced training",1000);
		//		mi.getMiTopNFromFile("svmData\\termMI.txt");
	}
	/**
	 * 思路：把排好序的MI筛除字母开头和数字开头的词组，构成N个词汇。输出到一个文件中可供后面使用。
	 * 
	 */
	@Deprecated
	public void getMiTopN(ArrayList<Map.Entry<String,Double>> miList){
	    LinkedHashMap<String, Double> miTopN=new LinkedHashMap<String, Double>();
		for(Map.Entry<String,Double> termEntry:miList){
			String term=termEntry.getKey();
//			if(term.matches("[A-Za-z0-9０-９]+.*")){
			if(miTopN.size()<Utility.MI_MAX_NUM){
				miTopN.put(term, termEntry.getValue());
			}else{
				break;
			}
		}
		
		//write new mi list to text.
		BufferedWriter out = null;   
		 String miFile="svmData\\miTopN.txt";
		    try {
		    	File file1=new File(miFile);
		    	 if (!file1.exists()) {
	   			    file1.createNewFile();
		    	 }
		         out = new BufferedWriter(new OutputStreamWriter(   
		                  new FileOutputStream(file1)));   
		         for(Map.Entry<String,Double> entry:miTopN.entrySet()){
//		        	 String wordMI="";
//		        	 wordMI+=entry.getKey()+" "+entry.getValue().toString()+"\n";
		        	 out.write(entry.getKey()+"\n");   //here may try to check whether it is right.
		         }
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
	@Deprecated
	public void getMiTopNFromFile(String fileName){
		BufferedWriter out = null;  
		BufferedReader reader=null;
		String miFile="svmData\\miTopN.txt";
	    try {
	    	reader = new BufferedReader(new FileReader(fileName));
	    	File file1=new File(miFile);
	    	 if (!file1.exists()) {
   			    file1.createNewFile();
	    	 }
	         out = new BufferedWriter(new OutputStreamWriter( 
	                  new FileOutputStream(file1)));  
			int num=0;
	    	String tempString;
	    	while ((tempString = reader.readLine()) != null){ 
	    		String[] splitString=tempString.split(" ");
				if(splitString[0].matches("[A-Za-z0-9０-９]+.*")){
					System.out.println(splitString[0]+" is filtered");
					continue;
				}else{
					if(num<Utility.MI_MAX_NUM){
						 out.write(splitString[0]+"\n");   //here may try to check whether it is right
					}else{
						break;
					}
					num++;
				}
	    	}
		}catch(Exception e){
			e.printStackTrace();
		} finally {   
            try {   
                out.close();  
                reader.close();
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
		}
	}
	
	/**
	 * @思路：1.进入某一个类别的目录下，读取每篇文章，对他进行词频统计并把出现的词加入总的词频表中。
	 * 2.对每个词语分别统计他在每一个类别下的词频。
	 * @param N is the num of feature you need
	 */
	public void work(String DirectorName,int N){
		int totalDocNum=0;
		File directory=new File(DirectorName);
		HashMap<String,Integer> numOfDocEachClass=new HashMap<String,Integer>();
		//the total statistic model to store word's doc freq for each class 
    	HashMap<String,HashMap<String,Integer>> totalWordList=new HashMap<String,HashMap<String,Integer>>();
    	
		
		for(File classFile:directory.listFiles()){
			String className=classFile.getName();
			int fileNum=classFile.list().length;
			totalDocNum+=fileNum;
			//add class  doc num to map
			if(!numOfDocEachClass.containsKey(className)){
				numOfDocEachClass.put(className,fileNum );
			}else{
				numOfDocEachClass.put(className, fileNum+numOfDocEachClass.get(className));
			} 
			
			//go into the text and analysis it.
			for(File text:classFile.listFiles()){
				BufferedReader reader = null; 
		    	try {
			    	reader = new BufferedReader(new FileReader(text)); 
			    	String textString="",tempString;
			
			    	while ((tempString = reader.readLine()) != null){ 
			    		textString+=tempString;
			    	}
			    	textString=textString.replaceAll("[\t\n\r 　]", "").trim();
			    	
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
	        		
	        		//put word list of this news to a txt 
	        		{
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
	        			String docTermFile="svmData\\docTerm.txt";
		        		BufferedWriter out = null; 
		        		 
		        	    try {  
		        	    	File file1=new File(docTermFile);
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
	        		}//end of put word list
	        		
	        		//participate to the total analysis.
	        		for(Map.Entry<String, Integer> entry: wordListOfDoc.entrySet()){
	        			String word;
	        			word=entry.getKey();
	        			if(	totalWordList.containsKey(word)){
	        				HashMap<String,Integer> temp=totalWordList.get(word);
	        				if(temp.containsKey(className)){
	        					temp.put(className, temp.get(className)+1);
	        				}else{
	        					temp.put(className, 1);
	        				}
	        			}else{
	        				HashMap<String,Integer> temp=new HashMap<String,Integer>();
	        				temp.put(className, 1);
	        				totalWordList.put(word, temp);
	        			}//end of if
	        		}//end of for
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
			}//end for classFile
		}//end for directoryFile  total text read finished.
		
    	//begin to get the mutual infomation of word.
		//by the way, i will get the word freq in the whole corpus. 
		HashMap<String,Integer> wordDocFreqInCorpus=new HashMap<String,Integer>();
		HashMap<String,Double> wordMIList=new HashMap<String,Double>();
		
		for(Map.Entry<String,HashMap<String,Integer>> entry: totalWordList.entrySet()){
			String word=entry.getKey();
			HashMap<String,Integer> temp=entry.getValue();
			Double wordMI=0.0;
			Integer wordFreq=0;
			for(Map.Entry<String, Integer> entryInner: temp.entrySet()){
				wordFreq+=entryInner.getValue();
			}
			wordDocFreqInCorpus.put(word, wordFreq);
			//deal with each word and its class
			for(Map.Entry<String, Integer> entryInner: temp.entrySet()){
				String className=entryInner.getKey();
				Integer wordFreqUnderClass=entryInner.getValue();
				Double tmpMI=0.0;
				tmpMI=(double) (numOfDocEachClass.get(className)/(double)totalDocNum)
						*(Math.log(wordFreqUnderClass)+Math.log(totalDocNum)-Math.log(wordFreq)-Math.log(numOfDocEachClass.get(className))) ;
//				System.out.println(className+numOfDocEachClass.get(className)+" "+"wordFreqUnderClass"+wordFreqUnderClass+" "+"wordFreq"+wordFreq+" "+"totalNumOfDoc"+totalDocNum+"MI"+tmpMI);
				wordMI+=tmpMI;
			}
			//now get the MI for the word 
//			System.out.println(word+wordMI);
			wordMIList.put(word, wordMI);
		}
		
		//sort it .
		ArrayList<Map.Entry<String,Double>> l = new ArrayList<Map.Entry<String,Double>>(wordMIList.entrySet());
		Collections.sort(l, new Comparator<Map.Entry<String,Double>>() {   
            public int compare(Map.Entry<String,Double> o1, Map.Entry<String,Double> o2) {   
                if(o2.getValue() - o1.getValue() > 0)
                	return 1;
                else
                	return -1;
            }   
        });
		
		//write the mi List to a txt
		String MIFile="svmData\\miTopN.txt";
		BufferedWriter out = null;   
	    try {
	    	File file1=new File(MIFile);
	    	 if (!file1.exists()) {
   			    file1.createNewFile();
	    	 }
	         out = new BufferedWriter(new OutputStreamWriter(   
	                  new FileOutputStream(file1)));  
	         int wordCount=0;
	         for(Map.Entry<String, Double> wordEntry:l){
	        	 String wordmi="";
	        	 wordCount++;
	        	 wordmi+=wordEntry.getKey()+"\n";//" "+wordEntry.getValue().toString()
	        	 out.write(wordmi);   //here may try to check whether it is right.
	        	 if(wordCount>=N)
	        		 break;
	         }
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            try {   
                out.close();   
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
        }
//	    getMiTopN(l);
	    //sort term freq
		ArrayList<Map.Entry<String,Integer>> ll = new ArrayList<Map.Entry<String,Integer>>(wordDocFreqInCorpus.entrySet());
		Collections.sort(ll, new Comparator<Map.Entry<String,Integer>>() {   
            public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {   
                if(o2.getValue() - o1.getValue() > 0)
                	return 1;
                else
                	return -1;
            }   
        });
	    
    	//write the term freq to txt
	    String termDFFile="svmData\\termDF.txt";
	    try {
	    	File file1=new File(termDFFile);
	    	 if (!file1.exists()) {
   			    file1.createNewFile();
	    	 }
	         out = new BufferedWriter(new OutputStreamWriter(   
	                  new FileOutputStream(file1)));   
	         for(Map.Entry<String, Integer> wordEntry: ll){
	        	 String wordDF="";
	        	 wordDF+=wordEntry.getKey()+" "+wordEntry.getValue().toString()+"\n";
	        	 out.write(wordDF);   //here may try to check whether it is right.
	         }
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
	
}