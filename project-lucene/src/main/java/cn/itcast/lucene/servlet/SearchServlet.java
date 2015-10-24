package cn.itcast.lucene.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.itcast.bean.Article;
import cn.itcast.bean.PageIndex;
import cn.itcast.bean.ResultBean;
import cn.itcast.lucene.service.LuceneService;
import cn.itcast.lucene.service.LuceneServiceImpl;
import cn.itcast.lucene.utils.Constants;
import cn.itcast.lucene.utils.PageUtils;
/**
 * 搜索到核心servlet实现
 * 
 * @author Liubao
 * @2015年7月26日
 *
 */
public class SearchServlet extends HttpServlet {

    private static final long serialVersionUID = 2414616064161536510L;
    
    @Autowired
    private LuceneService luceneService ;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String page = req.getParameter("currentPage");
        int current = Integer.parseInt(page);
        System.out.println(current);
        int firstResult = (current - 1) * Constants.PAGESIZE; 
        int maxreResult = current * Constants.PAGESIZE; 
        String keywords = req.getParameter("keywords");
        ResultBean<Article> resultBean=null;
        if(luceneService==null){
            WebApplicationContext appctx = WebApplicationContextUtils.getWebApplicationContext(req.getSession().getServletContext());
            luceneService = (LuceneService) appctx.getBean(LuceneService.class);
        }
        try {
            resultBean = luceneService.getResultBeanList(keywords, firstResult, maxreResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int totalPage = resultBean.getTotal() / Constants.PAGESIZE;
        System.out.println("------------------------------" + resultBean.getTotal());
        PageIndex pageIndex = PageUtils.getPageCount(current, resultBean.getTotal(), 10);
        StringBuffer buffer = new StringBuffer();
        for (int i = pageIndex.getStartPage(); i <= pageIndex.getEndPage(); i++) {
            if (current == i) {
                buffer.append("<strong>").append(i).append("</strong>");
            } else {
                buffer.append("<a style='width:65px;' href='query?currentPage=")
                        .append(i).append("&keywords=").append(keywords)
                        .append("'>").append(i).append("</a>");
            }
        }
        System.out.println("date==" + buffer.toString());
        req.setAttribute("pagetotal", buffer.toString());
        req.setAttribute("totalPage", totalPage);
        req.setAttribute("resultbean", resultBean);
        /**
         * String keywords="������" Query query=new TermQuery(new
         * Term("title",keywords)); QueryParser quereyparse=new
         * MultiQueryParser(version,String fields [] ,anlyzer); Query
         * query=quereyparse.parser(keywords); //standardAnalyzer(); 
         * 
         */
        req.getRequestDispatcher("/WEB-INF/jsp/querylist.jsp").forward(req, resp);

    }

}
