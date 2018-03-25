package info.xiancloud.qclouddocker.api;

import info.xiancloud.core.conf.XianConfig;

/**
 * in order to hide security info, we do not provide the configuration in the plugin.properties,application.properties,jvm system properties.
 * Instead we set configuration via the operation system environment.
 * For mac os x, edit the ~/.bash_profile add the following content in this file:
 * <p>
 * export dockerServiceRegistryUrl=ccr.ccs.tencentyun.com/xxxx/
 * export dockerServiceNonproductionClusterId=cls-xxxxxx
 * export dockerServiceProductionClusterId=cls-xxxxxx
 * export dockerServiceSecretId=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 * export dockerServiceSecretKey=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 * export dockerServiceSignatureMethod=xxxxxx
 * export dockerServiceRegion=gz/bj/sh
 * <p>
 * See res/plugin.properties
 *
 * @author yyq
 */
public class QCloudConfig {

    public final static String SecretId = XianConfig.get("dockerServiceSecretId");
    public final static String SecretKey = XianConfig.get("dockerServiceSecretKey");
    //signature method
    public final static String SignatureMethod = XianConfig.get("dockerServiceSignatureMethod");
    //region
    public final static String Region = XianConfig.get("dockerServiceRegion");
}
