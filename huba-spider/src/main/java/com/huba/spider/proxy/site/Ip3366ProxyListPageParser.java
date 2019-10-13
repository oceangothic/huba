package com.huba.spider.proxy.site;

import com.huba.spider.proxy.ProxyListPageParser;
import com.huba.spider.proxy.config.Constant;
import com.huba.spider.proxy.domain.Proxy;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * http://www.ip3366.net/
 */
@Slf4j
public class Ip3366ProxyListPageParser implements ProxyListPageParser {

    @Override
    public List<Proxy> parse(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select("div[id=list] table tbody tr");
        List<Proxy> proxyList = new ArrayList<>();
        for (Element element : elements) {
            String ip = element.select("td:eq(0)").first().text();
            String port = element.select("td:eq(1)").first().text();
//            String isAnonymous = element.select("td:eq(2)").first().text();  //gb2312乱码未解决
            String isAnonymous = "匿";
            String type = element.select("td:eq(3)").first().text();
            if (!anonymousFlag || isAnonymous.contains("匿") || isAnonymous.contains("anonymous")) {
                proxyList.add(new Proxy(ip, Integer.valueOf(port), type, Constant.TIME_INTERVAL));
            }
        }
        return proxyList;
    }
}
