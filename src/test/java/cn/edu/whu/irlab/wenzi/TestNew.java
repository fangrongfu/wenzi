package cn.edu.whu.irlab.wenzi;

import cn.edu.whu.irlab.service.boolmodel.BoolRetrivalModel;
import cn.edu.whu.irlab.service.boolmodel.Document;

import java.util.ArrayList;
import java.util.TreeMap;

public class TestNew {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Document document = new Document();
		TreeMap<String, ArrayList<Integer>> invertedIndex = document.getInvertedIndex();
		BoolRetrivalModel br = new BoolRetrivalModel();
		String[] queryTerms = {"阿里","阿坝"};
		String[] operators = {"not"};
		System.out.println(br.boolRetrival(queryTerms, operators, invertedIndex));
	}

}
