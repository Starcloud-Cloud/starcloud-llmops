package com.starcloud.ops.business.dataset.util.dataset;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import io.github.furstenheim.CopyDown;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Slf4j
public class JsoupUtil {


    /**
     * 代理服务器地址
     */
    private static final String PROXY_HOST = "your_proxy_host";
    /**
     * 代理服务器端口
     */
    private static final int PROXY_PORT = 8080;

    // 创建代理对象
    private static final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));


    /**
     * @param url            链接
     * @param acceptLanguage 网页语言 默认为中文
     * @return doc 对象
     */

    public static Document loadUrl(String url, String acceptLanguage) throws IOException {
        if (StrUtil.isBlank(acceptLanguage)) {
            acceptLanguage = "zh";
        }

        // url转换为标准格式
        String normalize = URLUtil.normalize(url);
        // 创建链接
        Connection connect = Jsoup.connect(normalize);
        // 代理设置
        // connect.proxy(proxy)

        connect.header("Accept-Language", acceptLanguage);
        // 设置超时时间
        connect.timeout(5000);
        return Jsoup.connect(normalize).get();
    }

    public static String html2Markdown(String html) {
        CopyDown converter = new CopyDown();
        return converter.convert(html);
    }
}
