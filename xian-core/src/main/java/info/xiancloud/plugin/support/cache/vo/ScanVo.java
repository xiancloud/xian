package info.xiancloud.plugin.support.cache.vo;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class ScanVo
{
    public static final String CURSOR_START_END = "0"; // 起始, 结束 标识

    private String cursor;

    private List<String> result;

    public ScanVo ()
    {

    }

    public ScanVo (JSONObject jsonObject)
    {
        if(!jsonObject.containsKey("cursor"))
            throw new IllegalArgumentException("缺少 cursor 参数");
        if(!jsonObject.containsKey("result"))
            throw new IllegalArgumentException("缺少 result 参数");

        this.cursor = jsonObject.getString("cursor");
        this.result = (List<String>) jsonObject.get("result");
    }

    /**
     * 判断是否结束迭代
     * @return
     */
    public boolean isEndIteration ()
    {
        return CURSOR_START_END.equals(this.cursor);
    }

    public String getCursor()
    {
        return cursor;
    }

    public void setCursor(String cursor)
    {
        this.cursor = cursor;
    }

    public List<String> getResult()
    {
        return result;
    }

    public void setResult(List<String> result)
    {
        this.result = result;
    }

}
