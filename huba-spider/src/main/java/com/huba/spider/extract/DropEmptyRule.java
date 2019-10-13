package com.huba.spider.extract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropEmptyRule extends Rule {
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
            if (((String) data).equals("\n") || ((String) data).equals(" ") || ((String) data).equals("\t") || (((String) data).length() <= 0)) {
                continue;
            }
            parsed_result.add(data);
        }
        return parsed_result;
    }
}
