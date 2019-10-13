package com.huba.spider.extract;

import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    protected String _text = null;
    protected NamedNodeMap _attr = null;

    public Rule(String text, NamedNodeMap attr) {
        _text = text;
        _attr = attr;
        if (_text == null) {
            _text = "";
        }
    }

    public void init(String text, NamedNodeMap attr) {
        _text = text;
        _attr = attr;
        if (_text == null) {
            _text = "";
        }
    }

    public Rule() {

    }

    protected List<String> _convert_string(List<Object> datas) {
        List<String> str_datas = new ArrayList<String>();
        if (datas == null) {
            return str_datas;
        }
        for (Object data : datas) {
            if (data instanceof String) {
                str_datas.add((String) data);
            }
        }
        return str_datas;
    }

    protected List<Object> _convert_object(List<String> datas) {
        List<Object> obj_datas = new ArrayList<Object>();
        if (datas == null) {
            return obj_datas;
        }
        for (Object data : datas) {
            obj_datas.add(data);
        }
        return obj_datas;
    }

    public List<Object> process(Object tree, String url) {
        return null;
    }

    public List<Object> process(List<Object> datas) {
        return null;
    }
}
