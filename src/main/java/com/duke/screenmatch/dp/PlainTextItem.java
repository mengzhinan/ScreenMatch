package com.duke.screenmatch.dp;

import com.duke.screenmatch.settings.SettingsParams;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;

/**
 * @Author: duke
 * @DateTime: 2016-08-24 17:32
 * @UpdateTime: 2020-07-07 17:54
 * @Description: dimens文件中的纯文本数据项(换行符/缩进符等等)
 */
public class PlainTextItem extends XMLItem {

    public String characters;

    @Override
    public void transfer(TransformerHandler handler,
                         AttributesImpl attributes,
                         SettingsParams params) throws SAXException {
        if (!params.isKeepSourceCodeComments()) return;

        characters(handler, characters);
    }
}
