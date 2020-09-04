package test;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import javax.xml.crypto.dom.DOMCryptoContext;
import java.io.File;



public class Test1 {
    String[] a = {
            "3, 华为 - 华为电脑, 爆款",
            "4, 华为手机, 旗舰",
            "5, 联想 - Thinkpad, 商务本",
            "6, 联想手机, 自拍神器"
    };

    @Test
    public void test1() throws Exception {
        //文件夹
        FSDirectory d = FSDirectory.open(new File("d:/abc/").toPath());

        //配置工具,配置中文分词器
        IndexWriterConfig conf = new IndexWriterConfig(new SmartChineseAnalyzer());
        //索引输出工具
        IndexWriter writer = new IndexWriter(d,conf);
        //循环处理四篇文档,输出索引
        for (String s:a){
            //id 名称 ，卖点
            //0    1     2
            String [] arr = s.split("\\s*,\\s*");

            Document doc = new Document();
            doc.add(new LongPoint("id",Long.parseLong(arr[0])));
            doc.add(new StoredField("id",Long.parseLong(arr[0])));
            doc.add(new TextField("title",arr[1], Field.Store.YES));
            doc.add(new TextField("sellPoint",arr[2], Field.Store.YES));

            writer.addDocument(doc);
        }
        writer.flush();
        writer.close();
    }
    @Test
    public void test2()throws Exception{
        //文件夹
        FSDirectory d = FSDirectory.open(new File("d:/abc/").toPath());
        //索引读取工具
        IndexReader reader = DirectoryReader.open(d);
        //搜索工具
        IndexSearcher searcher = new IndexSearcher(reader);
        // 创建对象封装搜索参数
        TermQuery q = new TermQuery(new Term("title","华为"));
        //查询 , 查询结果[{id:1,score:0.33},{id:5,score:0.29}]
        TopDocs topDocs =  searcher.search(q,20);//每页多少条
        //遍历显示文档
        for (ScoreDoc sd:topDocs.scoreDocs){
            int id = sd.doc;
            float score = sd.score;
            Document doc = searcher.doc(id);
            System.out.println(doc.get("id")+" - "+score);
            System.out.println(doc.get("title"));
            System.out.println(doc.get("sellPoint"));
            System.out.println("------------------------------");
        }
        reader.close();
    }


}
