package cn.itcast.lucene.listener;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.itcast.lucene.service.LuceneService;
import cn.itcast.lucene.utils.Constants;

public class StartReptileListener implements ServletContextListener {

    private LuceneService luceneService=null;//=new LuceneServiceImpl();
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    /**
     * 在监听器中,不能通过注解注入Spring中的对象,只能通过容器对象获取
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if(luceneService==null){
            WebApplicationContext appctx = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
            luceneService = (LuceneService) appctx.getBean(LuceneService.class);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            URL URL = new URL(Constants.URL);
            /**
             * <html></html>
             * <xml></xml> Soap=http +xml
             * httpClient
             */
            URLConnection connection = URL.openConnection();
            connection.connect();
            SAXReader reader = new SAXReader();
            org.dom4j.Document doc = reader.read(connection.getInputStream());
            Element root = doc.getRootElement();
            Element element;
            Element subfoo;
            int ios = 1;
            List<Document> documentList=null;
            for (Iterator<Element> iterator = root.elementIterator("channel"); iterator.hasNext();) {
                element = iterator.next();
                documentList=new ArrayList<Document>();
                Document document =null;
                for (Iterator<Element> iterator2 = element.elementIterator("item"); iterator2.hasNext();) {
                    System.out.println("--------------------------------------------------------------");
                    subfoo = iterator2.next();
                    String title = subfoo.elementTextTrim("title").trim();
                    String link = subfoo.elementTextTrim("link");
                    String author = subfoo.elementTextTrim("author");
                    String content = subfoo.elementTextTrim("description");
                    String pubDate = subfoo.elementTextTrim("pubDate");
                    System.out.println("title===" + title);
                    System.out.println("link===" + link);
                    System.out.println("author===" + author);
                    System.out.println("description===" + content);
                    System.out.println("pubDate===" + pubDate);
                    
                    document = new Document();
                    document.add(new StringField("Id", String.valueOf(ios++),Store.YES));
                    document.add(new TextField("title", title, Store.YES));
                    document.add(new TextField("content", content, Store.YES));
                    document.add(new StringField("author", author, Store.YES));
                    document.add(new StringField("link", link, Store.YES));
//                    document.add(new StringField("date",sdf.format(new Date()), Store.YES));
                    document.add(new StringField("date",pubDate, Store.YES));
                    documentList.add(document);
                }
                luceneService.addIndex(documentList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
