package com.huba.spider.extract;

import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExistRule extends Rule {
    String map2 = new String("1");
    Pattern p = null;

    @Override
    public void init(String text, NamedNodeMap attr) {
        super.init(text, attr);
        p = Pattern.compile(_text, Pattern.DOTALL | Pattern.UNICODE_CASE);
        if (_attr == null) {
            return;
        }
        if (_attr.getNamedItem("map2") != null) {
            map2 = _attr.getNamedItem("map2").getNodeValue();
        }
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
            if (is_match) {
                parsed_result.add(map2);
            }
        }
        return parsed_result;
    }
}
