package com.huba.spider.extract;

import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MappingRule extends Rule {
    String match = new String("full");
    String map2 = null;
    Pattern p;

    @Override
    public void init(String text, NamedNodeMap attr) {
        super.init(text, attr);
        p = Pattern.compile(_text, Pattern.DOTALL | Pattern.UNICODE_CASE);
        if (_attr == null) {
            return;
        }
        if (_attr.getNamedItem("match") != null) {
            match = _attr.getNamedItem("match").getNodeValue();
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
        String match = "full";
        String map2 = null;
        if (map2 == null) {
            return datas;
        }
        for (Object data : datas) {
            if (!(data instanceof String)) {
                continue;
            }
            Matcher m = p.matcher((String) data);
            boolean is_match = false;
            if (match.equals("full")) {
                if (data == _text) {
                    data = map2;
                }
            } else {
                is_match = m.find();
                if (is_match) {
                    data = map2;
                }
            }
            parsed_result.add(data);
        }
        return parsed_result;
    }
}
