package com.duke.screenmatch.dp;

import com.sun.istack.Nullable;
import org.xml.sax.helpers.AttributesImpl;

import javax.annotation.Nonnull;

/**
 * @Author: duke
 * @DateTime: 2016-08-24 17:32
 * @UpdateTime: 2020-07-07 17:54
 * @Description: dimens文件中的item定义的dimen数据项
 * <item name="ada" type="dimen" format="dimension">4dp</item>
 */
public class ItemDefinedDimenItem extends DimenItem {

    public String attr_type;

    @Nullable
    public String attr_format;

    @Override
    protected void fillAttribute(AttributesImpl attributes) {
        super.fillAttribute(attributes);
        attributes.addAttribute("", "", SAXReadHandler.PROPERTY_TYPE, "", this.attr_type);
        if (attr_format != null && !attr_format.isEmpty()) {
            attributes.addAttribute("", "", SAXReadHandler.PROPERTY_FORMAT, "", this.attr_format);
        }
    }

    @Nonnull
    @Override
    protected String getTagName() {
        return SAXReadHandler.ELEMENT_ITEM;
    }
}
