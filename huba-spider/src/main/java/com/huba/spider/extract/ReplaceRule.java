package com.huba.spider.extract;

import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceRule extends Rule {
    String pattern = null;
    Pattern p = null;

    @Override
    public void init(String text, NamedNodeMap attr) {
        super.init(text, attr);
        if (_attr == null) {
            return;
        }
        if (_attr.getNamedItem("pattern") != null) {
            pattern = _attr.getNamedItem("pattern").getNodeValue();
            p = Pattern.compile(pattern);
        }
    }

    @Override
    public List<Object> process(List<Object> datas) {
        if (datas == null) {
            return datas;
        }
        List<Object> parsed_result = new ArrayList<Object>();
        if (pattern == null || p == null) {
            return datas;
        }
        for (Object data : datas) {
            if (!(data instanceof String)) {
                continue;
            }
            Matcher m = p.matcher((String) data);
            parsed_result.add(m.replaceAll(_text));
        }
        return parsed_result;
    }
}
