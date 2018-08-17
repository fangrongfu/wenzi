package cn.edu.whu.irlab.service.boolmodel;

import org.springframework.stereotype.Service;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

/**
 * 按字典顺序排序
 * @author Administrator
 *
 */
@Service
public class MyComparator implements Comparator {

	//Collator 类执行区分语言环境的 String 比较。使用此类可为自然语言文本构建搜索和排序例程。
		private Collator collator=Collator.getInstance();
		
		
		
		/*o1和o2是含中文的字符串(map中的key),
		 * 现在想让o1和o2按中文拼音的先后顺序排序，则根据先后关系分别返回:
		 * 负整数、0或正整数
		 */
		@Override
		public int compare(Object o1, Object o2) {
			CollationKey key1=collator.getCollationKey(o1.toString());
			CollationKey key2=collator.getCollationKey(o2.toString());
			
			return key1.compareTo(key2);
		}


}
