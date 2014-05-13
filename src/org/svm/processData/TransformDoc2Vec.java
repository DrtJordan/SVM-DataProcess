package org.svm.processData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.ictclas4j.utility.ExcludeStopWords;
import org.svm.utility.Utility;

public class TransformDoc2Vec {
	/**
	 * ���ܣ��õ�feature
	 * @param featureFile
	 * @return
	 */
//	public HashMap<String,Integer> getFeatureList(String featureFile){
//		
//	}
	public void getDocVecFromText(String wordListFile,String newsDir,String docVecFileString,String form){
		HashMap<String,Integer> wordList=new HashMap<String,Integer>();
		int FeatureNum=0;
		
		//read the wordlist into hashmap and make every word map a unique number.
		try{
			String word;
			BufferedReader reader=new BufferedReader(new FileReader(wordListFile));
			while((word=reader.readLine())!=null){
				if (word.length()==0)
					continue;
				wordList.put(word, FeatureNum);
				FeatureNum++;
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		int[] docArray=new int[FeatureNum];
		//read docTerm.txt to get the doc vector
		try{
			BufferedWriter out=null;
	    	File docVecFile=new File(docVecFileString);
	    	 if (!docVecFile.exists()) {
	    		 docVecFile.createNewFile();
	    	 }
	        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docVecFile)));  
			String className;
			
			File directory=new File(newsDir);			
			for(File classFile:directory.listFiles()){
				className=classFile.getName();
			
				//go into the text and analysis it.
				for(File text:classFile.listFiles()){
					BufferedReader reader = null; 
			    	try {
				    	reader = new BufferedReader(new FileReader(text)); 
				    	String textString="",tempString;
				
				    	while ((tempString = reader.readLine()) != null){ 
				    		textString+=tempString;
				    	}
				    	textString=textString.replaceAll("[\t\n\r ��]", "").trim();
				    	
				    	String[] resultArray =ExcludeStopWords.excludeStopWords(textString).split(" ");
						for(int i=0;i<FeatureNum;i++) 		docArray[i]=0;
						
						Integer pos=null;
						for(String term:resultArray){
							if((pos=wordList.get(term)) != null){
								docArray[pos]++;
							}
						}
				
						//write to docVec.txt
						String docVec;
						if(form=="mySvm"){
							docVec=mySvmForm(className, docArray);
						}else{
							docVec=libsvmForm(className, docArray);
						}

						out.write(docVec);
			    	}catch(Exception e){
			    		e.printStackTrace();
			    	}
			    	reader.close();
				}
			}
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * ˼·���õ�topN��mi�ʻ㣬����������ʽ��ʾ���ǣ����뵱ǰ���ĵ��Ĵ����ʾ��
	 * ���N���ʶ�Ӧ�ķ���������TF���У�������˳��������������������һ���ļ��С�
	 * ��ʽ��Ϊ��mysvm���͡�libsvm������
	 * mysvm���£�C10001  3 4 5 2 3 12 2 ǰ��Ϊ��𣬺�������˳��Ĵ�Ƶ��
	 * libsvm���£�C10001 3��4 4��4 5��2
	 */
	public void getDocVecFromDocTerm(String wordListFile,String docTermFile,String docVecFileString,String form){

		HashMap<String,Integer> wordList=new HashMap<String,Integer>();
		int FeatureNum=0;
		//read the wordlist into hashmap and make every word map a unique number.
		try{
			String word;
			
			BufferedReader reader=new BufferedReader(new FileReader(wordListFile));
			while((word=reader.readLine())!=null){
				wordList.put(word, FeatureNum);
				FeatureNum++;
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		//read docTerm.txt to get the doc vector
		try{
			BufferedWriter out=null;
	    	File docVecFile=new File(docVecFileString);
	    	 if (!docVecFile.exists()) {
	    		 docVecFile.createNewFile();
	    	 }
	        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docVecFile)));  
			String className;
			BufferedReader reader=new BufferedReader(new FileReader(docTermFile));
			while((className=reader.readLine())!=null){
				int[] docArray=new int[FeatureNum];
				for(int i=0;i<FeatureNum;i++)
					docArray[i]=0;
				
				String docTerm=reader.readLine();
				String[] docTermList=docTerm.split(" ");
				Integer pos=null;
				for(String termFreq:docTermList){
					String[] termAttr=termFreq.split(":");
					if((pos=wordList.get(termAttr[0])) != null){
						docArray[pos]=Integer.valueOf(termAttr[1]);
					}
				}
				
				//write to docVec.txt
				String docVec;
				if(form=="mySvm"){
					docVec=mySvmForm(className, docArray);
				}else{
					docVec=libsvmForm(className, docArray);
				}
				out.write(docVec);
			}
			out.close();
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private String mySvmForm(String className,int[] docArray){
		String docVec=className+"\t";
		for(Integer x:docArray){
			docVec+=x.toString()+"\t";
		}
		docVec+="\n";
		return docVec;
	}
	private String libsvmForm(String className,int[] docArray){
		String docVec=className+" ";
		for(int i=0;i<docArray.length;i++){
			if(docArray[i]!=0){
				docVec+=String.valueOf(i+1)+":"+String.valueOf(docArray[i])+" ";
			}
		}
		docVec+="\n";
		return docVec;
	}
	public void writeToFile(String file){
		try{
			BufferedWriter out=null;
	    	File f=new File(file);
	    	 if (!f.exists()) {
	    		 f.createNewFile();
	    	 }
	        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));  
            String classString="";
            for(int i=0;i<100;i++){
            	classString+="C000008\n";
            }
            for(int i=0;i<100;i++){
            	classString+="C000010\n";
            }
			out.write(classString);
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		TransformDoc2Vec doc2vec=new TransformDoc2Vec();
		doc2vec.getDocVecFromDocTerm("svmData\\2class exp\\chiTopN.txt", "svmData\\2class exp\\test\\DocTerm.txt", "svmData\\2class exp\\test\\docVec chi libsvm.txt","libsvm");
//		doc2vec.getDocVecFromDocTerm("svmData\\2class exp\\igTopN.txt", "svmData\\2class exp\\docTerm.txt", "svmData\\2class exp\\Training docVec ig.txt");//D:\ѧϰ\��ѧ\natural language  processing\2014�����-��SVM�ı�����\data\sougou labs\SogouC.mini.20061127\SogouC.mini\Sample\C000007

//		doc2vec.getDocVecFromText("svmData\\2class exp\\chiTopN.txt", "D:\\ѧϰ\\��ѧ\\natural language  processing\\2014�����-��SVM�ı�����\\data\\sougou labs\\2class exp\\Reduced testing", "svmData\\2class exp\\Test docVec chi.txt");
//		doc2vec.getDocVecFromText("svmData\\2class exp\\igTopN.txt", "D:\\ѧϰ\\��ѧ\\natural language  processing\\2014�����-��SVM�ı�����\\data\\sougou labs\\2class exp\\Reduced testing", "svmData\\2class exp\\Test docVec ig.txt","libsvm");

		//		doc2vec.writeToFile("svmData\\Train docVec Y.txt");
	}
}
