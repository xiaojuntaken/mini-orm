package org.hxj.utils;

import cn.hutool.core.collection.CollectionUtil;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.hxj.entity.common.XmlMethod;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;

/**
 * @authorxiaojun
 * @date2024/1/9 22:20
 */
public class XmlUtils {
    private static final String path = "src/main/resources/mapper/";

    public static Map<String, Element> readXml(String mapperName) {
        Map<String, Element> methodMap = new HashMap<>();
        List<XmlMethod> result = new ArrayList<>();
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            File file = new File(path + mapperName + ".xml");
            if(!file.exists()){
                return null;
            }
            doc = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = doc.getRootElement();
        List<Element> list = root.elements();
        //获取所有方法节点并遍历
        for (Element element : list) {
            String methodName = element.attributeValue("id");
            methodMap.put(methodName,element);
        }
        MysqlUtils.getXmlMethodMap().put(mapperName,methodMap);
        return methodMap;
    }
}
