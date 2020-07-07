package com.duke.screenmatch.dp;

import com.duke.screenmatch.settings.SettingsParams;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.annotation.Nullable;
import javax.xml.transform.sax.TransformerHandler;

/**
 * @Author: duke
 * @DateTime: 2016-08-24 17:32
 * @UpdateTime: 2017-09-29 09:55
 * @Description: dimens文件中的数据项
 */
public class XMLItem {

    /**
     * 将数据写入handler
     */
    public void transfer(TransformerHandler handler,
                         AttributesImpl attributes,
                         SettingsParams params) throws SAXException {

    }

    protected XMLItem newLine(TransformerHandler handler) throws SAXException {
        return characters(handler, "\n");
    }

    protected XMLItem indent(TransformerHandler handler) throws SAXException {
        return characters(handler, "    ");
    }

    protected XMLItem characters(TransformerHandler handler, @Nullable String characters) throws SAXException {
        if (characters == null) return this;

        char[] array = characters.toCharArray();
        handler.characters(array, 0, array.length);
        return this;
    }

    protected XMLItem comment(TransformerHandler handler, String comment) throws SAXException {
        char[] array = comment.toCharArray();
        handler.comment(array, 0, array.length);
        return this;
    }
}
