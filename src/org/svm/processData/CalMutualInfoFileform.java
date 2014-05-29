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
import java.util.Map.Entry;

import org.svm.utility.Utility;

public class CalMutualInfoFileform {
	public void CalMutualInfoFileForm(){
		numOfDocEachClass=null;
		totalWordList=null;
		wordDocFreqInCorpus=null;
		totalDocNum=0;
	}
	
/**
 * read the news from a file, and process it to get the mi and df.
 */
	public static void main(String args[]){
		CalMutualInfoFileform mi=new CalMutualInfoFileform();
		//mi.getWordSegmentToText("D:\\学习\\自学\\natural language  processing\\2014年毕设-用SVM文本分类\\data\\sougou labs\\Reduced cross validate", "svmData\\5-flods 5 class");
//		mi.work("D:\\学习\\自学\\natural language  processing\\2014年毕设-用SVM文本分类\\data\\sougou labs\\2class exp\\Reduced training","svmData\\2class exp");
		mi.getTopNFromFile("svmData\\5-flods 5 class\\termCHI.txt","svmData\\5-flods 5 class\\chiTopN2000.txt",2000);
		mi.getTopNFromFile("svmData\\5-flods 5 class\\termIG.txt","svmData\\5-flods 5 class\\igTopN2000.txt",2000);
		//mi.calFSFromFile("svmData\\5-flods 5 class", "svmData\\5-flods 5 class");
	}
	/**
	 * 思路：把排好序的MI筛除字母开头和数字开头的词组，构成N个词汇。输出到一个文件中可供后面使用。
	 * 
	 */
	@Deprecated
	public void getMiTopN(ArrayList<Map.Entry<String,Double>> miList,String storeDirectory){
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
		 String miFile=storeDirectory+File.separator+"miTopN.txt";
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
	public void getWordSegmentToText(String DirectorName,String storeDirectory){
		File directory=new File(DirectorName);
		for(File classFile:directory.listFiles()){
			String className=classFile.getName();
			//go into the text and analysis it.
			for(File text:classFile.listFiles()){
				GetTermFreq.getTF(className, text,storeDirectory+File.separator+ "docTerm.txt");
			}
		}
	}
	public void getTopNFromFile(String infileName,String outfile,int N){
		BufferedWriter out = null;  
		BufferedReader reader=null;

	    try {
	    	reader = new BufferedReader(new FileReader(infileName));
	    	File file1=new File(outfile);
	    	 if (!file1.exists()) {
   			    file1.createNewFile();
	    	 }
	         out = new BufferedWriter(new OutputStreamWriter( 
	                  new FileOutputStream(file1)));  
			int num=0;
	    	String tempString;
	    	while ((tempString = reader.readLine()) != null){ 
	    		String[] splitString=tempString.split(" ");
				if(num<N){
					 out.write(splitString[0]+"\n");   //here may try to check whether it is right
				}else{
					break;
				}
					num++;
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
	 * @param DirectoryName 是语料所在的文件夹
	 * @param storeDirectory 是存储文档所在的文件夹。
	 */
	public void work(String DirectorName,String storeDirectory){
        totalDocNum=0;                                                                                            
		File directory=new File(DirectorName);
		numOfDocEachClass=new HashMap<String,Integer>();
		//the total statistic model to store word's doc freq for each class 
    	totalWordList=new HashMap<String,HashMap<String,Integer>>();
    	
		
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
				HashMap<String,Integer> wordListOfDoc=GetTermFreq.getTF(className, text,storeDirectory+File.separator+ "docTerm.txt");
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
			}//end for classFile
		}//end for directoryFile  total text read finished.
		
		calMI(storeDirectory);
		
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
		BufferedWriter out = null;   
	    String termDFFile=storeDirectory+File.separator+"termDF.txt";
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
		
	    //write doc num per class to txt 
	    String statisticFile=storeDirectory+File.separator+"statistic.txt";
	    try {
	    	File file1=new File(statisticFile);
	    	 if (!file1.exists()) {
   			    file1.createNewFile();
	    	 }
	         out = new BufferedWriter(new OutputStreamWriter(   
	                  new FileOutputStream(file1)));   
	         for(Map.Entry<String, Integer> classEntry: numOfDocEachClass.entrySet()){
	        	 String classCount="";
	        	 classCount+=classEntry.getKey()+" "+classEntry.getValue().toString()+"\n";
	        	 out.write(classCount);   //here may try to check whether it is right.
	         }
	         out.close();
        } catch (Exception e) {   
            e.printStackTrace();   
        }
	}
	
	/**
	 * 如果从文本分词进行计算文本特征，必须先从calMI计算，再计算其他CHI or IG
	 * @param storeDirectory
	 */
	private void calMI(String storeDirectory){
    	//begin to get the mutual infomation of word.
		//by the way, i will get the word freq in the whole corpus. 
	    if(wordDocFreqInCorpus==null)
	    	wordDocFreqInCorpus=new HashMap<String,Integer>();
	    
		HashMap<String,Double> wordMIList=new HashMap<String,Double>();
		
		for(Map.Entry<String,HashMap<String,Integer>> entry: totalWordList.entrySet()){
			String word=entry.getKey();
			HashMap<String,Integer> temp=entry.getValue();
			Double wordMI=0.0;
			Integer wordFreq=0;
			for(Map.Entry<String, Integer> entryInner: temp.entrySet()){
				wordFreq+=entryInner.getValue();
			}
			
			if(wordDocFreqInCorpus==null)
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
		String MIFile=storeDirectory+File.separator+"termMI.txt";
		BufferedWriter out = null;   
	    try {
	    	File file1=new File(MIFile);
	    	 if (!file1.exists()) {
   			    file1.createNewFile();
	    	 }
	         out = new BufferedWriter(new OutputStreamWriter(   
	                  new FileOutputStream(file1)));  
	         for(Map.Entry<String, Double> wordEntry:l){
	        	 String wordmi="";
//	        	 wordCount++;
	        	 wordmi+=wordEntry.getKey()+" "+wordEntry.getValue().toString()+"\n";//
	        	 out.write(wordmi);   //here may try to check whether it is right.
//	        	 if(wordCount>=N)
//	        		 break;
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
	
	/**
	 * 思路：同CHI方法,
	 * @param storeDirectory
	 */
	private void calIG(String storeDirectory){
		HashMap<String,Double> IGMap=new HashMap<String,Double>();
		for(Entry<String, HashMap<String, Integer>> entry:totalWordList.entrySet()){
			String word=entry.getKey();
			HashMap<String,Integer> wordUnderClass=entry.getValue();
			//calculate the chi
			double ig=0.0;
			int nt=wordDocFreqInCorpus.get(word);//n(t)
			int not_t=totalDocNum-nt;
			double tmp=0.0;
			for(Entry<String,Integer> wordClass:wordUnderClass.entrySet()){
				String className=wordClass.getKey();
				int nc=numOfDocEachClass.get(className);
				int ct=wordClass.getValue();//n(ci,t)
				tmp=ct*Math.log((double)(ct)/nt)+(nc-ct)*Math.log((double)(nc-ct)/not_t);
				ig+=tmp;
			}
			IGMap.put(word, ig);
		}
		
		System.out.println(IGMap.size());
		//sort it .
		ArrayList<Map.Entry<String,Double>> l = new ArrayList<Map.Entry<String,Double>>(IGMap.entrySet());
		Collections.sort(l, new Comparator<Map.Entry<String,Double>>() {   
            public int compare(Map.Entry<String,Double> o1, Map.Entry<String,Double> o2) {   
                if(o2.getValue() - o1.getValue() > 0)
                	return 1;
                else
                	return -1;
            }   
        });
		
		//write the chi List to a txt storeDirectory
		String IGFile=storeDirectory+File.separator+"termIG.txt";
		BufferedWriter out = null;   
	    try {
	    	File file1=new File(IGFile);
	    	 if (!file1.exists()) {
   			    file1.createNewFile();
	    	 }
	         out = new BufferedWriter(new OutputStreamWriter(   
	                  new FileOutputStream(file1)));  
	         String wordmi;
	         
	         for(Map.Entry<String, Double> wordEntry:l){
	        	 
//			        	 wordCount++;
	        	 wordmi=wordEntry.getKey()+" "+wordEntry.getValue().toString()+"\n";//
	        	 out.write(wordmi);   //here may try to check whether it is right.
//			        	 if(wordCount>=N)
//			        		 break;
	         }
	         out.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	
	/**
	 * 对totalWordList进行整合加起来，就是词语在整个语料上的频率。
	 * @param path
	 */
	
	private void getWordDocFreq(){
		wordDocFreqInCorpus=new HashMap<String,Integer>();
		for(Map.Entry<String,HashMap<String,Integer>> entry: totalWordList.entrySet()){
			String word=entry.getKey();
			HashMap<String,Integer> temp=entry.getValue();
			Integer wordFreq=0;
			for(Map.Entry<String, Integer> entryInner: temp.entrySet()){
				wordFreq+=entryInner.getValue();
			}
			
			wordDocFreqInCorpus.put(word, wordFreq);
		}
	}
	/**
	 * read term df from termDF.txt
	 * @param path
	 */
	private void getTotalTermDF(String path){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(path));
	    	String tempString;
	    	wordDocFreqInCorpus=new HashMap<String,Integer>();
	    	while ((tempString = reader.readLine()) != null){ 
	    		String[] splitString=tempString.split(" ");
	    		String word=splitString[0].trim();
	    		int freq=Integer.valueOf(splitString[1]);
	    		if(!wordDocFreqInCorpus.containsKey(word)){
	    			wordDocFreqInCorpus.put(word,freq );
	    		}else{
	    			wordDocFreqInCorpus.put(word, freq+wordDocFreqInCorpus.get(word));
	    		}
	    	}
	    	reader.close();
		}catch(Exception e){
			
			e.printStackTrace();
		}

	}
	/**
	 *retrieval wordDF on each class by read file "docTerm.txt"</br>
	 *
	 * @param docTermPath
	 */
	private void getWordDFonPerClass(String docTermPath){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(docTermPath));
	    	String className;
	    	totalWordList=new HashMap<String,HashMap<String,Integer>>();
	    	while ((className = reader.readLine()) != null){ 
	    		//read the freq of term on per doc
	    		String tempString=reader.readLine();
	    		if(tempString==null)
	    			break;
	    		String[] splitString=tempString.split(" ");
	    		for(String termString:splitString){
	    			if(termString.trim()!=null){
	    				String word=termString.split(":")[0];
	    				if(	totalWordList.containsKey(word)){
	    					//here need to test if the totalWordList.get(word) get the map can be changed by assigned it @here.
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
	    			}
	    		}
	    	}
	     	reader.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	/**
	 * getNumOfDocEachClassAndTotalDocNum
	 * 
	 */
	private void getStatistic(String path){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(path));
			totalDocNum=0;
	    	String tempString;
	    	numOfDocEachClass=new HashMap<String, Integer>();
	    	while ((tempString = reader.readLine()) != null){ 
	    		String[] splitString=tempString.split(" ");
	    		int num=Integer.valueOf(splitString[1]);
	    		totalDocNum+=num;
	    		numOfDocEachClass.put(splitString[0],num );
	    	}
	    	reader.close();
		}catch(Exception e){
			
			e.printStackTrace();
		}
	}
	
	/**
	 * 功能：从已经经过处理的输出文本(termDF.txt,docTerm.txt,statistic.txt-总文档数，每个分类的文档数)中得到
	 *totalWordList,wordDocFreqInCorpus,numOfDocEachClass,totalDocNum这些数据，
	 *从而可以进行特征计算。
	 * 思路：
	 */
	public void calFSFromFile(String readDirectory,String ResultDirectory){
		//get wordDocFreqInCorpus - DF of term
		//getTotalTermDF(readDirectory+File.separator+"termDF.txt");
		
		//get totalWordList - that is the word DF on each class
		getWordDFonPerClass(readDirectory+File.separator+"docTerm.txt");
		getWordDocFreq();
		//get numOfDocEachClass and totalDocNum
		getStatistic(readDirectory+File.separator+"statistic.txt");
		
 		calIG(ResultDirectory);
		calCHI(ResultDirectory);
	}
	/**
	 * 程序思路：对totalWordList中每一个词计算对应的CHI,放到chiMap中，输出到‘termCHI.txt’
	 * 
	 */
	private void calCHI(String storeDirectory){
		HashMap<String,Double> chiMap=new HashMap<String,Double>();
		for(Entry<String, HashMap<String, Integer>> entry:totalWordList.entrySet()){
			String word=entry.getKey();
			HashMap<String,Integer> wordUnderClass=entry.getValue();
			//calculate the chi
			double chi=0.0;
			int nt=wordDocFreqInCorpus.get(word);
//			System.out.println("nt:"+nt);
			for(Entry<String,Integer> wordClass:wordUnderClass.entrySet()){
				String className=wordClass.getKey();
				int nc=numOfDocEachClass.get(className);
				
				int A=wordClass.getValue();
				int B=nt-A;
				int C=nc-A;
				int D=totalDocNum-B-C+A;
				
//				System.out.println("A:"+A+"B:"+B+"C:"+C+"D:"+D);
//				System.out.println("nc:"+nc);
				double tempCHI=0.0;
				tempCHI=(double)(totalDocNum*(A*D-C*B)*(A*D-C*B))/(nc*(totalDocNum-nc)*nt*(totalDocNum-nt));
//				System.out.println("tempCHI"+tempCHI);
				tempCHI*=(double)(nc)/totalDocNum;
//				System.out.println("tempCHI"+tempCHI);
				chi+=tempCHI;
			}
//			System.out.println("chi"+chi);
			chiMap.put(word, chi);
		}
		
		//sort it .
		ArrayList<Map.Entry<String,Double>> l = new ArrayList<Map.Entry<String,Double>>(chiMap.entrySet());
		Collections.sort(l, new Comparator<Map.Entry<String,Double>>() {   
            public int compare(Map.Entry<String,Double> o1, Map.Entry<String,Double> o2) {   
                if(o2.getValue() - o1.getValue() > 0)
                	return 1;
                else
                	return -1;
            }   
        });
		
		//write the chi List to a txt storeDirectory
		String CHIFile=storeDirectory+File.separator+"termCHI.txt";
		BufferedWriter out = null;   
	    try {
	    	File file1=new File(CHIFile);
	    	 if (!file1.exists()) {
   			    file1.createNewFile();
	    	 }
	         out = new BufferedWriter(new OutputStreamWriter(   
	                  new FileOutputStream(file1)));  
	         for(Map.Entry<String, Double> wordEntry:l){
	        	 String wordmi="";
//			        	 wordCount++;
	        	 wordmi+=wordEntry.getKey()+" "+wordEntry.getValue().toString()+"\n";//
	        	 out.write(wordmi);   //here may try to check whether it is right.
//			        	 if(wordCount>=N)
//			        		 break;
	         }
	         out.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	
	
	private void writeKey(String fileName,HashMap<Object,Object>l){
		
	}
	private void writeKeyValue(String fileName,HashMap<Object,Object>l){
		
	}
	private HashMap<String,Integer> numOfDocEachClass;
	private HashMap<String,HashMap<String,Integer>> totalWordList;
	private int totalDocNum;
	private HashMap<String,Integer> wordDocFreqInCorpus;
	
}