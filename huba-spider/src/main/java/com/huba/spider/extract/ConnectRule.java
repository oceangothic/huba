package com.huba.spider.extract;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;

public class ConnectRule extends Rule {
    @Override
    public void init(String text, NamedNodeMap attr) {
        super.init(text, attr);
        _text = _text.replace("\\n", "\n");
    }

    @Override
    public List<Object> process(List<Object> raw_datas) {
        if (raw_datas == null || raw_datas.size() < 1) {
            return raw_datas;
        }
        List<Object> parsed_result = new ArrayList<Object>();
        List<String> datas = _convert_string(raw_datas);
        String result = StringUtils.join(datas, _text);
        parsed_result.add(result);
        return parsed_result;
    }
}
