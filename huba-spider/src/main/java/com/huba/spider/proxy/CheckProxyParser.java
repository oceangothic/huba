package com.huba.spider.proxy;

import com.huba.spider.proxy.site.XiaoHeXiaProxyListPageParser;

import java.util.HashMap;
import java.util.Map;

public class CheckProxyParser {

    public static void main(String[] args) {
        System.out.println("Start...");

        Map<String, Class> proxyMap = new HashMap<>();
//        proxyMap.put("http://www.xicidaili.com/nn/1.html", XicidailiProxyListPageParser.class);
//        proxyMap.put("https://proxy.mimvp.com/", MimvpProxyListPageParser.class);
//        proxyMap.put("http://www.mogumiao.com/web", MogumiaoProxyListPageParser.class);
//        proxyMap.put("http://www.goubanjia.com/", GoubanjiaProxyListPageParser.class);
//        proxyMap.put("http://m.66ip.cn/2.html", M66ipProxyListPageParser.class);
//        proxyMap.put("http://www.data5u.com/", Data4uProxyListPageParser.class);
//        proxyMap.put("https://list.proxylistplus.com/Fresh-HTTP-Proxy-List-1", ProxyListPlusProxyListPageParser.class);
//        proxyMap.put("http://www.ip3366.net/", Ip3366ProxyListPageParser.class);//TODO gb2312如何处理？
//        proxyMap.put("http://www.feilongip.com/", FeilongipProxyListPageParser.class);
//        proxyMap.put("http://proxydb.net/", ProxyDbProxyListPageParser.class);
        proxyMap.put("http://www.xiaohexia.cn/", XiaoHeXiaProxyListPageParser.class);

        ProxyPool.proxyMap = proxyMap;
        ProxyManager proxyManager = ProxyManager.get();
        proxyManager.start();

        System.out.println("End...");
    }
}
