package com.huba.spider.proxy;

import com.huba.spider.proxy.domain.Proxy;
import com.huba.spider.proxy.http.HttpManager;
import com.huba.spider.proxy.task.ProxyPageCallable;
import com.huba.common.utils.Preconditions;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class ProxyManager {

    private ProxyManager() {
    }

    public static ProxyManager get() {
        return Holder.MANAGER;
    }

    private static class Holder {
        private static final ProxyManager MANAGER = new ProxyManager();
    }

    /**
     * 抓取代理，成功的代理存放到ProxyPool中
     */
    public void start() {

        Flowable.fromIterable(ProxyPool.proxyMap.keySet())
                .parallel(ProxyPool.proxyMap.size())
                .map(new Function<String, List<Proxy>>() {
                    @Override
                    public List<Proxy> apply(String s) throws Exception {

                        try {
                            return new ProxyPageCallable(s).call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return new ArrayList<Proxy>();
                    }
                })
                .flatMap(new Function<List<Proxy>, Publisher<Proxy>>() {
                    @Override
                    public Publisher<Proxy> apply(List<Proxy> proxies) throws Exception {
                        if (Preconditions.isNotBlank(proxies)) {
                            List<Proxy> result = proxies
                                    .stream()
                                    .parallel()
                                    .filter(new Predicate<Proxy>() {
                                        @Override
                                        public boolean test(Proxy proxy) {
                                            HttpHost httpHost = new HttpHost(proxy.getIp(), proxy.getPort(), proxy.getType());
                                            boolean result = HttpManager.get().checkProxy(httpHost);
                                            if (result) log.info("checkProxy " + proxy.getProxyStr() + ", " + result);
                                            return result;
                                        }
                                    }).collect(Collectors.toList());

                            return Flowable.fromIterable(result);
                        }

                        return Flowable.empty();
                    }
                })
                .runOn(Schedulers.io())
                .sequential()
                .subscribe(new Consumer<Proxy>() {
                    @Override
                    public void accept(Proxy proxy) throws Exception {

                        if (proxy != null) {
                            log.info("accept " + proxy.getProxyStr());
                            proxy.setLastSuccessfulTime(System.currentTimeMillis());
                            ProxyPool.proxyList.add(proxy);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        log.error("ProxyManager is error: " + throwable.getMessage());
                    }
                });
    }
}
