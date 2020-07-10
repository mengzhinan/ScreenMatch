package com.duke.screenmatch.dp;

import com.duke.screenmatch.utils.Pair;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * @Author: duke
 * @DateTime: 2016-08-24 17:27
 * @UpdateTime: 2020-07-07 17:54
 * @Description: 解析xml工具类
 */
public class SAXReadHandler extends DefaultHandler implements LexicalHandler {

    private static final boolean DEBUG = false;

    static final String ELEMENT_RESOURCE = "resources";
    static final String ELEMENT_DIMEN = "dimen";
    static final String ELEMENT_ITEM = "item";

    static final String PROPERTY_NAME = "name";
    static final String PROPERTY_TYPE = "type";
    static final String PROPERTY_FORMAT = "format";

    static final String PROPERTY_TYPE_DIMEN = "dimen";

    private ArrayList<XMLItem> list = new ArrayList<>();
    private XMLItem mCurrentTag;
    private String tempElement;
    private boolean inAnyElement;

    public ArrayList<XMLItem> getData() {
        return list;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (DEBUG) System.err.println("startElement [" + qName + "]");
        tempElement = qName;
        inAnyElement = true;
        if (qName != null && qName.trim().length() > 0) {
            if (qName.equals(ELEMENT_RESOURCE)) {
                // empty
            } else if (qName.equals(ELEMENT_DIMEN)) {
                //创建对象
                DimenItem item = new DimenItem();
                if (attributes != null && attributes.getLength() > 0) {
                    item.attr_name = attributes.getValue(PROPERTY_NAME);
                    item.tagName = ELEMENT_DIMEN;
                }
                mCurrentTag = item;
            } else if (qName.equals(ELEMENT_ITEM)) {
                //检查是否是item方式进行的dimen定义
                // <item name="ada" type="dimen" format="dimension">4dp</item>
                String type = attributes.getValue(PROPERTY_TYPE);
                // type is required attr
                if (PROPERTY_TYPE_DIMEN.equals(type)) {
                    ItemDefinedDimenItem item = new ItemDefinedDimenItem();
                    item.attr_name = attributes.getValue(PROPERTY_NAME);
                    item.attr_type = type;
                    item.attr_format = attributes.getValue(PROPERTY_FORMAT);
                    item.tagName = ELEMENT_ITEM;
                    mCurrentTag = item;
                } else {
                    onUnSupportTag(qName, attributes);
                }
            } else {
                onUnSupportTag(qName, attributes);
            }
        }
    }

    private void onUnSupportTag(String tagName, Attributes attributes) {
        // 其他Tag
        OtherTagItem item = new OtherTagItem();
        item.tagName = tagName;
        if (attributes != null) {
            item.attributeList = new ArrayList<>();
            int length = attributes.getLength();
            for (int i = 0; i < length; i++) {
                item.attributeList.add(new Pair<>(
                        attributes.getQName(i),
                        attributes.getValue(i)
                ));
            }
        }
        this.mCurrentTag = item;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (DEBUG) System.err.println("endElement [" + qName + "]");
        if (qName != null && qName.trim().length() > 0) {
            // 结束标签，添加对象到集合
            if (list != null && mCurrentTag != null) {
                list.add(mCurrentTag);
                mCurrentTag = null;
            }
        }
        tempElement = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String temp = new String(ch, start, length);
        if (DEBUG) System.err.println("[" + temp + "]");
        if (tempElement != null && mCurrentTag != null) {
            if (temp.trim().length() > 0) {
                temp = temp.trim();
                /**
                 * 感谢网友提醒，发现偶现的bug，同一处的文本会回调多次
                 */
                if (mCurrentTag instanceof TagItem) {
                    TagItem item = (TagItem) mCurrentTag;
                    if (item.value == null || item.value.trim().length() == 0) {
                        item.value = temp;
                    } else {
                        //内容累加
                        item.value += temp;
                    }
                }
            }
        }
        // 如果是空白行(回车符/空格/TAB)
        else if (temp.replaceAll("\\s", "").isEmpty()){
            PlainTextItem item = new PlainTextItem();
            item.characters = temp;
            list.add(item);
        }
    }

    // implements LexicalHandler to handle xml comments

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
    }

    @Override
    public void endCDATA() throws SAXException {
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        String comment = new String(ch, start, length);
        if (DEBUG) System.err.println("comment [" + comment + "]");

        CommentItem item;
        if (!inAnyElement) {
            item = new DocumentCommentItem();
        } else {
            item = new CommentItem();
        }

        item.characters = comment;
        list.add(item);
    }
}