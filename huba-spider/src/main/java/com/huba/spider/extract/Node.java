package com.huba.spider.extract;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Node {
    private static final Logger logger = LoggerFactory.getLogger(Node.class);
    protected String _target = null;
    protected NamedNodeMap _attr = null;
    protected String _proto = new String("xpath");
    protected String _json = new String("raw");

    public void init(String target, NamedNodeMap attr) {
        _target = target;
        _attr = attr;
    }

    Node(String target, NamedNodeMap attr) {
        _target = target;
        _attr = attr;
    }

    Node() {

    }

    protected Boolean _load_from_elem(org.w3c.dom.Node elem) {
        return false;
    }

    protected Boolean load_from_elem(org.w3c.dom.Node elem) {
        if (_attr != null) {
            if (_attr.getNamedItem("json") != null) {
                _json = _attr.getNamedItem("json").getNodeValue();
            }
        }
        return _load_from_elem(elem);
    }

    protected Object _conv_data(Object obj, String data_type) {
        try {
            if (obj instanceof String) {
                if (data_type.equals("str")) {
                    return ((String) obj);
                } else if (data_type.equals("int")) {
                    return Integer.parseInt((String) obj);
                } else if (data_type.equals("float")) {
                    return BigDecimal.valueOf(Float.parseFloat((String) obj));
                } else {
                    return obj;
                }
            } else {
                if (data_type.equals("int")) {
                    if (obj instanceof Boolean) {
                        if ((Boolean) obj) {
                            return (Integer) 1;
                        } else {
                            return (Integer) 0;
                        }
                    }
                    return (Integer) obj;
                } else if (data_type.equals("float")) {
                    if (obj instanceof BigDecimal) {
                        return obj;
                    } else {
                        if (obj instanceof Integer) {
                            return BigDecimal.valueOf((float) ((Integer) obj).intValue());
                        } else {
                            return BigDecimal.valueOf((Float) obj);
                        }
                    }
                } else {
                    return obj;
                }
            }
        } catch (Exception e) {
            logger.error("_conv_data execption:" + e);
            return null;
        }
    }

    public String getProto() {
        return _proto;
    }

    public String getJson() {
        return _json;
    }

    public String getTarget() {
        return _target;
    }

    protected Object _conv_data_list(List<Object> datas) {
        String data_type = "str";
        String is_array = "false";
        int limit = -9999;
        int index = -9999;
        if (null == datas) {
            return null;
        }
        if (_attr != null) {
            if (_attr.getNamedItem("data_type") != null) {
                data_type = _attr.getNamedItem("data_type").getNodeValue();
            }
            if (_attr.getNamedItem("is_array") != null) {
                is_array = _attr.getNamedItem("is_array").getNodeValue();
            }
            if (_attr.getNamedItem("limit") != null) {
                limit = Integer.parseInt(_attr.getNamedItem("limit").getNodeValue());
            }
            if (_attr.getNamedItem("index") != null) {
                index = Integer.parseInt(_attr.getNamedItem("index").getNodeValue());
            }
        }
        List<Object> array_result = new ArrayList<Object>();
        Object result = null;
        for (Object obj : datas) {
            Object conv = _conv_data(obj, data_type);
            if (conv != null) {
                array_result.add(conv);
            }
        }
        if (array_result.size() < 1) {
            if (_attr != null) {
                if (_attr.getNamedItem("default_value") != null) {
                    String default_value = _attr.getNamedItem("default_value").getNodeValue();
                    Object conv = _conv_data(default_value, data_type);
                    array_result.add(conv);
                }
            }
        }
        if (limit != -9999 && array_result.size() > 0) {
            if (limit < 0) {
                limit = array_result.size() + limit;
            }
            array_result = new ArrayList<Object>(array_result.subList(0, limit));
        }
        if (index != -9999 && array_result.size() > 0) {
            if (index < 0) {
                index = array_result.size() + index;
            }
            Object elem = array_result.get(index);
            array_result.clear();
            array_result.add(elem);
        }
        if (is_array.equals("false") && array_result.size() > 0) {
            result = array_result.get(0);
        } else {
            if (array_result.size() > 0) {
                result = array_result;
            }
        }
        return result;
    }

    public JSONObject process(Object tree, String url) {

        return null;
    }

    public Object data_process(Object tree, String url) {

        return null;
    }
}
