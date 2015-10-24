package cn.itcast.lucene.utils;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKTokenizer;

import cn.itcast.bean.Article;
import cn.itcast.bean.ResultBean;
import cn.itcast.lucene.service.LuceneService;
import cn.itcast.lucene.service.LuceneServiceImpl;

/**
 * 测试类
 * 
 * @author Liubao
 * @2015年7月26日
 * 
 */
public class LuceneJunit {

    LuceneService luceneService = new LuceneServiceImpl();

    @Test
    public void testAddIndex() {
        rss("http://news.qq.com/newsgn/rss_newsgn.xml");
    }

    public String rss(String rssXML) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String urlName = rssXML;
            URL U = new URL(urlName);
            URLConnection connection = U.openConnection();
            connection.connect();
            SAXReader reader = new SAXReader();
            Document doc = reader.read(connection.getInputStream());
            Element root = doc.getRootElement();
            Element foo;
            Element subfoo;
            int ios = 1;
            for (Iterator<Element> i = root.elementIterator("channel"); i
                    .hasNext();) {
                foo = i.next();
                for (Iterator<Element> j = foo.elementIterator("item"); j
                        .hasNext();) {
                    System.out.println("--------------------------------------------------------------");
                    subfoo = j.next();
                    String link = subfoo.elementTextTrim("link");
                    String title = subfoo.elementTextTrim("title").trim();
                    String content = subfoo.elementTextTrim("description");
                    String author = subfoo.elementTextTrim("author");
                    System.out.println("author===" + author);
                    System.out.println("link===" + link);
                    System.out.println("description===" + content);
                    org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
                    document.add(new StringField("Id", String.valueOf(ios++),Store.YES));
                    document.add(new TextField("title", title, Store.YES));
                    document.add(new TextField("content", content, Store.YES));
                    document.add(new StringField("author", author, Store.YES));
                    document.add(new StringField("link", link, Store.YES));
                    document.add(new StringField("date",sdf.format(new Date()), Store.YES));
                    // LuceneDao.addIndex(document);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testQuery() throws Exception {
        ResultBean<Article> rs = luceneService.getResultBeanList("www.qq.com", 0, 100);
        System.out.println(rs.getResult().size());
        for (Article article : rs.getResult()) {
            System.out.println("Id===" + article.getId());
            System.out.println(article.getConent());
        }
    }

    @Test
    public void testDelete() throws IOException {
        luceneService.deleteIndexById("7");
    }

    @Test
    public void updateIndex() throws IOException {
        org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
        document.add(new StringField("Id", "8", Store.YES));
        document.add(new TextField("content", "测试数据ﵽ", Store.YES));
        luceneService.updateIndexById(document);
    }

    @Test
    public void optimise() {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                Version.LUCENE_44, LuceneUtil.getAnalyzer());
        LogMergePolicy logMergePolicy = new LogByteSizeMergePolicy();
        logMergePolicy.setMergeFactor(3);
        logMergePolicy.setMaxMergeDocs(1000);
        indexWriterConfig.setMergePolicy(logMergePolicy);
    }

    @Test
    public void testRMDirectory() throws IOException {
        Directory directory = new RAMDirectory();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                Version.LUCENE_44, LuceneUtil.getAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
        document.add(new StringField("Id", "8", Store.YES));
        document.add(new TextField("content", "测试数据",Store.YES));
        indexWriter.addDocument(document);
        indexWriter.close();
    }

    @Test
    public void testRedirectory() throws IOException {
        // Directory directory=FSDirectory.open(new File("/indexDir/"));
        // IOContext context=new IOContext();
        // Directory ramDir = new RAMDirectory(directory,context);
        // IndexReader indexReader=DirectoryReader.open(ramDir);
        // IndexSearcher indexSearcher=new IndexSearcher(indexReader);
    }

    @Test
    public void testAnalyer() throws IOException {
        String keywords = "www.qq.com";
//         Analyzer analyzer=new StandardAnalyzer(Version.LUCENE_44);
        // Analyzer analyzer=new ChineseAnalyzer();
//        Analyzer analyzer = new CJKAnalyzer(Version.LUCENE_44);
//        doCKJAnalyzer(analyzer, keywords);
        
        Analyzer analyzer = new IKAnalyzer();
         doAnalyzer(analyzer, keywords);
    }
    
    public void doCKJAnalyzer(Analyzer analyzer, String keyWord) throws IOException {
        System.out.println("当前分词器是:" + analyzer.getClass().getSimpleName());
        TokenStream tokenStream = analyzer.tokenStream("content", keyWord);
        while (tokenStream.incrementToken()) {
            CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
            System.out.println(new String(charTermAttribute.toString()));
        }
    }

    public void doAnalyzer(Analyzer analyzer, String keyWord) throws IOException {
        System.out.println("当前分词器是:" + analyzer.getClass().getSimpleName());
        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(keyWord));
        tokenStream.addAttribute(CharTermAttribute.class);
        while (tokenStream.incrementToken()) {
            CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
            System.out.println(new String(charTermAttribute.toString()));
        }
    }

}
