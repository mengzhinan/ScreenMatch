package com.duke.screenmatch.dp;

import com.duke.screenmatch.settings.SettingsParams;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.sax.TransformerHandler;

/**
 * @Author: duke
 * @DateTime: 2016-08-24 17:32
 * @UpdateTime: 2020-07-07 17:54
 */
public abstract class TagItem extends XMLItem {

    public String tagName;
    public String value;

    @Override
    public void transfer(TransformerHandler handler, AttributesImpl attributes, SettingsParams params) throws SAXException {
        boolean keepSourceCodeComments = params.isKeepSourceCodeComments();
        if (!isAutoGenerationItem() && !keepSourceCodeComments) return;

        fillAttribute(attributes);

        if (!keepSourceCodeComments) {
            //如果不保留源文件格式,那么需要手动进行换行+缩进
            newLine(handler).indent(handler);
        }

        //开始标签对输出
        handler.startElement("", "", getTagName(), attributes);
        characters(handler, getTagValue(attributes, params));
        handler.endElement("", "", getTagName());
    }

    protected abstract boolean isAutoGenerationItem();

    protected void fillAttribute(AttributesImpl attributes) {

    }

    @Nonnull
    protected abstract String getTagName();

    @Nullable
    protected abstract String getTagValue(AttributesImpl attributes, SettingsParams params);
}
