package junit.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

	public Integer fileReadTemplete(String filepath, BufferedReaderCallBack callBack) throws IOException {
		BufferedReader reader= null; 
		
		try{
		reader = new BufferedReader(new FileReader(filepath));
		int ret = callBack.doSomethingWithReader(reader);
		return ret;
		}catch(IOException e){
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if(reader!=null){
				try{
					reader.close();
				}catch(IOException e){
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public Integer calcSum(String filepath) throws IOException {
		BufferedReaderCallBack callBack = new BufferedReaderCallBack() {
			@Override
			public Integer doSomethingWithReader(BufferedReader reader) throws IOException {
				Integer sum = 0;
				String line = null;
				while((line=reader.readLine())!=null){
					sum += Integer.valueOf(line);
				}
				return sum;
			}
		};
		return fileReadTemplete(filepath, callBack);
	}

	public Integer calcMultiply(String numFilepath) throws IOException{
		LineCallBack callBack = new LineCallBack() {
			
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return value*Integer.valueOf(line);
			}
		};
		return lineReadTemplete(numFilepath, callBack, 1);
	}
	
	public Integer lineReadTemplete(String filepath, LineCallBack callBack, int initVal) throws IOException{
		BufferedReader bufferedReader =null;
		try{
			bufferedReader = new BufferedReader(new FileReader(filepath));
			Integer res = initVal;
			String line = null;
			while ((line=bufferedReader.readLine())!=null) {
				res = callBack.doSomethingWithLine(line, res);
			}
			return res;
		}catch(IOException e){
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if(bufferedReader!=null){
				try{
					bufferedReader.close();
				}catch(IOException e){
					System.out.println(e.getMessage());
				}
			}
		}
}
}

