package com.huba.spider.proxy;


import com.huba.spider.proxy.domain.Proxy;

import java.util.List;

public interface ProxyListPageParser {

    /**
     * 是否只要匿名代理
     */
    boolean anonymousFlag = true;

    List<Proxy> parse(String content);
}
