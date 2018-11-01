package cn.edu.whu.irlab.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

/**
 * 打印语汇单元的信息
 * @author user
 *
 */
public class AnalyzerUtils {
    public static String displayToken(String str,Analyzer a) throws IOException {
        TokenStream stream = a.tokenStream("content", new StringReader(str));
        /*
         * TokenStream相当于一条流
         * CharTermAttribute相当于一个碗
         * 然后把碗丢进流里面，当碗得到一个元素后，碗又会自动流到了下
         * 一个元素进行取值
         * 这是一种设计模式：创建一个属性，这个属性会添加流中，
         * 随着这个TokenStream增加
         */
        CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
        ArrayList result=new ArrayList();
        StringBuffer sb=new StringBuffer();
        String s=new String();
        stream.reset();
        try {
            cta.setEmpty();
            while(stream.incrementToken()){
                System.out.print("["+cta+"]");
                result.add(cta);
                result.add(" ");
                sb.append(cta);
                sb.append(" ");
//				System.out.println(stream);
                //如果直接打印Stream的话,toString打印如下：
                //(来,startOffset=1,endOffset=2,positionIncrement=1,type=<IDEOGRAPHIC>)
            }
            s=new String(sb);
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
          //PolicyUtils.IO.close(stream, cta);
        }
        //return result;
        stream.end();
        stream.close();
        return s;

    }
    /**
     * 打印详细信息的语汇单元
     * @param str
     * @param a
     */
    public static void displayAllToken(String str,Analyzer a){
        TokenStream stream = a.tokenStream("content", new StringReader(str));
        //位置增量
        PositionIncrementAttribute pia = stream.addAttribute(PositionIncrementAttribute.class);
        //偏移量
        OffsetAttribute oa = stream.addAttribute(OffsetAttribute.class);
        //词元
        CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
        //分词的类型
        TypeAttribute ta = stream.addAttribute(TypeAttribute.class);
        try {
            while(stream.incrementToken()){
                System.out.print(pia.getPositionIncrement()+":");
                System.out.print(cta+"["+oa.startOffset()+"-"+
                        oa.endOffset()+"-"+ta.type());
                System.out.println();
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
