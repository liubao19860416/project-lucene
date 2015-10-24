package cn.itcast.bean;

import java.util.List;
/**
 * 结果集信息
 * @author Liubao
 * @2015年7月26日
 *
 */
public class ResultBean<T> {
    private int total;
    private List<T> result;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

}
