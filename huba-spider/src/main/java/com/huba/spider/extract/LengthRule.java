package com.huba.spider.extract;

import java.util.ArrayList;
import java.util.List;

public class LengthRule extends Rule {
    @Override
    public List<Object> process(List<Object> datas) {
        if (datas == null) {
            return datas;
        }
        List<Object> parsed_result = new ArrayList<Object>();
        parsed_result.add(String.valueOf(datas.size()));
        return parsed_result;
    }
}
