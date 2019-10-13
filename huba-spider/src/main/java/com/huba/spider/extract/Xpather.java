package com.huba.spider.extract;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Configuration
@PropertySource(value = {"classpath:application.properties"}, encoding = "UTF-8")
public class Xpather {
    private static final Logger logger = LoggerFactory.getLogger(Xpather.class);
    @Value("${template.template_dir}")
    private String _template_dir;
    @Value(("${template.test_template_dir}"))
    private String _test_template_dir;
    private List<TemplateParser> _test_template_parser_list = null;
    private List<TemplateParser> _template_parser_list = null;

    @PostConstruct
    public void init() {
        load_templates(_template_dir, 1);
        load_templates(_test_template_dir, 0);
    }

    @PreDestroy
    public void destroy() {
    }

    public String getTemplateDir(int online) {
        if (online > 0) {
            return _template_dir;
        } else {
            return _test_template_dir;
        }
    }

    public boolean reload_templates() {
        logger.info("reload templates...");
        boolean result = load_templates(_template_dir, 1);
        boolean test_result = load_templates(_test_template_dir, 0);
        return (result && test_result);
    }

    public boolean load_templates(String dir_path, int online) {
        logger.info("tempate dir path:" + dir_path);
        File file = new File(dir_path);
        if (!file.isDirectory()) {
            try {
                logger.info("template dir not exist,create...");
                return file.mkdirs();
            } catch (Exception e) {
                logger.error("create dir error");
                return false;
            }
        }
        File[] fs = file.listFiles();
        if (fs == null) {
            logger.error("template files empty,online:" + online);
            return false;
        }
        List<TemplateParser> template_parser_list = new ArrayList<>();
        for (File f : fs) {
            if (!f.isDirectory()) {
                String template_path = f.getAbsolutePath();
                TemplateParser template_parser = new TemplateParser(template_path);
                try {
                    boolean init = template_parser.init_template();
                    if (!init) {
                        logger.error("template_parse init error");
                        continue;
                    }
                } catch (Exception e) {
                    logger.error("template_parse init execption");
                    continue;
                }
                template_parser_list.add(template_parser);
            }
        }
        if (template_parser_list.size() < 1) {
            logger.error("load_templates error");
            return false;
        }
        if (online > 0) {
            _template_dir = dir_path;
            _template_parser_list = template_parser_list;
        } else {
            _test_template_dir = dir_path;
            _test_template_parser_list = template_parser_list;
        }
        return true;
    }

    public JSONObject parse(String url, String html, int online) {
        JSONObject result = new JSONObject();
        int struct_info_size = 0;
        List<TemplateParser> template_parser_list;
        if (online > 0) {
            template_parser_list = _template_parser_list;
        } else {
            template_parser_list = _test_template_parser_list;
        }
        if (template_parser_list == null) {
            logger.error("template null error");
            return result;
        }
        if (template_parser_list.size() < 1) {
            logger.error("template empty error");
            return result;
        }
        long start_timestamp = System.currentTimeMillis();
        for (int i = 0; i < template_parser_list.size(); i++) {
            JSONObject data = template_parser_list.get(i).parse(url, html);
            JSONObject struct_info = null;
            if (data.get("content_struct_info") != null) {
                struct_info = (JSONObject) data.get("content_struct_info");
            }
            if (data != null && struct_info != null && struct_info.size() > struct_info_size) {
                result = data;
                struct_info_size = struct_info.size();
            }
        }
        long end_timestamp = System.currentTimeMillis();
        result.put("latency", (end_timestamp - start_timestamp));
        return result;
    }
}
