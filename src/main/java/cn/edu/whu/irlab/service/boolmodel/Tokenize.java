package cn.edu.whu.irlab.service.boolmodel;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;

@Service
public class Tokenize {
	/**
	 * 将词条全部转化为小写
	 * @param str
	 * @return
	 */
	 public static String tokenize ( String str ) {
	        str = str.toLowerCase();
	        return str;
	    }
	/**
	 *   获取停用词集合
	 * @return HashSet<String> stopWords
	 */
	public static HashSet<String> getStopWords(){
		HashSet<String> stopWords = new HashSet<String>();
		//File stopFile = new File("stopwords.txt");
        //修改一下
        File stopFile = null;
        try {
            stopFile = ResourceUtils.getFile("classpath:stopwords.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String stopWord = null;
		try {
			FileInputStream fin = new FileInputStream(stopFile);
			InputStreamReader ir = new InputStreamReader(fin,"gb2312");
			BufferedReader br = new BufferedReader(ir);
			while(null!=(stopWord=br.readLine())) {
				stopWord = stopWord.trim();
				stopWords.add(stopWord);
			}
			fin.close();
			ir.close();
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stopWords;
		
	}
	
	public static void main(String[] args) {
		Iterator<String> it = getStopWords().iterator();
		System.out.println(getStopWords().size());
		while (it.hasNext()) {
			System.out.println(it.next());
			
		}
	}
}
