package com.huba.spider.extract;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TemplateParser {
    private static final Logger logger = LoggerFactory.getLogger(TemplateParser.class);
    private List<Node> _nodes;
    private List<Node> _rawnodes;
    private List<String> _url_pattern;
    private String _encoding;
    private String _template_path;
    private String _template_name;
    private HashMap<String, String> _nodeMap;
    List<Pattern> _pattern;

    public TemplateParser(String template_path) {
        _template_path = template_path;
        _nodes = new ArrayList<Node>();
        _rawnodes = new ArrayList<Node>();
        _nodeMap = NodeFactory.getNodeMap();
    }

    public boolean init_template() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(_template_path);
            logger.info("parse xml:" + _template_path);
            NodeList nodeList = document.getElementsByTagName("url_pattern");
            if (nodeList != null) {
                _url_pattern = new ArrayList<>();
                _pattern = new ArrayList<>();
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    String url_pattern = nodeList.item(i).getTextContent();
                    logger.info("url pattern:" + url_pattern);
                    Pattern pattern = Pattern.compile(url_pattern, Pattern.DOTALL | Pattern.UNICODE_CASE);
                    _url_pattern.add(url_pattern);
                    _pattern.add(pattern);
                }
            } else {
                return false;
            }
            nodeList = document.getElementsByTagName("encoding");
            if (nodeList != null) {
                _encoding = nodeList.item(0).getTextContent();
            } else {
                _encoding = "utf8";
            }
            org.w3c.dom.Node templateNode;
            nodeList = document.getElementsByTagName("template");
            if (nodeList != null) {
                templateNode = nodeList.item(0);
            } else {
                return false;
            }
            String[] file_path = _template_path.trim().split("/");
            String template_name = new String("");
            if (file_path.length > 0) {
                _template_name = file_path[file_path.length - 1];
            } else {
                _template_name = new String("");
            }
            NodeList children = templateNode.getChildNodes();
            for (int k = 0; k < children.getLength(); k++) {
                org.w3c.dom.Node domNode = children.item(k);
                if (_nodeMap.containsKey(domNode.getNodeName())) {
                    NamedNodeMap attrs = domNode.getAttributes();
                    if (attrs == null) {
                        return false;
                    }
                    org.w3c.dom.Node domAttr = attrs.getNamedItem("target");
                    if (domAttr == null) {
                        logger.error("target is mising");
                        return false;
                    }
                    String target = domAttr.getNodeValue();
                    String tag = domNode.getNodeName();
                    Node node = NodeFactory.createNode(tag, target, attrs);
                    if (node == null) {
                        logger.error("create node error");
                        continue;
                    }
                    node.init(target, attrs);
                    if (node.load_from_elem(domNode)) {
                        if (tag.equals("raw")) {
                            _rawnodes.add(node);
                        } else {
                            _nodes.add(node);
                        }
                    } else {
                        logger.error("node init error");
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("template parse init execption:" + e + " template_path:" + _template_path);
            return false;
        }
    }

    public JSONObject parse(String url, String html) {
        JSONObject result = new JSONObject();
        JSONObject struct_info = new JSONObject();
        boolean is_pattern = false;
        for (Pattern pattern : _pattern) {
            Matcher m = pattern.matcher(url);
            if (m.find()) {
                is_pattern = true;
                break;
            }
        }
        if (!is_pattern) {
            return result;
        }
        logger.info("url pattern template_name:" + _template_name + ",url:" + url);
        result.put("template_name", _template_name);
        result.put("url_prefix", _url_pattern);
        Document dom = null;
        JSONObject jsonRoot = null;
        HashMap<String, JSONObject> jsonMap = new HashMap<>();
        // 优先处理JSON字符串
        if (_rawnodes.size() > 0) {
            try {
                for (int k = 0; k < _rawnodes.size(); k++) {
                    Object tmp_result = html;
                    tmp_result = _rawnodes.get(k).data_process(tmp_result, url);
                    if (tmp_result == null) {
                        logger.error("raw node process error,template_name:" + _template_name + ",url:" + url + ",nodename:" + _rawnodes.get(k).getTarget());
                        continue;
                    }
                    try {
                        jsonRoot = JSONObject.parseObject((String) tmp_result);
                        jsonMap.put(_rawnodes.get(k).getTarget(), jsonRoot);
                    } catch (Exception e) {
                        if (null != tmp_result && tmp_result instanceof String) {
                            logger.error("parse json error,e:" + e + " template:" + _template_name + ",url:" + url + "json_html:" + tmp_result);
                        } else {
                            logger.error("parse json exception:" + e + " template:" + _template_name + ",url:" + url);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("rawnodes execption:" + e + " template_name:" + _template_name + " url:" + url);
                return result;
            }
        }
        if (html.contains("<html")) {
            try {
                HtmlCleaner hc = new HtmlCleaner();
                TagNode tn = hc.clean(html);
                dom = new DomSerializer(new CleanerProperties()).createDOM(tn);
            } catch (Exception e) {
                logger.error("exception:" + e + " template_name:" + _template_name + ",url:" + url);
                return result;
            }
        }
        boolean try_input_json = false;
        for (Node node : _nodes) {
            JSONObject data = new JSONObject();
            if (node.getProto().equals("xpath")) {
                data = node.process(dom, url);
            } else {
                String json_target = node.getJson();
                if (!jsonMap.containsKey(json_target)) {
                    if (try_input_json) {
                        continue;
                    }
                    try {
                        JSONObject obj = JSONObject.parseObject(StringEscapeUtils.unescapeJava(html));
                        jsonMap.put(json_target, obj);
                    } catch (Exception e) {
                        logger.error("input not json,but not found json target");
                        try_input_json = true;
                        continue;
                    }
                }
                data = node.process(jsonMap.get(json_target), url);
            }
            if (data == null) {
                continue;
            }
            Set<String> keySet = data.keySet();
            Iterator<String> iter = keySet.iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                if (!struct_info.containsKey(key)) {
                    try {
                        struct_info.put(key, data.get(key));
                    } catch (Exception e) {
                        logger.error("exception:" + e);
                        continue;
                    }
                }
            }

        }
        if (struct_info.size() > 0) {
            result.put("content_struct_info", struct_info);
        }
        return result;
    }
}
