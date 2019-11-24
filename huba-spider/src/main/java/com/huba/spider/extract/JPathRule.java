package com.huba.spider.extract;

import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;

public class JPathRule extends Rule {
    JsonPath jsonpath;
    private static final Logger logger = LoggerFactory.getLogger(JPathRule.class);

    @Override
    public void init(String text, NamedNodeMap attr) {
        super.init(text, attr);
        jsonpath = JsonPath.compile(_text);
    }

    @Override
    public List<Object> process(Object tree, String url) {
        List<Object> datas = new ArrayList<Object>();
        if (_text.equals("@raw_url")) {
            datas.add(url);
            return datas;
        }
        Object result = null;
        try {
            result = jsonpath.read(tree);
            if (result != null) {
                if (result instanceof List) {
                    List<Object> objList = (List<Object>) result;
                    for (Object obj : objList) {
                        if (null == obj) {
                            continue;
                        }
                        datas.add(obj);
                    }
                } else {
                    datas.add(result);
                }
            }
        } catch (Exception e) {
            logger.error("json_path exception:" + e + ",url:" + url);
        }
        if (datas.size() < 1) {
            if (_attr != null) {
                if (_attr.getNamedItem("default_value") != null) {
                    String default_value = _attr.getNamedItem("default_value").getNodeValue();
                    datas.add(default_value);
                }
            }
        }
        return datas;
    }
}
