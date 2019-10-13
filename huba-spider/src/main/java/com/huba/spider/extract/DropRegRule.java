package com.huba.spider.extract;

import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropRegRule extends Rule {
    Pattern p;

    @Override
    public void init(String text, NamedNodeMap attr) {
        super.init(text, attr);
        p = Pattern.compile(_text, Pattern.DOTALL | Pattern.UNICODE_CASE);
    }

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
            Matcher m = p.matcher((String) data);
            boolean is_match = false;
            is_match = m.find();
            if (!is_match) {
                parsed_result.add(data);
            }
        }
        return parsed_result;
    }
}
