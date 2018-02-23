package info.xiancloud.plugin.httpclient.apache_http.basic_auth;

import info.xiancloud.plugin.httpclient.apache_http.IApacheHttpClient;
import info.xiancloud.plugin.httpclient.apache_http.no_auth.ApacheHttpClient;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Map;

/**
 * @author happyyangyuan
 */
public class BasicAuthApacheHttpClient extends ApacheHttpClient {

    final private String userName;
    final private String password;

    public static IApacheHttpClient newInstance(String url, String userName, String password, Map<String, String> headers) {
        return new BasicAuthApacheHttpClient(url, userName, password, headers);
    }

    private BasicAuthApacheHttpClient(String url, String userName, String password, Map<String, String> headers) {
        super(url, headers);
        this.userName = userName;
        this.password = password;
        client = getHttpClient(userName, password);
    }

    private static HttpClient getHttpClient(String username, String password) {
        HttpClient httpClient;
        if (username != null && password != null) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            provider.setCredentials(AuthScope.ANY, credentials);
            httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        } else {
            httpClient = HttpClientBuilder.create().build();
        }

        return httpClient;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
