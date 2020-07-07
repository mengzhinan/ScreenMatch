package com.duke.screenmatch.dp;

import com.duke.screenmatch.settings.SettingsParams;
import org.xml.sax.helpers.AttributesImpl;

import javax.annotation.Nonnull;

/**
 * @Author: duke
 * @DateTime: 2016-08-24 17:32
 * @UpdateTime: 2020-07-07 17:54
 * @Description: dimens文件中的dimen数据项
 * <dimen name="name">18dp</dimen>
 */
public class DimenItem extends NamedTagItem {

    @Override
    protected void fillAttribute(AttributesImpl attributes) {
        super.fillAttribute(attributes);
        attributes.addAttribute("", "", SAXReadHandler.PROPERTY_NAME, "", this.attr_name);
    }

    @Override
    protected boolean isAutoGenerationItem() {
        return true;
    }

    @Nonnull
    @Override
    protected String getTagName() {
        return SAXReadHandler.ELEMENT_DIMEN;
    }

    @Nonnull
    @Override
    protected String getTagValue(AttributesImpl attributes, SettingsParams params) {
        //乘以系数，加上后缀
        return Tools.countValue(params.isFontMatch(), this.value, params.getMultipleForDpi());
    }
}
