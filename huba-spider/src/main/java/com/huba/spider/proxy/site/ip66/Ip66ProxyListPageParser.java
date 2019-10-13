package com.huba.spider.proxy.site.ip66;

import com.huba.spider.proxy.ProxyListPageParser;
import com.huba.spider.proxy.config.Constant;
import com.huba.spider.proxy.domain.Proxy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Ip66ProxyListPageParser implements ProxyListPageParser {

    @Override
    public List<Proxy> parse(String content) {
        List<Proxy> proxyList = new ArrayList<>();
        if (content == null || content.equals("")) {
            return proxyList;
        }
        Document document = Jsoup.parse(content);
        Elements elements = document.select("table tr:gt(1)");
        for (Element element : elements) {
            String ip = element.select("td:eq(0)").first().text();
            String port = element.select("td:eq(1)").first().text();
            String isAnonymous = element.select("td:eq(3)").first().text();
//            if(!anonymousFlag || isAnonymous.contains("匿")){
            proxyList.add(new Proxy(ip, Integer.valueOf(port), Constant.TIME_INTERVAL));
//            }
        }
        return proxyList;
    }
}
