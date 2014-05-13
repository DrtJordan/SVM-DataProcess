package org.file.utility;

import java.io.File;

public class createFile {
	public static void recursiveCreateFile(String path){
		File file1=new File(path);
		if(!file1.exists()){
			if(!file1.getParentFile().exists()){}
				
		}
	}
}
