package info.xiancloud.apidoc;

/**
 * api doc configuration
 *
 * @author yyq
 */
public class ApiBuildConfig {

    /**
     * hugo 内容md文件存放路径
     * <p>
     * 注 !!!! 如果要变更此文件夹，请将变更后的文件夹读写权限赋予当前操作的ftp对应的账号,不然操作无效
     */
    public final static String HUGOMDPATH = "/data/workspace/xiancloud-api-site/content/";

    //hugo 对外部文档 md文件路径
    public final static String HUGOMDPATHOUT = "/data/workspace/xiancloud-api-out/content/";

    // unit md文件名
    public final static String HUGO_UNIT_MD = "unit.md";

    // oauth20 md文件名
    public final static String HUGOAOUTH20MD = "oauth20.md";
}
