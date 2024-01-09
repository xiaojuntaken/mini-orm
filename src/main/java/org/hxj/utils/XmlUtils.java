package org.hxj.utils;

import cn.hutool.core.collection.CollectionUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * @authorxiaojun
 * @date2024/1/9 22:20
 */
public class XmlUtils {
    private static final String path = "src/main/resources/mapper/";

    public static List<Object> readXml(String xmlPath) {
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
        for (Element element : list) {
            String text = element.getText().trim();
            List<Element> elements = element.elements();
            if (CollectionUtil.isNotEmpty(elements)) {
                for (Element child : elements) {
                    if (child.getName().equals("for")) {
                        String item = child.attribute("item").getText();
                        System.out.println("for:" + child.getText());
                        System.out.println("end");
                    }
                }
            }
            System.out.println("text:" + text);
        }
        return null;
    }
}
