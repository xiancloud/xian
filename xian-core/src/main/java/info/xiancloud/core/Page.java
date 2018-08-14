package info.xiancloud.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.util.Reflection;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * database Page query bean
 *
 * @author happyyangyuan
 */
public class Page implements Serializable {

    /**
     * list result of this page
     */
    private List<Map<String, Object>> list;
    /**
     * page number
     */
    private int pageNumber;
    /**
     * result amount of this page
     */
    private int pageSize;
    /**
     * total page
     */
    private int totalPage;
    /**
     * total row
     */
    private int totalRow;

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
     * Constructor
     *
     * @param list       the list of paginate result
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param totalPage  the total page of paginate
     * @param totalRow   the total row of paginate
     */
    public Page(List<Map<String, Object>> list, int pageNumber, int pageSize, int totalPage, int totalRow) {
        this.list = list;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalRow = totalRow;
    }

    /**
     * Constructor
     *
     * @param list       the list of paginate result
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param totalPage  the total page of paginate
     * @param totalRow   the total row of paginate
     */
    public Page(JSONArray list, int pageNumber, int pageSize, int totalPage, int totalRow) {
        this.list = Reflection.toType(list, List.class);
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

