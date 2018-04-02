package info.xiancloud.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * data base Page query bean
 *
 * @author hang, happyyangyuan
 */
public class Page implements Serializable {

    private static final long serialVersionUID = -5395997221963176643L;

    private List list;                // list result of this page
    private int pageNumber;           // page number
    private int pageSize;             // result amount of this page
    private int totalPage;            // total page
    private int totalRow;             // total row

    public static Page create(String jsonStr) {
        if (jsonStr != null) {
            return create(JSONObject.parseObject(jsonStr));
        }
        throw new IllegalArgumentException("json string is not allowed to be null!");
    }

    public static Page create(JSONObject json) {
        if (json != null) {
            JSONArray list = json.getJSONArray("list");
            Integer pageNumber = json.getInteger("pageNumber");
            Integer pageSize = json.getInteger("pageSize");
            Integer totalPage = json.getInteger("totalPage");
            Integer totalRow = json.getInteger("totalRow");
            if (list != null && pageNumber != null && pageSize != null && totalPage != null && totalRow != null) {
                return new Page(list, pageNumber, pageSize, totalPage, totalRow);
            } else {
                throw new IllegalArgumentException("缺少参数,需要参数为[list,pageNumber,pageSize,totalPage,totalRow]!");
            }
        }
        throw new IllegalArgumentException("参数json为空!");
    }

    /**
     * Constructor.
     *
     * @param list       the list of paginate result
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param totalPage  the total page of paginate
     * @param totalRow   the total row of paginate
     */
    public Page(List list, int pageNumber, int pageSize, int totalPage, int totalRow) {
        this.list = list;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalRow = totalRow;
    }

    /**
     * Return list of this page.
     */
    public List getList() {
        return list;
    }

    /**
     * Return page number.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Return page size.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Return total page.
     */
    public int getTotalPage() {
        return totalPage;
    }

    /**
     * Return total row.
     */
    public int getTotalRow() {
        return totalRow;
    }

    public boolean isFirstPage() {
        return pageNumber == 1;
    }

    public boolean isLastPage() {
        return pageNumber == totalPage;
    }
}

