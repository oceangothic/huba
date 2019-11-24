package com.huba.spider.extract;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;

public class XpathRule extends Rule {
    private static final Logger logger = LoggerFactory.getLogger(XpathRule.class);
    XPath xPath;
    XPathExpression expr;

    @Override
    public void init(String text, NamedNodeMap attr) {
        super.init(text, attr);
        xPath = XPathFactory.newInstance().newXPath();
        try {
            expr = xPath.compile(_text);
        } catch (Exception e) {
            logger.error("xpath compile error:" + _text);
        }
    }

    @Override
    public List<Object> process(Object tree, String url) {
        List<Object> datas = new ArrayList<Object>();
        if (_text.equals("@raw_url")) {
            datas.add(url);
            return datas;
        }

        Object result = null;
        try {
            result = expr.evaluate(tree, XPathConstants.NODESET);
            if (result instanceof NodeList) {
                NodeList nodeList = (NodeList) result;
                for (int i = 0; i < nodeList.getLength(); i++) {
                    org.w3c.dom.Node node = nodeList.item(i);
                    if (node == null) {
                        continue;
                    }
                    String data = node.getNodeValue() == null ? node.getTextContent() : node.getNodeValue();
                    datas.add(StringUtils.strip(data));
                }
            } else {
                logger.error("xpath error,url" + url);
            }
        } catch (Exception e) {
            System.out.println("exception:" + e + ",url:" + url);
        }
        if (datas.size() < 1) {
            if (_attr != null) {
                if (_attr.getNamedItem("default_value") != null) {
                    String default_value = _attr.getNamedItem("default_value").getNodeValue();
                    datas.add(default_value);
                }
            }
        }
        return datas;
    }
}
