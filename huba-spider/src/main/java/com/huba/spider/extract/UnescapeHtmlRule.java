package com.huba.spider.extract;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class UnescapeHtmlRule extends Rule {
    @Override
    public List<Object> process(List<Object> datas) {
        if (datas == null) {
            return datas;
        }
        List<Object> parsed_result = new ArrayList<Object>();
        for (Object data : datas) {
            if (!(data instanceof String)) {
                continue;
            }
            String elem = StringEscapeUtils.unescapeHtml3((String) data);
            parsed_result.add(elem);
        }
        return parsed_result;
    }
}
