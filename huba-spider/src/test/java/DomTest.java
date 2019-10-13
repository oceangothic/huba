import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomTest {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String xmlPath = "D:\\gitlab-projects\\javaPro\\base\\huba-spider\\src\\test\\java\\template\\topic.xml";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(xmlPath);
        System.out.println("parse xml:" + xmlPath);
        NodeList nodeList = document.getElementsByTagName("template");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node curNode = nodeList.item(i);
            List<String> endList = loop(curNode);
            for (String str : endList) {
                System.out.println(str);
            }
            System.out.println(endList.size());
        }
//        Node templateNode = nodeList.item(0);
//        System.out.println(templateNode.getChildNodes().item(1).getChildNodes().item(0).getChildNodes().getLength());
//        NodeList nodeList1 = templateNode.getChildNodes();
//        System.out.println(nodeList1.item(1).getChildNodes().item(1).getTextContent());
    }

    public static boolean judgeNode(org.w3c.dom.Node node) {
        NodeList subNodeList = node.getChildNodes();
        if (subNodeList == null) {
            return false;
        }
        return true;
    }

    public static List<String> loop(org.w3c.dom.Node node) {
        List<String> values = new ArrayList<>();
        NodeList subNodeList = node.getChildNodes();
        if (subNodeList.getLength() == 0) {
            if (!replaceBlank(node.getTextContent()).equals("")) {
                values.add(replaceBlank(node.getTextContent()));
            }
            return values;
        }
        for (int i = 0; i < subNodeList.getLength(); i++) {
            Node curNode = subNodeList.item(i);
            values.addAll(loop(curNode));
        }
        return values;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}
