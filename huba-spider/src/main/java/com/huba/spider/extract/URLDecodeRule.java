package com.huba.spider.extract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class URLDecodeRule extends Rule {
    private static final Logger logger = LoggerFactory.getLogger(JPathRule.class);

    @Override
    public List<Object> process(List<Object> datas) {
        if (datas == null) {
            return datas;
        }
        List<String> parsed_result = new ArrayList<String>();
        for (Object data : datas) {
            if (!(data instanceof String)) {
                continue;
            }
            String elem;
            try {
                elem = URLDecoder.decode((String) data, "utf-8");
            } catch (Exception e) {
                logger.error("urldecode error:" + e);
                continue;
            }
            parsed_result.add(elem);
        }
        return _convert_object(parsed_result);
    }
}
