package cn.itcast.lucene.utils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import cn.itcast.bean.Article;

/**
 * 文件和实体bean转换工具类
 * 
 * @author Liubao
 * @2015年7月26日
 * 
 */
public class DocumentUtils {

    public static void main(String[] args) {
        Document document = null;
        DocumentUtils.documentToArticle(document);
    }

    /**
     * 将Article对象转换为Document对象
     */
    public static Document articleToDocument(Article article) {
        Document document = new Document();
        document.add(new TextField("title", article.getTitle(), Store.YES));
        document.add(new TextField("content", article.getConent(), Store.YES));
        document.add(new StringField("link", article.getUrl(), Store.YES));
        document.add(new StringField("author", article.getAuthor(), Store.YES));
        document.add(new StringField("pubDate", article.getPubDate(), Store.YES));
        return document;
    }

    /**
     * 将Document对象转换为Article对象
     */
    public static Article documentToArticle(Document document) {
        Article article = new Article();
        article.setId(document.get("Id"));
        article.setAuthor(document.get("author"));
        article.setConent(document.get("content"));
        article.setUrl(document.get("link"));
        article.setTitle(document.get("title"));
        article.setPubDate(document.get("pubDate"));
        return article;
    }

}
