package com.huba.spider.extract;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StripRule extends Rule {
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
            parsed_result.add(StringUtils.strip((String) data));
        }
        return parsed_result;
    }
}
