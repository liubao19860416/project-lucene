package cn.itcast.lucene.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.bean.Article;
import cn.itcast.bean.ResultBean;
import cn.itcast.lucene.dao.LuceneDao;
import cn.itcast.lucene.utils.DocumentUtils;

@Service
public class LuceneServiceImpl implements LuceneService {

    @Autowired
    private LuceneDao luceneDao;
    
    @Override
    public <T> boolean addIndex(T object) throws IOException {
        Document document=null;
        boolean result=true;
        try {
            if(object instanceof Document){
                document=(Document)object;
            }else if(object instanceof Article){
                document=DocumentUtils.articleToDocument((Article)object);
            }
            luceneDao.addIndex(document);
        } catch (Exception e) {
            result=false;
            e.printStackTrace();
        }
        return result;
    }

//    @Override
//    public boolean addIndex(Document document) throws IOException{
//        boolean result=true;
//        try {
//            luceneDao.addIndex(document);
//        } catch (Exception e) {
//            result=false;
//            e.printStackTrace();
//        }
//        return result;
//    }
    
//    @Override
//    public boolean addIndex(Article article) throws IOException{
//        boolean result=true;
//        try {
//            luceneDao.addIndex(DocumentUtils.articleToDocument(article));
//        } catch (Exception e) {
//            result=false;
//            e.printStackTrace();
//        }
//        return result;
//    }
    
    @Override
    public <T> void addIndex(List<T> objectList) throws IOException {
        List<Document> documentList=new ArrayList<Document>();
        for (T t : objectList) {
            if(t instanceof Document){
                documentList.add((Document)t);
            }else if(t instanceof Article){
                documentList.add(DocumentUtils.articleToDocument((Article)t));
            }
        }
        try {
            luceneDao.addIndex(documentList);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        }
    }
    
//    @Override
//    public void addIndex(List<Article> articleList) throws IOException{
//        List<Document> documentList=new ArrayList<Document>();
//        for (Article article : articleList) {
//            documentList.add(DocumentUtils.articleToDocument(article));
//        }
//        try {
//            luceneDao.addIndex(documentList);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally{
//            documentList=null;
//        }
//    }

    @Override
    public void updateIndexById(Document document) throws IOException {
        luceneDao.updateIndexById(document);
    }

    @Override
    public void deleteIndexById(String Id) throws IOException {
        luceneDao.deleteIndexById(Id);
    }

    @Override
    public ResultBean<Article> getResultBeanList(String keywords, int firstResult,
            int maxResult) throws Exception {
        return luceneDao.getdocumentList(keywords, firstResult, maxResult);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Article getResultBeanById(String Id) throws Exception {
        return luceneDao.findDocumentById(Id,Article.class);
    }

}
