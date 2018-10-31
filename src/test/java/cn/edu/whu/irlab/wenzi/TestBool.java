package cn.edu.whu.irlab.wenzi;

import cn.edu.whu.irlab.service.boolmodel.BoolRetrivalModel;
import cn.edu.whu.irlab.service.boolmodel.Document;

import java.util.*;

public class TestBool {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Document document = new Document();
		boolean isChinese = true;
		TreeMap<String, ArrayList<Integer>> invertedIndex = document.getInvertedIndex(isChinese);

		BoolRetrivalModel br = new BoolRetrivalModel();
//		String[] queryTerms = {"阿里","阿坝"};
		String[] queryTerms = {"abil","accessori"};
		String[] operators = {"not"};
		ArrayList<Integer> resultsID = br.boolRetrival(queryTerms, operators, invertedIndex);
		HashMap<String,String> title_Contents = document.getTitle_Content(resultsID,isChinese);
		System.out.println(resultsID);

//		System.out.println(document.getDocID_Name(isChinese));
//		System.out.println(document.getName_Contents(isChinese));
//		System.out.println(document.getName_Titles(isChinese));
//		System.out.println(document.getTitle_Content(resultsID,isChinese));
		Iterator<String> iterator = title_Contents.keySet().iterator();
		while (iterator.hasNext()){
			String title = iterator.next();
			String content = title_Contents.get(title);
			System.out.println("title==="+title+"\n"+content);
		}
	}

}
