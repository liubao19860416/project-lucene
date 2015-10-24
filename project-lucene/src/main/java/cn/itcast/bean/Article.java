package cn.itcast.bean;

import java.io.Serializable;

/**
 * 信息封装实体
 * 
 * @author Liubao
 * @2015年7月26日
 * 
 */
public class Article implements Serializable{

    private static final long serialVersionUID = -5086893786884753510L;
    private String id;
    private String url;
    private String title;
    private String conent;
    private String author;
    private String pubDate;
    
    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConent() {
        return conent;
    }

    public void setConent(String conent) {
        this.conent = conent;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Article [id=" + id + ", url=" + url + ", title=" + title
                + ", conent=" + conent + ", author=" + author + "]";
    }

}
