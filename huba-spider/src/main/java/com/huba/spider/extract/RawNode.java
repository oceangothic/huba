package com.huba.spider.extract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RawNode extends Node {
    private static final Logger logger = LoggerFactory.getLogger(RawNode.class);
    private List<Rule> _rules = new ArrayList<Rule>();

    @Override
    protected Boolean _load_from_elem(org.w3c.dom.Node elem) {
        NodeList children = elem.getChildNodes();
        if (children == null) {
            logger.error("children null");
            return false;
        }
        HashMap<String, String> ruleMap = RuleFactory.getRuleMap();
        for (int i = 0; i < children.getLength(); i++) {
            org.w3c.dom.Node node = children.item(i);
            String nodeName = node.getNodeName();
            if (!ruleMap.containsKey(nodeName)) {
                continue;
            }
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
    public Object data_process(Object tree, String url) {
        Object result;
        List<Object> datas = new ArrayList<Object>();
        if (tree == null) {
            return null;
        }
        datas.add(tree);
        for (Rule rule : _rules) {
            if (rule instanceof XpathRule) {
                return null;
            } else if (rule instanceof JPathRule) {
                return null;
            } else {
                datas = rule.process(datas);
            }
        }
        result = _conv_data_list(datas);
        if (result == null) {
            return null;
        }
        return result;
    }
}
