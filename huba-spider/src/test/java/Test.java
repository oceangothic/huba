import com.alibaba.fastjson.JSONObject;
import com.huba.spider.extract.Xpather;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;

public class Test {
    public static void main(String[] args) {
//        System.out.println("float:"+Float.parseFloat("4"));
//        System.out.println("float:"+Float.parseFloat("8999.00"));
        JSONObject tObj = new JSONObject();
        tObj.put("price1",BigDecimal.valueOf(Float.parseFloat("4")));
        tObj.put("price2",BigDecimal.valueOf(Float.parseFloat("8999.00")));
        tObj.put("price3",Float.parseFloat("8999.01"));
//        System.out.println(tObj.toJSONString());
        Xpather xpather = new Xpather();
        xpather.load_templates("D:\\gitlab-projects\\github\\huba\\huba-spider\\src\\test\\java\\template",1);
        String html;
        File file=new File("D:\\gitlab-projects\\javaPro\\base\\huba-spider\\src\\test\\java\\json\\topic.json");
        //File file=new File("/Users/uc/project_new/youlemei_data_extract/crawler_data_extracter/test/B07DJL15QT.html");
        try{
            FileInputStream in=new FileInputStream(file);
            int size=in.available();
            byte[] buffer=new byte[size];
            in.read(buffer);
            in.close();
            html=new String(buffer);
            // System.out.println("file:"+html);
        }catch(Exception e)
        {
            System.out.println("Exception:"+e);
            return;
        }
        //JSONObject result = xpather.parse("https://www.amazon.in/dp/B07GFP852L?th=1&psc=1",html);
        JSONObject result = xpather.parse("topic/#",html,1);
        if (result != null){
            System.out.println("result:"+result);
        }
    }
}
