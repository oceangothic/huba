package com.huba.spider.extract;

import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegRule extends Rule {
    String connector = new String("");
    String match = new String("findall");
    String pattern;
    int group = -1;
    Pattern p;

    @Override
    public void init(String text, NamedNodeMap attr) {
        super.init(text, attr);
        p = Pattern.compile(_text, Pattern.DOTALL | Pattern.UNICODE_CASE);
        if (null == _attr) {
            return;
        }
        if (_attr.getNamedItem("connector") != null) {
            connector = _attr.getNamedItem("connector").getNodeValue();
        }
        if (_attr.getNamedItem("match") != null) {
            match = _attr.getNamedItem("match").getNodeValue();
        }
        if (_attr.getNamedItem("pattern") != null) {
            pattern = _attr.getNamedItem("pattern").getNodeValue();
        }
        if (_attr.getNamedItem("group") != null) {
            group = Integer.parseInt(_attr.getNamedItem("group").getNodeValue());
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
            if (match.equals("match")) {
                is_match = m.lookingAt();
            } else {
                is_match = m.find();
            }
            if (is_match) {
                String tmp_str = new String("");
                if (group >= 0) {
                    tmp_str = m.group(group);
                } else {
                    for (int i = 1; i <= m.groupCount(); i++) {
                        tmp_str = tmp_str + m.group(i) + connector;
                    }
                }
                parsed_result.add(tmp_str);
            }
        }
        return parsed_result;
    }
}
