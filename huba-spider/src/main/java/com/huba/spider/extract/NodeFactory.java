package com.huba.spider.extract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;

import java.util.HashMap;
import java.util.Map;

public class NodeFactory {
    private static final Logger logger = LoggerFactory.getLogger(RuleFactory.class);
    private static final HashMap<String, String> map = new HashMap<String, String>() {
        {
            put("match", "com.huba.spider.extract.MatchNode");
            put("struct", "com.huba.spider.extract.StructNode");
            put("dict", "com.huba.spider.extract.DictNode");
            put("raw", "com.huba.spider.extract.RawNode");
            put("map", "com.huba.spider.extract.MapNode");
            put("list", "com.huba.spider.extract.ListNode");
            put("field", "com.huba.spider.extract.FieldNode");
        }
    };

    public static Node createNode(String nodeName, String target, NamedNodeMap attr) {
        if (map.containsKey(nodeName)) {
            try {
                return (Node) Class.forName((String) map.get(nodeName)).newInstance();
            } catch (Exception e) {
                logger.error("not find node class:" + nodeName);
                return null;
            }
        } else {
            logger.error("not find node name:" + nodeName);
            return null;
        }
    }

    public NodeFactory() {

    }

    public static HashMap<String, String> getNodeMap() {
        return map;
    }
}
