package com.duke.screenmatch.dp;

import org.xml.sax.helpers.AttributesImpl;

public abstract class NamedTagItem extends TagItem {

    public String attr_name;

    @Override
    protected void fillAttribute(AttributesImpl attributes) {
        super.fillAttribute(attributes);
        attributes.addAttribute("", "", SAXReadHandler.PROPERTY_NAME, "", this.attr_name);
    }
}
