package cn.edu.whu.irlab.service.boolmodel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BoolRetrivalModel {
	
	/**
	 * 根据布尔检索表达式获取满足的文档ID （注：该布尔检索式为简单的表达式，不含括号等改变运算优先级的符号）
	 * @param queryTerms 按序存储的检索词
	 * @param operators 按序存储的逻辑表达式 
	 * @return ArrayList<Integer> results
	 */
	public ArrayList<Integer> boolRetrival(String[] queryTerms, String[] operators,TreeMap<String, ArrayList<Integer>> invertedIndex) {
		//将检索词全部转化为小写
		for (int i = 0; i < queryTerms.length; i++) {
			queryTerms[i] = Tokenize.tokenize(queryTerms[i]);
		}
		ArrayList<Integer> results = new ArrayList<Integer>();
		if(null==operators) { //如果只有一个检索词 (前端传递的operators可能为null)
			if (invertedIndex.containsKey(queryTerms[0])) {
				results.addAll(invertedIndex.get(queryTerms[0]));
				return results;
			}else {
				return null;
			}
			
		}else {
			if (invertedIndex.containsKey(queryTerms[0])) {//倒排索引含有第一个词
				results.addAll(invertedIndex.get(queryTerms[0])); 
				for (int i = 0; i < operators.length; i++) {
					if (invertedIndex.containsKey(queryTerms[i + 1])) {// 如果倒排索引含有下一个词
						if ("and".equals(operators[i])) {
							results.retainAll(invertedIndex.get(queryTerms[i + 1]));// 与（交集）：去除含有第一个词但不含有第二个词的文档ID
						} else if ("or" .equals(operators[i])) {
							results.addAll(invertedIndex.get(queryTerms[i + 1]));// 或（并集）：加入含有第二个词的所有文档ID
							results = removeDuplicate(results); //去除重复值
						} else {
							results.removeAll(invertedIndex.get(queryTerms[i + 1]));// 非：去除含有第二个词的所有文档ID
						}
					} else { // 如果倒排索引中不含有下一个词
						if("and".equals(operators[i])) { //第一个逻辑运算符为and 那么没有满足的文档，返回null
							return null;
						} 								//第一个逻辑运算符为 or 或not,可以接着查找，results不变为前一步的计算结果
					}
				}
				
			}else {//倒排索引不含第一个词
				if("and".equals(operators[0]) || "not".equals(operators[0])){
					return null;  //第一个逻辑运算符为 and 或not，则没有，满足的文档，返回null
				}else {  //第一个逻辑运算符为or
					results.addAll(invertedIndex.get(queryTerms[1])); //以第二个词作为第一个词进行逻辑运算
					for (int i = 1; i < operators.length; i++) { //逻辑运算符也从第二个开始遍历，即i=1
						if (invertedIndex.containsKey(queryTerms[i + 1])) {// 如果倒排索引含有下一个词
							if ("and".equals(operators[i])) {
								results.retainAll(invertedIndex.get(queryTerms[i + 1]));// 与（交集）：去除含有第一个词但不含有第二个词的文档ID
							} else if ("or" .equals(operators[i])) {
								results.addAll(invertedIndex.get(queryTerms[i + 1]));// 或（并集）：加入含有第二个词的所有文档ID
								results = removeDuplicate(results);//去除重复值
							} else {
								results.removeAll(invertedIndex.get(queryTerms[i + 1]));// 非：去除含有第二个词的所有文档ID
							}
						} else { // 如果倒排索引中不含有下一个词
							if("and".equals(operators[i])) { //第一个逻辑运算符为and 那么没有满足的文档，返回null
								return null;
							} 								//第一个逻辑运算符为 or 或not,可以接着查找，results不变为前一步的计算结果
						}
					}
				}
			}

		}
		return results;

	}
	/**
	 * 去除ArrayList<Integer> list里面重复的值
	 * @param list 
	 * @return
	 */
	private ArrayList<Integer> removeDuplicate ( ArrayList<Integer> list ) {
        ArrayList<Integer> unique = new ArrayList<Integer>();
        for ( int i=0; i<list.size(); i++ ) {
            if ( !unique.contains(list.get(i)) ) unique.add(list.get(i));
        }
        
        return unique;
    }
	//放到工具类里面，将map<integer,integer>转换成map<string,integer>
	private ArrayList<String> intArrayListtoStr ( ArrayList<Integer> list ) {
        ArrayList<String> unique = new ArrayList<String>();
        for ( int i=0; i<list.size(); i++ ) {
          unique.add(list.get(i).toString());
        }
        
        return unique;
    }
	
}
