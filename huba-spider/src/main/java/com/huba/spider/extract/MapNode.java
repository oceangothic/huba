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

public class MapNode extends Node {

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
        int len = children.getLength();
        HashMap<String, String> nodeMap = NodeFactory.getNodeMap();
        for (int i = 0; i < children.getLength(); i++) {
            org.w3c.dom.Node node = children.item(i);
            String nodeName = node.getNodeName();
            if (!nodeMap.containsKey(nodeName) && !nodeName.equals("xpath") && !nodeName.equals("jpath")) {
                continue;
            }
            if (nodeName.equals("match")) {
                org.w3c.dom.Node lastNode = node.getChildNodes().item(1);
                String lastNodeName = lastNode.getNodeName();
                if (lastNodeName.equals("xpath")) {
                    _xpath.add(lastNode.getTextContent());
                    _proto = "xpath";
                    return true;
                } else if (lastNodeName.equals("jpath")) {
                    _jpath.add(lastNode.getTextContent());
                    _proto = "jpath";
                    return true;
                }
            } else if (nodeName.equals("map")) {
                NamedNodeMap attrs = node.getAttributes();
                if (attrs == null) {
                    return false;
                }
                org.w3c.dom.Node domAttr = attrs.getNamedItem("target");
                if (domAttr == null) {
                    logger.error("target is mising,nodeName:" + nodeName);
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
            } else if (nodeName.equals("field")){
                NamedNodeMap attrs = node.getAttributes();
                if (attrs == null) {
                    return false;
                }
                org.w3c.dom.Node domAttr = attrs.getNamedItem("target");
                if (domAttr == null) {
                    logger.error("target is mising,nodeName:" + nodeName);
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
            }else if (nodeName.equals("list")){
                NamedNodeMap attrs = node.getAttributes();
                if (attrs == null) {
                    return false;
                }
                org.w3c.dom.Node domAttr = attrs.getNamedItem("target");
                if (domAttr == null) {
                    logger.error("target is mising,nodeName:" + nodeName);
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
            else {
                return false;
            }
        }
        return true;
    }

    @Override
    public JSONObject process(Object tree, String url) {
        JSONObject rtJson = new JSONObject();
        List<String> paths = new ArrayList<String>();
        JSONObject final_result = new JSONObject();
        if (tree == null) {
            return rtJson;
        }
        if (_xpath.size() > 0) {
            paths = _xpath;
            Object trees = null;
            for (int i = 0; i < paths.size(); i++) {
                XPath xPath = XPathFactory.newInstance().newXPath();
                try {
                    trees = xPath.evaluate(paths.get(i), tree, XPathConstants.NODESET);
                } catch (Exception e) {
                    return rtJson;
                }
                if (trees == null) {
                    logger.error("struct trees null,url:" + url);
                    continue;
                }
            }
        } else if (_jpath.size() > 0) {
            paths = _jpath;
            Object trees = null;
            for (int i = 0; i < paths.size(); i++) {
                trees = JsonPath.read(tree, paths.get(i));
                if (trees == null) {
                    logger.error("struct trees null,url:" + url);
                    continue;
                }
            }
        } else {
            JSONObject sub_result = new JSONObject();
            List<Object> datas = new ArrayList<Object>();
            for (int j = 0; j < _subnodes.size(); j++) {
                JSONObject data = _subnodes.get(j).process(tree, url);
                if (data == null) {
                    logger.error("node process data is null,url:" + url);
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
                            logger.error("exception next:" + e + ",url:" + url);
                            continue;
                        }
                    }
                }
            }
            if (sub_result.size() > 0) {
                datas.add(sub_result);
            }
            Object result = _conv_data_list(datas);
            if (result == null) {
                return final_result;
            }
            final_result.put(_target, result);
        }
        return final_result;
    }
}
