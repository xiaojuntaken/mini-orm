package org.hxj.utils;

import cn.hutool.core.collection.CollectionUtil;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.hxj.entity.common.XmlMethod;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @authorxiaojun
 * @date2024/1/9 22:20
 */
public class XmlUtils {
    private static final String path = "src/main/resources/mapper/";

    public static List<XmlMethod> readXml(String xmlPath) {
        List<XmlMethod> result = new ArrayList<>();
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(new File(path + xmlPath));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = doc.getRootElement();
        System.out.println("获取了根元素:" + root.getName());
        List<Element> list = root.elements();
        //获取所有方法节点并遍历
        for (Element element : list) {
            XmlMethod xmlMethod = new XmlMethod();
            //查询方法是否有子节点
            List<Element> elements = element.elements();
            if (CollectionUtil.isNotEmpty(elements)) {
                for (int i = 0; i < elements.size(); i++) {
                    Element child = elements.get(i);
                    if (child.getName().equals("for")) {
                        String item = child.attribute("item").getText();
                        String textTrim = child.getTextTrim();
                        if (textTrim.startsWith("#")) {
                            String param = "''";
                        }
                        child.setText("%s");
                        System.out.println("for:" + child.getText());
                        System.out.println("end");
                    }
                }
            }
            String methodName = element.getName();
            String sql = element.getStringValue().trim();
            xmlMethod.setMethodName(methodName);
        }
        return result;
    }
}
