package cn.itcast.lucene.dao;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;

import cn.itcast.bean.Article;
import cn.itcast.bean.ResultBean;

/**
 * LuceneDAO层接口
 * 
 * @author Liubao
 * @2015年7月26日
 * 
 */
public interface LuceneDao {
    
    public void addIndex(Document document) throws IOException;
    
    public void addIndex(List<Document> documentList) throws IOException;

    public void updateIndexById(Document document) throws IOException;

    public void deleteIndexById(String Id) throws IOException;
    
    public <T> T findDocumentById(String Id,Class<T> classType) throws Exception;
    
    public ResultBean<Article> getdocumentList(String keywords,
            int firstResult, int maxResult) throws Exception;
    
}
