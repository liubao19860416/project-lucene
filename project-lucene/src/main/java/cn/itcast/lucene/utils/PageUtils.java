package cn.itcast.lucene.utils;

import cn.itcast.bean.PageIndex;

/**
 * 页码计算工具类:设置显示10夜数据,每页10条记录
 * 
 * @author Liubao
 * @2015年7月26日
 * 
 */
public class PageUtils {

    public static void main(String[] args) {
        PageIndex pageCount = getPageCount(6, 203, 10);
        System.out.println(pageCount);
    }

    public static PageIndex getPageCount(int currentPage, int total,
            int pageSize) {
        int viewpagecount = 10;
        int totalpage = total / pageSize;
        int startpage = currentPage
                - (viewpagecount % 2 == 0 ? viewpagecount / 2 - 1
                        : viewpagecount / 2);
        int endpage = currentPage + viewpagecount / 2;
        if (startpage < 1) {
            startpage = 1;
            if (totalpage >= viewpagecount)
                endpage = viewpagecount;
            else
                endpage = totalpage;
        }
        if (endpage > totalpage) {
            endpage = totalpage;
            if ((endpage - viewpagecount) > 0)
                startpage = endpage - viewpagecount + 1;
            else
                startpage = 1;
        }
        PageIndex pageIndex = new PageIndex();
        pageIndex.setStartPage(startpage);
        pageIndex.setEndPage(endpage);
        return pageIndex;
    }

}
