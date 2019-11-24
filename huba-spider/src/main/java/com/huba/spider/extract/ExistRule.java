package com.huba.spider.extract;

import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;

public class ExistRule extends Rule {
    String map2 = new String("1");
    ;

    @Override
    public void init(String text, NamedNodeMap attr) {
        if (_attr == null) {
            return;
        }
        if (_attr.getNamedItem("map2") != null) {
            map2 = _attr.getNamedItem("map2").getNodeValue();
        }
    }

    @Override
    public List<Object> process(List<Object> datas) {
        List<Object> parsed_result = new ArrayList<Object>();
        if (null != datas && datas.size() > 0) {
            parsed_result.add(map2);
        }
        return parsed_result;
    }
}
