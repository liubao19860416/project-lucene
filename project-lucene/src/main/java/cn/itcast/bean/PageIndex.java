package cn.itcast.bean;

/**
 * 分页页码信息
 * 
 * @author Liubao
 * @2015年7月26日
 * 
 */
public class PageIndex {

    private int startPage;
    private int endPage;

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    @Override
    public String toString() {
        return "PageIndex [startPage=" + startPage + ", endPage=" + endPage
                + "]";
    }

}
