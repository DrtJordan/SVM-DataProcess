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
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ictclas4j.utility.ExcludeStopWords;
import org.xml.sax.SAXException;

/**
 * 
 * @author weiwei
 * @function: cal mutual information of each word, also get the term freq table of each doc. 
 */


public class CalMutualInfoXMLform {
	
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private HashMap<String,String> classMap;
	
	
	public void readClassMap(String fileName){
		//need a url to class map so that we can use it to do anything we wanna.
    	//For example "http://auto.sohu.com/" map to “car”
    	classMap=new HashMap<String,String>();
    	
    	//read class from txt    	
    	File file = new File(fileName); 
    	BufferedReader reader = null; 
    	try { 
	    	reader = new BufferedReader(new FileReader(file)); 
	    	String tempString = null; 
	    	//一次读入一行，直到读入null为文件结束 
	    	while ((tempString = reader.readLine()) != null){ 
	    	String[] urlAndClass=tempString.split("\t");
		    	if(urlAndClass.length==2){
		    		System.out.println(urlAndClass[0]+", "+urlAndClass[1]);
		    		classMap.put(urlAndClass[0],urlAndClass[1]);
		    	}else{
		    		System.out.println("read error!");
		    	} 
	    	}
    	} catch (IOException e) { 
    		e.printStackTrace(); 
    	} finally { 
	    	if (reader != null){ 
	    	try { 
	    		reader.close(); 
	    	} catch (IOException e1) { 
	    		e1.printStackTrace();
	    	} 
    	} 
      } 
   } 
    	
	
	/**
	 * 思路：1.对每篇文章进行分词（含去停词），对文章中出现的词进行词频统计，统计完之后我们
	 * 可以将这个存储到文件中。
	 * 2.对每个词存储他在每个类别下出现的文档数作为P(t^c),而P（t）=∑c P(t^c);而P(t)也应该记录到一个新文件中。
	 * 3.对每个类统计一下出现的文档数即为P(c)
	 */
	public void work(String xmlFileName){
		//read xml 
		try{
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
	    File file=new File(xmlFileName);
    	org.w3c.dom.Document document = builder.parse(file);
    	
    	
    	//number of doc under each class
    	HashMap<String,Integer> numOfDocEachClass=new HashMap<String,Integer>();
    	for(Map.Entry<String, String> entry : classMap.entrySet()){
    		String className=entry.getValue();
    		if(!numOfDocEachClass.containsKey(className)){
    			numOfDocEachClass.put(className, 0);
    		}
		}
    	
    	//total doc num
    	int totalNumOfDoc=0;
    	
    	//the total statistic model to store word's doc freq for each class 
    	HashMap<String,HashMap<String,Integer>> totalWordList=new HashMap<String,HashMap<String,Integer>>();
    	
    	//begin to process the file
    	for(int i=0;i<document.getElementsByTagName("doc").getLength();i++){
    		String content = "", title = "",  url = "";
    		
    		if(document.getElementsByTagName("url").item(i).getFirstChild()==null || document.getElementsByTagName("url").item(i).getFirstChild().getNodeValue()==null){
    			//is null then continue 
    			continue;
    		}else{
    			url= document.getElementsByTagName("url").item(i).getFirstChild().getNodeValue();
    			if(document.getElementsByTagName("content").item(i).getFirstChild() != null){   			
        			content = document.getElementsByTagName("content").item(i).getFirstChild().getNodeValue();
	        		if(document.getElementsByTagName("contenttitle").item(i).getFirstChild() != null)
	        			title = document.getElementsByTagName("contenttitle").item(i).getFirstChild().getNodeValue();
	    			
	        		totalNumOfDoc++;//a new doc in.
	        		
	        		//process content 
	        		//convert content to word and freq then write it to txt 
	        		// now i will not use title info.
	        		//different class i can use map to store it.
	        		
	        		String[] resultArray =ExcludeStopWords.excludeStopWords(content).split(" ");
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
	        		
	        		// analysis the url and know the category of doc
	        		String prefix,category=null;
	        		for(Map.Entry<String, String> entry : classMap.entrySet()){
	        			prefix=entry.getKey();
	        			if(url.startsWith(prefix)){
	        				category=entry.getValue();
	        				break;
	        			}
	        		}
	        		
	        		//add category num
	        		numOfDocEachClass.put(category,numOfDocEachClass.get(category)+1);
	        		
	        		//write wordListOfDoc to txt 
	        		//change wordListOfDoc into String 
	        		{
	        			String wordListOfDocToString=category+"\n";
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
	        		}
	        		//participate to the total analysis.
	        		for(Map.Entry<String, Integer> entry: wordListOfDoc.entrySet()){
	        			String word;
	        			word=entry.getKey();
	        			if(	totalWordList.containsKey(word)){
	        				HashMap<String,Integer> temp=totalWordList.get(word);
	        				if(temp.containsKey(category)){
	        					temp.put(category, temp.get(category)+1);
	        				}else{
	        					temp.put(category, 1);
	        				}
	        			}else{
	        				HashMap<String,Integer> temp=new HashMap<String,Integer>();
	        				temp.put(category, 1);
	        				totalWordList.put(word, temp);
	        			}//end of if
	        		}//end of for
	        		
    			}
    		}
    	}//end for for doc.length
		
    	
    	
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
				tmpMI=(double) (numOfDocEachClass.get(className)/(double)totalNumOfDoc)
						*(Math.log(wordFreqUnderClass)+Math.log(totalNumOfDoc)-Math.log(wordFreq)-Math.log(numOfDocEachClass.get(className))) ;
				System.out.println(className+numOfDocEachClass.get(className)+" "+"wordFreqUnderClass"+wordFreqUnderClass+" "+"wordFreq"+wordFreq+" "+"totalNumOfDoc"+totalNumOfDoc+"MI"+tmpMI);
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
		String MIFile="svmData\\termMI.txt";
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
	        	 wordmi+=wordEntry.getKey()+" "+wordEntry.getValue().toString()+"\n";
	        	 out.write(wordmi);   //here may try to check whether it is right.
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
	    
    	//write the term freq to txt
	    String termDFFile="svmData\\termDF.txt";
	    try {
	    	File file1=new File(termDFFile);
	    	 if (!file1.exists()) {
   			    file1.createNewFile();
	    	 }
	         out = new BufferedWriter(new OutputStreamWriter(   
	                  new FileOutputStream(file1)));   
	         for(Map.Entry<String, Integer> wordEntry: wordDocFreqInCorpus.entrySet()){
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
	}catch(ParserConfigurationException e){
		e.printStackTrace();
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	/**
	 *             
	 * Read xml and go on word segmentation on each record, then return a list of string  <br/>
	 * Attention : here have not consider the content title.
	 * @return
	 */
	@Deprecated
	public List<String> readXML(){
		
		return null;
		
	}
	
	public static void main(String args[]){
		CalMutualInfoXMLform mi=new CalMutualInfoXMLform();
		mi.readClassMap("RawData//classMap.txt");
		mi.work("RawData//xmlData.xml");
	}
}
