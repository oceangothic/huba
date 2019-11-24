package com.huba.spider.extract;

import com.alibaba.fastjson.JSONArray;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapNode extends Node {

    private static final Logger logger = LoggerFactory.getLogger(StructNode.class);
    private List<String> _xpath = new ArrayList<String>();
    private List<String> _jpath = new ArrayList<String>();
    private List<List<org.w3c.dom.Node>> _linkpath = new ArrayList<>();
    private LinkedHashMap<Integer, List<List<org.w3c.dom.Node>>> layerMap = new LinkedHashMap<>();
    private String _tag = null;

    public static boolean loop(org.w3c.dom.Node node, List<org.w3c.dom.Node> values) {
        if (node == null) {
            return false;
        }
        NodeList subNodeList = node.getChildNodes();
        if (subNodeList.getLength() == 0) {
            if ("#text".equals(node.getNodeName()) && !values.contains(node.getParentNode())) {
                values.add(node.getParentNode());
                return true;
            }
            return false;
        }
        for (int i = 0; i < subNodeList.getLength(); i++) {
            org.w3c.dom.Node curNode = subNodeList.item(i);
            loop(curNode, values);
        }
        return true;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = pattern.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    @Override
    protected Boolean _load_from_elem(org.w3c.dom.Node elem) {
        NodeList children = elem.getChildNodes();
        if (children == null) {
            logger.error("children null");
            return false;
        }
        _proto = elem.getAttributes().getNamedItem("proto").getNodeValue();
        _tag = elem.getNodeName();
        for (int i = 0; i < children.getLength(); i++) {
            org.w3c.dom.Node node = children.item(i);
            String nodeName = node.getNodeName();
            if (!nodeName.equals("map") && !nodeName.equals("list") && !nodeName.equals("field")) {
                continue;
            }
            List<org.w3c.dom.Node> curPath = new ArrayList<>();
            curPath.add(elem);
            if (loop(node, curPath)) {
                _linkpath.add(curPath);
            }
            System.out.println(curPath.size());
        }

        for (List<org.w3c.dom.Node> list : _linkpath) {
            if (layerMap.get(list.size()) == null) {
                List<List<org.w3c.dom.Node>> tempList = new ArrayList<>();
                tempList.add(list);
                layerMap.put(list.size(), tempList);
            } else {
                layerMap.get(list.size()).add(list);
            }
        }
        return true;
    }

    private boolean parseLoop(JSONObject object, String target, String tag) {
        if (!object.containsKey(target)) {
            return false;
        }
        if ("map".equals(tag)) {
            JSONObject tempObj = new JSONObject();
            object.put(target, tempObj);
        } else if ("list".equals(tag)) {
            JSONArray tempArray = new JSONArray();
            object.put(target, tempArray);
        } else if ("field".equals(tag)) {
            object.put(target, "");
        }
        return true;
    }

    private JSONObject parseLayer(Object input) {
        JSONObject jsonObject = new JSONObject();
        int index = 0;
        for (Map.Entry<Integer, List<List<org.w3c.dom.Node>>> entry : layerMap.entrySet()) {
            if (index > 0) {
                int max = index + 1;
                for (List<org.w3c.dom.Node> nodeList : entry.getValue()) {
                    StringBuffer targetPath = new StringBuffer();
                    String endPath = nodeList.get(nodeList.size() - 1).getFirstChild().getTextContent();
                    while (max >= 0) {
                        org.w3c.dom.Node node = nodeList.get(max);
                        String tag = node.getNodeName();
                        String target = node.getAttributes().getNamedItem("target").getNodeValue();
                        targetPath.append(target);
                        if (parseLoop(jsonObject, target, tag)) {

                        }
                        max -= 1;
                    }
                }
            }
            index += 1;
        }
        return jsonObject;
    }

    private Object parse(Object tree, String path) {
        Object trees = null;
        if (_proto.equals("xpath")) {
            XPath xPath = XPathFactory.newInstance().newXPath();
            try {
                trees = xPath.evaluate(path, tree, XPathConstants.NODESET);
            } catch (Exception e) {
                return null;
            }

        } else if (_proto.equals("jpath")) {
            trees = JsonPath.read(tree, path);
        }
        return trees;
    }

    @Override
    public JSONObject process(Object tree, String url) {
        JSONObject mapObject = new JSONObject();
        JSONArray listObject = new JSONArray();
        JSONObject final_result = new JSONObject();
        if (tree == null) {
            return final_result;
        }
        if ("map".equals(_tag)) {
            final_result = parseLayer(tree);
        }
        //===========================================cut line=============================================
        JSONObject sub_result = new JSONObject();
        List<Object> datas = new ArrayList<Object>();

        if (sub_result.size() > 0) {
            datas.add(sub_result);
        }
        Object result = _conv_data_list(datas);
        if (result == null) {
            return final_result;
        }
        final_result.put(_target, result);
        return final_result;
    }
}
