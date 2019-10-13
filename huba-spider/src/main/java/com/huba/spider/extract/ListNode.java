package com.huba.spider.extract;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.*;

public class ListNode extends Node {

    private static final Logger logger = LoggerFactory.getLogger(StructNode.class);
    private List<Node> _subnodes = new ArrayList<Node>();
    private List<String> _xpath = new ArrayList<String>();
    private List<String> _jpath = new ArrayList<String>();

    @Override
    protected Boolean _load_from_elem(org.w3c.dom.Node elem) {
        NodeList children = elem.getChildNodes();
        if (children == null) {
            logger.error("children null");
            return false;
        }

        return true;
    }

    @Override
    public JSONObject process(Object tree, String url) {
        JSONObject rtJson = new JSONObject();
        List<String> paths = new ArrayList<String>();
        JSONObject final_result = new JSONObject();

        return final_result;
    }
}
