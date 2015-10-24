package cn.itcast.lucene.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Repository;

import cn.itcast.bean.Article;
import cn.itcast.bean.ResultBean;
import cn.itcast.lucene.utils.DocumentUtils;
import cn.itcast.lucene.utils.LuceneUtil;

/**
 * Lucene操作dao层实现
 * 
 * @author Liubao
 * @2015年7月26日
 * 
 */
@Repository
public class LuceneDaoImpl implements LuceneDao {

    /**
     * 添加索引
     */
    @Override
    public void addIndex(Document document) throws IOException{
        IndexWriter indexWriter = LuceneUtil.getIndexWriter();
        try {
            indexWriter.addDocument(document);
            indexWriter.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            LuceneUtil.closeIndexWriter(indexWriter);
        }
    }
    
    /**
     * 添加索引
     */
    @Override
    public void addIndex(List<Document> documentList) throws IOException {
        IndexWriter indexWriter = LuceneUtil.getIndexWriter();
        try {
            indexWriter.addDocuments(documentList);
            indexWriter.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.closeIndexWriter(indexWriter);
        }
    }

    /**
     * 根据文件id,更新索引
     */
    @Override
    public void updateIndexById(Document document) throws IOException {
        IndexWriter indexWriter = LuceneUtil.getIndexWriter();
        Term term = new Term("Id", document.get("Id"));
        try {
            indexWriter.updateDocument(term, document);
            indexWriter.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            LuceneUtil.closeIndexWriter(indexWriter);
        }
    }

    /**
     * 根据索引id,删除索引
     */
    @Override
    public void deleteIndexById(String Id) throws IOException{
        IndexWriter indexWriter = LuceneUtil.getIndexWriter();
        Term term = new Term("Id", Id);
        try {
            indexWriter.deleteDocuments(term);
            indexWriter.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            LuceneUtil.closeIndexWriter(indexWriter);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findDocumentById(String Id,Class<T> classType) throws Exception {
        T instance = classType.newInstance();
        Object result =null;
        if(instance instanceof Document){
            result = findArticleById(Id);
        }else if(instance instanceof Article){
            result = findDocumentById(Id);
        }
        return (T) result;
    }
    
    /**
     * 根据id查找索引
     */
    private Article findArticleById(String Id) throws IOException {
        IndexSearcher indexSearcher = LuceneUtil.getIndexSearcher();
        Query query = new TermQuery(new Term("Id", Id));
        TopDocs docs = indexSearcher.search(query, 1);
        Article article = null;
        try {
            ScoreDoc[] scoreDocs = docs.scoreDocs;
            for (int i = 0; i < scoreDocs.length; i++) {
                ScoreDoc scoreDoc = scoreDocs[i];
                //存储信息很少,一般只有id有用,这里只取了1条记录
                int id = scoreDoc.doc;
                Document document = indexSearcher.doc(id);
                article = DocumentUtils.documentToArticle(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            LuceneUtil.closeIndexReader(indexSearcher);
        }
        return article;
    }
    
    /**
     * 根据id查找索引
     */
    private Document findDocumentById(String Id) throws IOException {
        IndexSearcher indexSearcher = LuceneUtil.getIndexSearcher();
        Query query = new TermQuery(new Term("Id", Id));
        TopDocs docs = indexSearcher.search(query, 1);
        Document document = null;
        try {
            ScoreDoc[] scoreDocs = docs.scoreDocs;
            for (int i = 0; i < scoreDocs.length; i++) {
                ScoreDoc scoreDoc = scoreDocs[i];
                //存储信息很少,一般只有id有用,这里只取了1条记录
                int id = scoreDoc.doc;
                document = indexSearcher.doc(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            LuceneUtil.closeIndexReader(indexSearcher);
        }
        return document;
    }

    /**
     * 分页查找索引列表
     */
    @Override
    public ResultBean<Article> getdocumentList(String keywords,int firstResult, int maxResult) throws Exception {
        Analyzer analyzer = LuceneUtil.getAnalyzer();
        String[] fileds = new String[] { "title", "content", "link", "author" ,"pubDate"};
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_44,fileds,analyzer);
        Query query = null;
        if (StringUtils.isNotBlank(keywords)) {
            query = parser.parse(keywords);
        }else{
            //设置默认的模糊查询方式,使用通配符匹配
            Term term=new Term("title","*");
            query=new WildcardQuery(term);
        }
        IndexSearcher indexSearcher = LuceneUtil.getIndexSearcher();
        ResultBean<Article> resultBean = new ResultBean<Article>();
        TopDocs hits = indexSearcher.search(query, firstResult + maxResult);
        resultBean.setTotal(hits.totalHits);
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        Article article = null;
        List<Article> list = new ArrayList<Article>();
        Formatter formatter = new SimpleHTMLFormatter("<font color='red'>","</font>");
        Scorer scorer = new QueryScorer(query, "title");
        Highlighter highlighter = new Highlighter(formatter, scorer);
        int endResult = Math.min(scoreDocs.length, firstResult + maxResult);
        int fragmenter = 200;
        highlighter.setTextFragmenter(new SimpleFragmenter(fragmenter));
        for (int i = firstResult; i < endResult; i++) {
            System.out.println("得分为:" + scoreDocs[i].score);
            int doc = scoreDocs[i].doc;
            System.out.println("docid=" + doc);
            Document document = indexSearcher.doc(doc);
            String content = highlighter.getBestFragment(analyzer, "content", document.get("content"));
            String title = highlighter.getBestFragment(analyzer, "title", document.get("title")); 
            //简化内容
            if (title == null) {
                title = document.get("title");
                if (title != null && title.length() > fragmenter) {
                    title = title.substring(0, fragmenter);
                }
            }
            if (content == null) {
                content = document.get("content");
                if (content != null && content.length() > fragmenter) {
                    content = content.substring(0, fragmenter);
                }
            }
            article = DocumentUtils.documentToArticle(document);
            list.add(article);
        }
        resultBean.setResult(list);
        return resultBean;
    }

}
