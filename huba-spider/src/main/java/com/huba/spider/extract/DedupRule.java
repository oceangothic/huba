package com.huba.spider.extract;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DedupRule extends Rule {
    @Override
    public List<Object> process(List<Object> datas) {
        if (datas == null) {
            return datas;
        }
        List<Object> parsed_result = new ArrayList<Object>();
        Set<String> objSet = new LinkedHashSet<>();
        for (Object data : datas) {
            if (!(data instanceof String)) {
                continue;
            }
            String strObj = StringUtils.strip((String) data);
            if (!objSet.contains(strObj)) {
                parsed_result.add(strObj);
                objSet.add(strObj);
            }
        }
        return parsed_result;
    }
}
