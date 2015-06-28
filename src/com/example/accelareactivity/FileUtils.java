package com.example.accelareactivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


import android.os.Environment;
import android.widget.Toast;

public class FileUtils {
	
	 File file = null;
	 OutputStreamWriter osw = null ;
	 OutputStream os = null;
	 public static String[] accFileNames = {"acc1.txt","acc2.txt","acc3.txt","acc4.txt","acc5.txt",
		 "acc6.txt","acc7.txt","acc8.txt","acc9.txt","acc10.txt","acc11.txt","acc12.txt","acc13.txt",
		 "acc14.txt","acc15.txt","acc16.txt","acc17.txt","acc18.txt","acc19.txt","acc20.txt"};
	 int fileNum = 0; 
	 final int fileMaxNum = 20;
	 private boolean fileflag = false;   
	private String SDCardRoot;
    private boolean recordExist = false;

	public String getSDPath(){
		return SDCardRoot;
	}
	public FileUtils() {
		super();
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		if (sdCardExist) {
			SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		} else {
			Toast.makeText(ContextUtil.getInstance(), "SD卡不存在", Toast.LENGTH_SHORT).show();
		}
		System.out.println("---------xxxxx---->"+ SDCardRoot);
	}
	public File createFileInSdCard(String fileName,String dir)throws IOException{
		File file = new File(SDCardRoot + dir + File.separator + fileName);
		if(! file.exists()) {  
            makeDir(file.getParentFile());  
        }  
//        return file.createNewFile();  
		file.createNewFile();
		return file;
	}
	public static void makeDir(File dir) {  
        if(! dir.getParentFile().exists()) {  
            makeDir(dir.getParentFile());  
        }  
        dir.mkdir();  
    }  
	public File createSDDir(String dirName){
		File dirFile = new File(SDCardRoot + dirName + File.separator);
		dirFile.mkdirs();
		return dirFile;
	}
	public boolean isFileExist(String fileName){
		File file = new File(SDCardRoot + fileName);
		return file.exists();
	}
	public File writeToSDCardFromInput(String path,String fileName,InputStream input){
		File file = null;
		OutputStream output = null;
		try{
			createSDDir(path);
			file = createFileInSdCard(fileName, path);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[1024*1];//一个文件最大为1M
			int temp;
			while((temp = input.read(buffer)) != -1){
				output.write(buffer, 0, temp);
			}
			output.flush();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				output.close();
			}catch (Exception e ){
				e.printStackTrace();
			}
		}
		return file;
	}
	public void writeFileSdcardFile(String fileName,String write_str) throws IOException{ 
		 try{ 

		       FileOutputStream fout = new FileOutputStream(fileName); 
		       byte [] bytes = write_str.getBytes(); 

		       fout.write(bytes); 
		       fout.close(); 
		     }

		      catch(Exception e){ 
		        e.printStackTrace(); 
		       } 
		   } 
	
	public boolean createLog(String fileName){
    	try{
    		if(isFileExist("/Sensor/"+fileName))
    			file = new File(getSDPath() + "/Sensor/"+fileName);
    		else
    			file = createFileInSdCard(fileName, "Sensor");
//    		System.out.println(file.getName()+ "---------------"+file.getPath()+ "------file");
    		try{
    			os = new FileOutputStream(file,true);
    			osw = new OutputStreamWriter(os,"UTF-8");
    		}catch(FileNotFoundException e){
    			e.printStackTrace();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return true;
    }
    public boolean createLog2(){
    	try{
    		for(int i=0;i<fileMaxNum;i++){
	    		if(isFileExist("/Sensor/"+accFileNames[i])) {
	    			file = new File(getSDPath()+"/Sensor/"+accFileNames[i]);
	    			if(file.length() < 1024){//文件内容小于1M
	    				fileflag = true;
	    				fileNum = i;
	    				break;
	    			}
	    		}
    		}
    		if(!fileflag)
    			file =  new File(getSDPath()+"/Sensor/"+accFileNames[0]);
    		else
    			file = new File(getSDPath()+"/Sensor/"+accFileNames[fileNum]);

    		try{
    			os = new FileOutputStream(file,fileflag);
    			osw = new OutputStreamWriter(os,"UTF-8");
    			fileflag = false;
    		}catch(FileNotFoundException e){
    			e.printStackTrace();
    		}
    		File numFile = new File(getSDPath()+"/Sensor/fileNum.txt");
			 try{
	    			OutputStream osNum = new FileOutputStream(numFile);
	    			OutputStreamWriter oswNum = new OutputStreamWriter(osNum,"UTF-8");
	    			oswNum.write(fileNum+"");
	    			oswNum.flush();
	    			oswNum.close();
	    			osNum.close();
	    		}catch(FileNotFoundException e){
	    			e.printStackTrace();
	    		}
    				
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return true;
    }
	
    boolean write(float fx,float fy,float fz){
    	try{
			 osw.write("x = "+ fx +"  y = " + fy + "  z = "+ fz +"\r\n");
			 osw.flush();
			 if(file.length()>=1024){
				 osw.close();
				 os.close();
				 fileNum ++;
				 if(fileNum>=fileMaxNum)
					 fileNum = 0;
				 file = new File(getSDPath()+"/Sensor/"+FileUtils.accFileNames[fileNum]);
				 try{
					 os = new FileOutputStream(file);
					 osw = new OutputStreamWriter(os,"UTF-8");
		    		}catch(FileNotFoundException e){
		    			e.printStackTrace();
		    		}
					File numFile = new File(getSDPath()+"/Sensor/fileNum.txt");
					 try{
			    			OutputStream osNum = new FileOutputStream(numFile);
			    			OutputStreamWriter oswNum = new OutputStreamWriter(osNum,"UTF-8");
			    			oswNum.write(fileNum+"");
			    			oswNum.flush();
			    			oswNum.close();
			    			osNum.close();
			    		}catch(FileNotFoundException e){
			    			e.printStackTrace();
			    		}
			 }
				 
		 }catch(Exception e){
			 e.printStackTrace();
		 }
    	return true;
    }


  
  ArrayList<String> readFile(String fileName){
	  ArrayList<String> strs = new ArrayList<String>();
	  try{
			if(isFileExist(fileName)){
				file = new File(getSDPath() + fileName);
				recordExist = true;
			}else {
				Toast.makeText(ContextUtil.getInstance(), "记录文件不存在", Toast.LENGTH_LONG).show();
				recordExist = false;
				return null;
			}
			if(file.length() == 0){
				Toast.makeText(ContextUtil.getInstance(), "文件内容为空", Toast.LENGTH_LONG).show();
				recordExist = false;
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(recordExist == true){
			  try{
					@SuppressWarnings("resource")
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line = "";
					while((line = br.readLine())!=null){
						strs.add(line);
					}
					br.close();
					}catch(Exception e){
						e.printStackTrace();
					}
		}
	  return strs;
  }
  ArrayList<String> readECGRecord(){
	  ArrayList<String> strs = new ArrayList<String>();
	  try{
  		if(isFileExist("/Sensor/fileNum.txt")){
  			file = new File(getSDPath() + "/Sensor/fileNum.txt");
//  			recordExist = true;
  		}else {
  			Toast.makeText(ContextUtil.getInstance(), "创建文件失败", Toast.LENGTH_LONG).show();
  			recordExist = false;
  			return null;
  		}
  		if(file.length() == 0){
  			Toast.makeText(ContextUtil.getInstance(), "文件内容为空", Toast.LENGTH_LONG).show();
  			recordExist = false;
  			return null;
  		}
  		BufferedReader br = new BufferedReader(new FileReader(file));
  		String num = br.readLine();
  		br.close();
  		int fileNum = Integer.parseInt(num);
  		if(isFileExist("/Sensor/"+accFileNames[fileNum])){
      		file = new File(getSDPath()+"/Sensor/"+accFileNames[fileNum]);
  			recordExist = true;
  		}else {
  			Toast.makeText(ContextUtil.getInstance(), "记录文件不存在", Toast.LENGTH_LONG).show();
  			recordExist = false;
  			return null;
  		}
  		if(file.length() == 0){
  			Toast.makeText(ContextUtil.getInstance(), "文件内容为空", Toast.LENGTH_LONG).show();
  			recordExist = false;
  			return null;
  		}

  	}catch(Exception e){
  		e.printStackTrace();
  	}
	  if(recordExist == true){
		  try{
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = "";
				while((line = br.readLine())!=null){
					strs.add(line);
				}
				br.close();
				}catch(Exception e){
					e.printStackTrace();
				}
	}
  return strs;
  }
}

