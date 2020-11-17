package com.duke.screenmatch.dp;

import com.duke.screenmatch.settings.SettingsParams;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * dimens处理
 *
 * @UpdateTime: 2020-07-07 17:54
 */
public class XmlIO {

    /**
     * 解析dimens文件
     *
     * @param baseDimenFilePath 源dimens文件路径
     */
    public static ArrayList<XMLItem> readDimenFile(String baseDimenFilePath) {
        ArrayList<XMLItem> list = null;
        try {
            InputStream inputStream = new FileInputStream(baseDimenFilePath);
            SAXReadHandler saxReadHandler = new SAXReadHandler();

            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", saxReadHandler);
            xmlReader.setContentHandler(saxReadHandler);
            xmlReader.parse(new InputSource(inputStream));

            list = saxReadHandler.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 解析dimens文件
     *
     * @param baseDimenFilePath 源dimens文件路径
     */
    public static ArrayList<XMLItem> readDimenFile(VirtualFile baseDimenFilePath) {
        ArrayList<XMLItem> list = null;
        try {
            SAXReadHandler saxReadHandler = new SAXReadHandler();

            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", saxReadHandler);
            xmlReader.setContentHandler(saxReadHandler);
            xmlReader.parse(new InputSource(baseDimenFilePath.getInputStream()));

            list = saxReadHandler.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 生成dimens文件
     *
     * @param params 设置参数
     * @param list        源dimens数据
     * @param multiple    对应新文件需要乘以的系数
     * @param outPutFile  目标文件输出目录
     */
    public static void createDestinationDimens(Project project,
                                               SettingsParams params,
                                               List<XMLItem> list,
                                               double multiple,
                                               VirtualFile outPutFile) {
        try {
            params = params.newBuilder()
                    .setMultipleForDpi(multiple)
                    .build();

            //创建SAXTransformerFactory实例
            SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
            //创建TransformerHandler实例
            TransformerHandler handler = saxTransformerFactory.newTransformerHandler();
            //创建Transformer实例
            Transformer transformer = handler.getTransformer();
            //是否自动添加额外的空白
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //设置字符编码
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            //添加xml版本，默认也是1.0
            transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            //保存xml路径
            StreamResult result = new StreamResult(outPutFile.getOutputStream(project));
            handler.setResult(result);
            //创建属性Attribute对象
            AttributesImpl attributes = new AttributesImpl();
            //开始xml
            handler.startDocument();
            //换行
            handler.characters("\n".toCharArray(), 0, "\n".length());

            // 保留文档注释
            List<XMLItem> documentComments = list.stream()
                    .filter(xmlItem -> xmlItem instanceof DocumentCommentItem)
                    .collect(Collectors.toList());
            for (XMLItem documentComment : documentComments) {
                documentComment.transfer(handler, attributes, params);
            }

            //写入根节点resources
            handler.startElement("", "", SAXReadHandler.ELEMENT_RESOURCE, attributes);

            // 过滤文档注释
            list.removeAll(documentComments);

            // 处理数据或者注释节点
            for (XMLItem xmlItem : list) {
                attributes.clear();
                xmlItem.transfer(handler, attributes, params);
            }
            handler.endElement("", "", SAXReadHandler.ELEMENT_RESOURCE);
            handler.endDocument();
            result.getOutputStream().close();
            System.out.println(">>>>> " + outPutFile + " 文件生成完成!");
        } catch (Exception e) {
            System.out.println("DK WARNING: " + outPutFile + " 文件生成失败!");
            e.printStackTrace();
        }
    }
}
