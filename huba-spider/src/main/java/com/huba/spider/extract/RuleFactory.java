package com.huba.spider.extract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;

import java.util.HashMap;

public class RuleFactory {
    private static final Logger logger = LoggerFactory.getLogger(RuleFactory.class);
    private static final HashMap<String, String> map = new HashMap<String, String>() {
        {
            put("xpath", "com.huba.spider.extract.XpathRule");
            put("jpath", "com.huba.spider.extract.JPathRule");
            put("regex", "com.huba.spider.extract.RegRule");
            put("prefix", "com.huba.spider.extract.PrefixRule");
            put("suffix", "com.huba.spider.extract.SuffixRule");
            put("split", "com.huba.spider.extract.SplitRule");
            put("connect", "com.huba.spider.extract.ConnectRule");
            put("mapping", "com.huba.spider.extract.MappingRule");
            put("replace", "com.huba.spider.extract.ReplaceRule");
            put("drop_reg", "com.huba.spider.extract.DropRegRule");
            put("length", "com.huba.spider.extract.LengthRule");
            put("drop_empty", "com.huba.spider.extract.DropEmptyRule");
            put("strip", "com.huba.spider.extract.StripRule");
            put("unescape_xml", "com.huba.spider.extract.UnescapeXmlRule");
            put("url_decode", "com.huba.spider.extract.URLDecodeRule");
            put("exist", "com.huba.spider.extract.ExistRule");
            put("reg_exist", "com.huba.spider.extract.RegExistRule");
            put("reg_incr", "com.huba.spider.extract.RegIncRule");
            put("unescape_html", "com.huba.spider.extract.UnescapeHtmlRule");
            put("reg_try", "com.huba.spider.extract.RegTryRule");
            put("dedup", "com.huba.spider.extract.DedupRule");
        }
    };

    public static Rule createRule(String ruleName, String text, NamedNodeMap attr) {
        if (map.containsKey(ruleName)) {
            try {
                return (Rule) Class.forName((String) map.get(ruleName)).newInstance();
            } catch (Exception e) {
                logger.error("not find rule class:" + ruleName);
                return null;
            }
        } else {
            logger.error("not find rule name:" + ruleName);
            return null;
        }
    }

    public RuleFactory() {

    }

    public static HashMap<String, String> getRuleMap() {
        return map;
    }
}
