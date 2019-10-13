package com.huba.spider.proxy.site;

import com.huba.spider.proxy.ProxyListPageParser;
import com.huba.spider.proxy.config.Constant;
import com.huba.spider.proxy.domain.Proxy;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * http://www.xiaohexia.cn/
 */
@Log4j2
public class XiaoHeXiaProxyListPageParser implements ProxyListPageParser {

    @Override
    public List<Proxy> parse(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select("div[class=table-responsive] table tbody tr:gt(0)");
        List<Proxy> proxyList = new ArrayList<>();
        for (Element element : elements) {
            String ip = element.select("td:eq(0)").first().text();
            String port = element.select("td:eq(1)").first().text();
            String isAnonymous = element.select("td:eq(2)").first().text();
            String type = element.select("td:eq(3)").first().text();
            if (!anonymousFlag || isAnonymous.contains("匿") || isAnonymous.contains("anonymous")) {
                proxyList.add(new Proxy(ip, Integer.valueOf(port), type, Constant.TIME_INTERVAL));
            }
        }
        return proxyList;
    }

}
