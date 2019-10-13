package com.huba.spider.extract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class PrefixRule extends Rule {
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
            String elem = _text + (String) data;
            parsed_result.add(elem);
        }
        return parsed_result;
    }
}
