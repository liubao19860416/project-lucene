package cn.itcast.lucene.service;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;

import cn.itcast.bean.Article;
import cn.itcast.bean.ResultBean;

public interface LuceneService {
    
    public <T> boolean addIndex(T object) throws IOException;
//    public boolean addIndex(Article article) throws IOException;
//    public boolean addIndex(Document document) throws IOException;
    
    public <T> void addIndex(List<T> objectList) throws IOException;
    //public void addIndex(List<Article> articleList) throws IOException;
    //public void addIndex(List<Document> documentList) throws IOException;
    
    public void updateIndexById(Document document) throws IOException;

    public void deleteIndexById(String Id) throws IOException;
    
    public <T> T getResultBeanById(String Id) throws Exception;

    public ResultBean<Article> getResultBeanList(String keywords, int firstReslut,
            int maxResult) throws Exception;



}
