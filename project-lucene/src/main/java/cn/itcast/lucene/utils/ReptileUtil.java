package cn.itcast.lucene.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;

import cn.itcast.bean.Article;
/**
 * 获取网页数据GET和POST请求方式工具类
 * 
 * @author Liubao
 * @2015年7月26日
 *
 */
public class ReptileUtil {

    /**
     * GET请求方式
     * @param url
     * @param param
     * @param charset
     */
    public static String sendGet(String url, String param, String charset) {
        StringBuffer sb=new StringBuffer();
        try {
            String urlName = url + "?" + param;
            URL U = new URL(urlName);
            URLConnection connection = U.openConnection();
            connection.connect();
            InputStreamReader isr = null;
            if (StringUtils.isNotBlank(charset)) {
                isr = new InputStreamReader(connection.getInputStream(),charset);
            } else {
                isr = new InputStreamReader(connection.getInputStream(),Constants.CHARST_FAULT);
            }
            BufferedReader in = new BufferedReader(isr);
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
        } catch (Exception e) {
            System.out.println("error occur:" + e);
        }
        return sb.toString();
    }

    /**
     * POST请求方式
     * @param url
     * @param param
     * @param charset
     */
    public static String sendPost(String url, String param, String charset) {
        StringBuffer sb=new StringBuffer();
        try {
            URL httpurl = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("POST");
            PrintWriter out = new PrintWriter(httpConn.getOutputStream());
            out.write(param);
            out.flush();
            out.close();
            BufferedReader in = null;
            if (StringUtils.isNotBlank(charset)) {
                in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), charset));
            } else {
                in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(),Constants.CHARST_FAULT));
            }
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
        } catch (Exception e) {

        }
        return sb.toString();
    }

}
