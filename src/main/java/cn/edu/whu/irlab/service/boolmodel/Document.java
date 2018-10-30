package cn.edu.whu.irlab.service.boolmodel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.util.ResourceUtils;

public class Document {
	private HashMap<Integer, String> docID_Name; //文档ID和文档名
	private HashMap<String, String> Name_Title; //文档名和文档title
	private HashMap<String, String> Name_Contents; //文档名和文档原始内容
	



	public Document() {
		// TODO Auto-generated constructor stub
		 this.docID_Name = new HashMap<Integer, String>();
		 this.Name_Title = new HashMap<String, String>();
		 this.Name_Contents = new HashMap<String, String>();
	}
	/**
	 * 获取倒排索引 
	 * @return
	 */
	public TreeMap<String, ArrayList<Integer>> getInvertedIndex(boolean isChinese) {
		TreeMap<String, ArrayList<Integer>> invertedIndex = new TreeMap<String, ArrayList<Integer>>(); // 倒排索引
		String dataDir = null;
        try {
        	if (isChinese){
				dataDir = ResourceUtils.getFile("classpath:index//term_id1.txt").getPath();
			}else {
				dataDir = ResourceUtils.getFile("classpath:index//term_id1_eng.txt").getPath();
			}

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

		File inverFile = new File(dataDir);

		try {
			FileInputStream fin = new FileInputStream(inverFile);
			InputStreamReader ir = new InputStreamReader(fin);
			BufferedReader br = new BufferedReader(ir);
			try {
				String input = br.readLine();
				JSONObject jsonObj = JSONObject.fromObject(input);
				Iterator<String> terms = jsonObj.keys();
				while(terms.hasNext()) {
					String term = terms.next();
					JSONArray jsonPostings = jsonObj.getJSONArray(term);
					ArrayList<Integer> postings = new ArrayList<Integer>();
//					ArrayList<Integer> postings =(ArrayList<Integer>) JSONArray.toArray(jsonObj.getJSONArray(term));
//					ArrayList<Integer> postings = (ArrayList<Integer>)jsonObj.get(term);
					for (int i = 0; i < jsonPostings.size(); i++) {
						postings.add(jsonPostings.getInt(i));
						
					}
					invertedIndex.put(term, postings);
				}
				fin.close();
				ir.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(invertedIndex);
		return invertedIndex;
	}
	/**
	 * 获取key值为文档ID，value值为文档name的Map 
	 * @return
	 */
	public HashMap<Integer, String> getDocID_Name(boolean isChinese) {
		String dataDir = null;
		try {
			if (isChinese){
				dataDir = ResourceUtils.getFile("classpath:index//docID_Name.txt").getPath();
			}else {
				dataDir = ResourceUtils.getFile("classpath:index//docID_Name_eng.txt").getPath();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		File file = new File(dataDir);
		try {
			FileInputStream fin = new FileInputStream(file);
			InputStreamReader ir = new InputStreamReader(fin);
			BufferedReader br = new BufferedReader(ir);
			try {
				String input = br.readLine();
				JSONObject jsonObj = JSONObject.fromObject(input);
				Iterator<String> docIDs = jsonObj.keys();
				while(docIDs.hasNext()) {
					String docID = docIDs.next();
					String docName = (String) jsonObj.get(docID);
					docName = docName.substring(0,docName.indexOf('.'));
					docID_Name.put(Integer.valueOf(docID), docName);
				}
				fin.close();
				ir.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return docID_Name;
	}
	/**
	 * 获取key值为文档name，value值为文档title的Map 
	 * @return
	 */
	public HashMap<String, String> getName_Titles(boolean isChinese) {
		String dataDir = null;
		try {
			if (isChinese){
				dataDir = ResourceUtils.getFile("classpath:index//doc_list_new.json").getPath();
			}
			else {
				dataDir = ResourceUtils.getFile("classpath:index//doc_list_new_eng.json").getPath();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		File file = new File(dataDir);
		try {
			FileInputStream fin = new FileInputStream(file);
			InputStreamReader ir = new InputStreamReader(fin);
			BufferedReader br = new BufferedReader(ir);
			try {
				String input = br.readLine();
				JSONObject jsonObj = JSONObject.fromObject(input);
				Iterator<String> QNOs = jsonObj.keys();
				while(QNOs.hasNext()) {
					String QNO = QNOs.next();
					JSONObject q_doc = (JSONObject)jsonObj.get(QNO);
					JSONArray doc_list = q_doc.getJSONArray("doc_list");
					for (int i = 0; i < doc_list.size(); i++) {
						JSONObject docInfo = doc_list.getJSONObject(i);
						String docName = docInfo.getString("doc_id");
						String docTitle = docInfo.getString("title");
						Name_Title.put(docName, docTitle);
					}
				}
				fin.close();
				ir.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Name_Title;
	}
	/**
	 * 获取key值为文档name，value值为文档content的Map 
	 * @return
	 */
	public HashMap<String, String> getName_Contents(boolean isChinese) {
		String dataDir = null;
		try {
			if (isChinese){
				dataDir = ResourceUtils.getFile("classpath:doc_中文").getPath();
			}
			else {
				dataDir = ResourceUtils.getFile("classpath:doc_英文").getPath();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		File f = new File(dataDir);
		File[] docs = f.listFiles();
		for (File doc : docs) {
			String docName = doc.getName();
			if (docName.equals(".DS_Store"))
                continue;
			try {
				FileInputStream fin = new FileInputStream(doc);
				InputStreamReader ir = new InputStreamReader(fin);
				BufferedReader br = new BufferedReader(ir);
				String input = "";
				docName = docName.substring(0,docName.indexOf('.'));
				StringBuffer content = new StringBuffer();
				while((input=br.readLine())!=null) {
					content.append(input);
				}
				Name_Contents.put(docName, content.toString());
				fin.close();
				ir.close();
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return Name_Contents;
	}
/**
 * 获取key值为文档title，value值为文档content的Map 	
 * @param docIDs 文档ID集合
 * @return
 */
public HashMap<String, String> getTitle_Content(ArrayList<Integer> docIDs,boolean isChinese){
	HashMap<String, String>Title_Content = new HashMap<String,String>();
	this.getDocID_Name(isChinese);
	this.getName_Titles(isChinese);
	this.getName_Contents(isChinese);
	for (int i = 0; i < docIDs.size(); i++) {
		int docID = docIDs.get(i);
		String docName = this.docID_Name.get(docID);
		String tilte = this.Name_Title.get(docName);
		String content = this.Name_Contents.get(docName);
		Title_Content.put(tilte, content);
	}
	return Title_Content;
}

/**
 * 获取某文件夹中的全部文档名
 * @param docDir 文档文件夹路径
 * @return
 */
	public ArrayList<String> getFileNames(String docDir) {
		ArrayList<String> fileNames = new ArrayList<String>();
		File f = new File(docDir);
		File[] docs = f.listFiles();
		String docName = null;
		for (File doc : docs) {
			docName = doc.getName();
			fileNames.add(docName);
		}
		return fileNames;
		
	}
	
}
