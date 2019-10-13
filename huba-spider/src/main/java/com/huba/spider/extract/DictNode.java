package com.huba.spider.extract;

import com.jayway.jsonpath.JsonPath;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.*;

public class DictNode extends Node {
    private static final Logger logger = LoggerFactory.getLogger(DictNode.class);
    private List<Node> _subnodes = new ArrayList<Node>();
    private List<String> _xpath = new ArrayList<String>();
    private List<String> _jpath = new ArrayList<String>();
    private String _key = new String();

    @Override
    protected Boolean _load_from_elem(org.w3c.dom.Node elem) {
        NamedNodeMap root_attrs = elem.getAttributes();
        if (root_attrs == null) {
            return false;
        }
        org.w3c.dom.Node rootAttr = root_attrs.getNamedItem("key");
        if (rootAttr != null) {
            _key = rootAttr.getNodeValue();
        }
        NodeList children = elem.getChildNodes();
        if (children == null) {
            logger.error("children null");
            return false;
        }
        HashMap<String, String> nodeMap = NodeFactory.getNodeMap();
        boolean is_first = true;
        for (int i = 0; i < children.getLength(); i++) {
            org.w3c.dom.Node node = children.item(i);
            String nodeName = node.getNodeName();
            if (!nodeMap.containsKey(nodeName) && !nodeName.equals("jpath")) {
                continue;
            }
            if (is_first) {
                if (nodeName.equals("jpath")) {
                    _jpath.add(node.getTextContent());
                    _proto = "jpath";
                } else {
                    logger.error("struct first node error:" + nodeName);
                    return false;
                }
                is_first = false;
                continue;
            }
            NamedNodeMap attrs = node.getAttributes();
            if (attrs == null) {
                return false;
            }
            org.w3c.dom.Node domAttr = attrs.getNamedItem("target");
            if (domAttr == null) {
                logger.error("target is mising,nodename:" + nodeName);
                return false;
            }
            String target = domAttr.getNodeValue();
            if (!nodeMap.containsKey(nodeName)) {
                logger.error("unknown node:" + nodeName);
                return false;
            }
            Node subNode = NodeFactory.createNode(nodeName, target, attrs);
            subNode.init(target, attrs);
            if (!subNode.load_from_elem(node)) {
                logger.error("node init error:" + nodeName);
                return false;
            }
            _subnodes.add(subNode);
        }
        return true;
    }

    @Override
    public JSONObject process(Object root, String url) {
        JSONObject rtJson = new JSONObject();
        List<String> paths = new ArrayList<String>();
        if (_jpath.size() > 0) {
            paths = _jpath;
        } else {
            logger.error("proto error,url:" + url);
            return rtJson;
        }
        if (root == null) {
            return rtJson;
        }
        JSONObject final_result = new JSONObject();
        JSONArray result = new JSONArray();
        for (int i = 0; i < paths.size(); i++) {
            Object trees = null;
            if (_proto.equals("jpath")) {
                trees = JsonPath.read(root, paths.get(i));
            }
            if (trees == null) {
                logger.error("struct trees null,url:" + url);
                return rtJson;
            }
            if (trees instanceof List) {
                List<Object> objtrees = (List<Object>) trees;
                for (Object obj : objtrees) {
                    JSONObject sub_result = new JSONObject();
                    for (int j = 0; j < _subnodes.size(); j++) {
                        JSONObject data = _subnodes.get(j).process(obj, url);
                        if (data == null) {
                            logger.error("node process null,url:" + url);
                            continue;
                        }
                        Set<String> keySet = data.keySet();
                        Iterator<String> iter = keySet.iterator();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            if (!sub_result.containsKey(key)) {
                                try {
                                    sub_result.put(key, data.get(key));
                                } catch (Exception e) {
                                    logger.error("exception:" + e + " url:" + url);
                                    continue;
                                }
                            }
                        }

                    }
                    if (sub_result.size() > 0) {
                        result.add(sub_result);
                    }
                }
            } else {
                logger.error("dict node other type,url:" + url);
            }
        }
        if (result.size() > 0) {
            final_result.put(_target, result);
        }
        return final_result;
    }
}
