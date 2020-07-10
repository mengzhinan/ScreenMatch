package com.duke.screenmatch.dp;

import com.duke.screenmatch.settings.SettingsParams;
import com.duke.screenmatch.utils.Pair;
import org.xml.sax.helpers.AttributesImpl;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @Author: duke
 * @DateTime: 2016-08-24 17:32
 * @UpdateTime: 2020-07-07 17:54
 * @Description: dimens文件中的非dimen数据项
 */
public class OtherTagItem extends TagItem {

    // attr name -> attr value
    public List<Pair<String, String>> attributeList;

    @Override
    protected boolean isAutoGenerationItem() {
        return false;
    }

    @Override
    protected void fillAttribute(AttributesImpl attributes) {
        super.fillAttribute(attributes);
        if (attributeList == null) return;

        for (Pair<String, String> attrPair : attributeList) {
            attributes.addAttribute("", "",
                    attrPair.first,
                    "",
                    attrPair.second);
        }
    }

    @Nonnull
    @Override
    protected String getTagName() {
        return tagName;
    }

    @Override
    protected String getTagValue(AttributesImpl attributes, SettingsParams params) {
        return this.value;
    }
}
