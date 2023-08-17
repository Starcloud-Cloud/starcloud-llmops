package com.starcloud.ops.business.dataset.util.dataset;

import cn.hutool.core.util.URLUtil;
import io.github.furstenheim.CopyDown;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Slf4j
public class JsoupUtil {

    public static Document loadUrl(String url) {

        try {
            String normalize = URLUtil.normalize(url);
            Document doc = Jsoup.connect(normalize).get();
            return doc;
        } catch (Exception e) {

            log.error("====> 网页解析失败,数据状态为 false，网页链接为{}", url);
        }

        return null;
    }

    public static String html2Markdown(String html) {
        CopyDown converter = new CopyDown();
        return converter.convert(html);
    }
}
