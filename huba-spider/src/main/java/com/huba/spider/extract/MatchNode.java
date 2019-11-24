package com.huba.spider.extract;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MatchNode extends Node {
    private static final Logger logger = LoggerFactory.getLogger(MatchNode.class);
    private List<Rule> _rules = new ArrayList<Rule>();

    @Override
    protected Boolean _load_from_elem(org.w3c.dom.Node elem) {
        NodeList children = elem.getChildNodes();
        if (children == null) {
            logger.error("children null");
            return false;
        }
        HashMap<String, String> ruleMap = RuleFactory.getRuleMap();
        boolean is_first = true;
        for (int i = 0; i < children.getLength(); i++) {
            org.w3c.dom.Node node = children.item(i);
            String nodeName = node.getNodeName();
            if (!ruleMap.containsKey(nodeName)) {
                continue;
            }
            if (is_first && !nodeName.equals("xpath") && !nodeName.equals("jpath")) {
                logger.error("first tag error:" + nodeName);
                return false;
            } else if (is_first && nodeName.equals("jpath")) {
                _proto = "jpath";
            }
            is_first = false;
            Rule rule = RuleFactory.createRule(nodeName, node.getTextContent(), node.getAttributes());
            if (rule != null) {
                rule.init(node.getTextContent(), node.getAttributes());
                _rules.add(rule);
            } else {
                logger.error("create rule error:" + nodeName);
            }
        }
        return true;
    }

    @Override
    public JSONObject process(Object tree, String url) {
        List<Object> datas = new ArrayList<Object>();
        Object result;
        JSONObject rtJson = new JSONObject();
        if (tree == null) {
            return rtJson;
        }
        for (Rule rule : _rules) {
            if (rule instanceof XpathRule) {
                datas.addAll(rule.process(tree, url));
            } else if (rule instanceof JPathRule) {
                datas.addAll(rule.process(tree, url));
            } else {
                datas = rule.process(datas);
            }
        }
        result = _conv_data_list(datas);
        if (result == null) {
            return rtJson;
        }
        try {
            rtJson.put(_target, result);
        } catch (Exception e) {
            logger.error("exception:" + e + " url:" + url);
        }
        return rtJson;
    }
}
