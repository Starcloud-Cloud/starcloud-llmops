package com.starcloud.ops.business.dataset.util.dataset;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import io.github.furstenheim.CopyDown;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class JsoupUtil {


    /**
     * 代理服务器地址
     */
    private static final String PROXY_HOST = "18.118.101.217";
    /**
     * 代理服务器端口
     */
    private static final int PROXY_PORT = 38091;

    // 创建代理对象
    private static final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));


    /**
     * @param url            链接
     * @param acceptLanguage 网页语言 默认为中文
     * @return doc 对象
     */

    public static Document loadNoProxyUrl(String url, String acceptLanguage) throws IOException {
        if (StrUtil.isBlank(acceptLanguage)) {
            acceptLanguage = "zh";
        }
        // 创建自定义 TrustManager
        TrustManager[] trustManagers = new TrustManager[]{new TrustAllCertManager()};

        // 获取默认的 SSLContext
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        // 应用自定义的 SSLContext
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        // url转换为标准格式
        String normalize = URLUtil.normalize(url);
        // 创建链接
        Connection connect = Jsoup.connect(normalize);

        connect.header("Accept-Language", acceptLanguage);
        // 设置超时时间
        connect.timeout(5000);
        return Jsoup.connect(normalize).get();
    }


    public static Document loadProxyUrl(String url, String acceptLanguage) throws IOException {
        if (StrUtil.isBlank(acceptLanguage)) {
            acceptLanguage = "zh";
        }

        // 创建自定义 TrustManager
        TrustManager[] trustManagers = new TrustManager[]{new TrustAllCertManager()};

        // 获取默认的 SSLContext
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        // 应用自定义的 SSLContext
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        // url转换为标准格式
        String normalize = URLUtil.normalize(url);
        // 创建链接
        Connection connect = Jsoup.connect(normalize);
        // 代理设置
        connect.proxy(proxy);

        connect.header("Accept-Language", acceptLanguage);
        // 设置超时时间
        connect.timeout(5000);
        return Jsoup.connect(normalize).get();
    }

    public static Document loadUrl(String url, String acceptLanguage) throws Exception {
        Document doc;
        try {
            doc = JsoupUtil.loadNoProxyUrl(url, acceptLanguage);
            return doc;
        } catch (Exception e) {
            log.error("====> 网页解析失败,数据状态为 false，网页链接为{}，开始启用代理访问，切换代理请求", url);
            try {
                doc = JsoupUtil.loadProxyUrl(url, acceptLanguage);
                return doc;
            } catch (Exception e1) {
                log.error("====> 代理请求请求网页解析失败,数据状态为 false，网页链接为{}", url);
                throw new Exception(e1);
            }
        }


    }

    public static String html2Markdown(String html) {
        CopyDown converter = new CopyDown();
        return converter.convert(html);
    }

    public static void main(String[] args) throws IOException {
        Document document = loadNoProxyUrl("https://www.chinatax.gov.cn/chinatax/n363/c5211371/content.html", null);
        System.out.println(document.text());
    }
}
