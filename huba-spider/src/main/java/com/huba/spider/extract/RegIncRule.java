package com.huba.spider.extract;

import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegIncRule extends Rule {
    String pattern;
    int incr = 1;
    Pattern p;

    @Override
    public void init(String text, NamedNodeMap attr) {
        super.init(text, attr);
        p = Pattern.compile(_text, Pattern.DOTALL | Pattern.UNICODE_CASE);
        if (null == _attr) {
            return;
        }
        if (_attr.getNamedItem("pattern") != null) {
            pattern = _attr.getNamedItem("pattern").getNodeValue();
        }
        if (_attr.getNamedItem("incr") != null) {
            incr = Integer.parseInt(_attr.getNamedItem("incr").getNodeValue());
        }
    }

    @Override
    public List<Object> process(List<Object> datas) {
        List<Object> parsed_result = new ArrayList<Object>();
        for (Object data : datas) {
            if (!(data instanceof String)) {
                continue;
            }
            Matcher m = p.matcher((String) data);
            boolean is_match = false;
            is_match = m.find();
            if (is_match) {
                String[] tmp_str = m.group(1).split("=");
                if (tmp_str.length != 2) {
                    continue;
                }
                int number = Integer.parseInt(tmp_str[1]);
                number = number + incr;
                parsed_result.add(((String) data).replace(m.group(1), tmp_str[0] + "=" + String.valueOf(number)));
            }
        }
        return parsed_result;
    }
}
