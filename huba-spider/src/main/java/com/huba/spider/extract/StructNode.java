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

public class StructNode extends Node {
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
        HashMap<String, String> nodeMap = NodeFactory.getNodeMap();
        boolean is_first = true;
        for (int i = 0; i < children.getLength(); i++) {
            org.w3c.dom.Node node = children.item(i);
            String nodeName = node.getNodeName();
            if (!nodeMap.containsKey(nodeName) && !nodeName.equals("xpath") && !nodeName.equals("jpath")) {
                continue;
            }
            if (is_first) {
                if (nodeName.equals("xpath")) {
                    _xpath.add(node.getTextContent());
                    _proto = "xpath";
                } else if (nodeName.equals("jpath")) {
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
        return true;
    }

    @Override
    public JSONObject process(Object root, String url) {
        JSONObject rtJson = new JSONObject();
        List<String> paths = new ArrayList<String>();
        if (root == null) {
            return rtJson;
        }
        if (_xpath.size() > 0) {
            paths = _xpath;
        } else if (_jpath.size() > 0) {
            paths = _jpath;
        } else {
            logger.error("proto error,url:" + url);
            return rtJson;
        }
        JSONObject final_result = new JSONObject();
        //JSONArray result = new JSONArray();
        List<Object> datas = new ArrayList<Object>();
        XPath xPath = XPathFactory.newInstance().newXPath();
        for (int i = 0; i < paths.size(); i++) {
            Object trees = null;
            if (_proto.equals("jpath")) {
                trees = JsonPath.read(root, paths.get(i));
            } else {
                try {
                    trees = xPath.evaluate(paths.get(i), root, XPathConstants.NODESET);
                } catch (Exception e) {
                    return rtJson;
                }
            }
            if (trees == null) {
                logger.error("struct trees null,url:" + url);
                continue;
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
                                    logger.error("exception:" + e + ",url:" + url);
                                    continue;
                                }
                            }
                        }

                    }
                    if (sub_result.size() > 0) {
                        datas.add(sub_result);
                    }
                }
            } else {
                NodeList nodeList = (NodeList) trees;
                for (int k = 0; k < nodeList.getLength(); k++) {
                    JSONObject sub_result = new JSONObject();
                    for (int j = 0; j < _subnodes.size(); j++) {
                        JSONObject data = _subnodes.get(j).process(nodeList.item(k), url);
                        if (data == null) {
                            logger.error("node process null,url" + url);
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
                        datas.add(sub_result);
                    }
                }
            }
        }
        Object result = _conv_data_list(datas);
        if (result == null) {
            return final_result;
        }
        final_result.put(_target, result);

        return final_result;
    }
}
