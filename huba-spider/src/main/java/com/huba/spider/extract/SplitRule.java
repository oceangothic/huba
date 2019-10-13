package com.huba.spider.extract;

import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SplitRule extends Rule {
    int index = -1;
    String skip_pattern = null;

    @Override
    public void init(String text, NamedNodeMap attr) {
        super.init(text, attr);
        if (null == _attr) {
            return;
        }
        if (_attr.getNamedItem("index") != null) {
            index = Integer.parseInt(_attr.getNamedItem("index").getNodeValue());
        }
        if (_attr.getNamedItem("not_start_with") != null) {
            skip_pattern = _attr.getNamedItem("not_start_with").getNodeValue();
        }
    }

    @Override
    public List<Object> process(List<Object> datas) {
        List<Object> parsed_result = new ArrayList<Object>();
        for (Object obj : datas) {
            if (!(obj instanceof String)) {
                continue;
            }
            String data = (String) obj;
            String[] slices = data.split(_text);
            List<String> slices_list = new ArrayList<String>();
            for (String slice : slices) {
                if (skip_pattern != null) {
                    if (slice.startsWith(skip_pattern)) {
                        continue;
                    }
                }
                slices_list.add(slice);
            }
            if (slices_list.size() <= index) {
                continue;
            }
            if (index == -1) {
                parsed_result.addAll(slices_list);
            } else {
                parsed_result.add(slices_list.get(index));
            }
        }
        return parsed_result;
    }
}
