package cn.itcast.lucene.utils;

import java.io.File;

/**
 * 常量值设置
 * 
 * @author Liubao
 * @2015年7月26日
 * 
 */
public class Constants {
    //数据源url
    public static final String URL = "http://news.qq.com/newsgn/rss_newsgn.xml";

    public static final String filePath;
    public static final int PAGESIZE = 100;
    public static final String CHARST_FAULT = "utf-8";

    static {
        // eclipse根路径
        //filePath = (System.getProperty("user.dir") + File.separator + "luceneIndexDir").replaceAll("%20", " ");
        
        //classpath路径下
        filePath= (Constants.class.getClassLoader().getResource("/").getPath()+ File.separator + "luceneIndexDir").replaceAll("%20", " ");//将%20替换为空格
        
        //同上
        //filePath= (Thread.currentThread().getContextClassLoader().getResource("").getPath()+ File.separator + "luceneIndexDir").replaceAll("%20", " ");
        //filePath= (Thread.currentThread().getContextClassLoader().getResource("/").getPath()+ File.separator + "luceneIndexDir").replaceAll("%20", " ");
        
        //启动获取失败,不可以
        //filePath= (Thread.currentThread().getClass().getResource("").getPath()+ File.separator + "luceneIndexDir").replaceAll("%20", " ");
        System.out.println(filePath);
    }

}
