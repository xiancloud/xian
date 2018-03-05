package info.xiancloud.plugin.support.cache;

import com.alibaba.fastjson.JSONObject;

public class CacheConfigBean
{

    private String host;

    private int port;

    private String password;

    private int dbIndex;

    public CacheConfigBean()
    {

    }

//    public CacheConfigBean()
//    {
//        String url = null;
//        if (EnvUtil.isQcloudLan())
//            url = XianConfig.get("redisLanUrl"); // 腾讯云内网内
//        else
//            url = XianConfig.get("redisInternetUrl"); // 外网
//
//        String password = XianConfig.get("redisPassword");
//
//        int dbIndex = XianConfig.getIntValue("redisDbIndex", 0);
//
//        constructor(url, password, dbIndex);
//    }

    /**
     * @param url 127.0.0.1:6379
     * @param password
     */
    public CacheConfigBean(String url, String password)
    {
        constructor(url, password, 0);
    }

    public CacheConfigBean(String url, String password, int dbIndex)
    {
        constructor(url, password, dbIndex);
    }

    public CacheConfigBean(String host, int port, String password, int dbIndex)
    {
        constructor(host, port, password, dbIndex);
    }

    private void constructor (String url, String password, int dbIndex)
    {
        String host = url;
        int port = 6379;

        if(url.indexOf(":") > -1)
        {
            host = url.substring(0, url.indexOf(":"));
            port = Integer.parseInt(url.substring(url.indexOf(":") + 1));
        }

        constructor(host, port, password, dbIndex);
    }

    private void constructor (String host, int port, String password, int dbIndex)
    {
        if(host == null || "".equals(host))
            throw new IllegalArgumentException("域名, IP 为 NULL");

        this.host = host;
        this.port = port;
        this.password = password;
        this.dbIndex = dbIndex;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public int getDbIndex()
    {
        return dbIndex;
    }

    public void setDbIndex(int dbIndex)
    {
        this.dbIndex = dbIndex;
    }

    @Override
    public String toString()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("host", this.host);
        jsonObject.put("port", this.port);
        jsonObject.put("dbIndex", this.dbIndex);

        return jsonObject.toJSONString();
    }

}
